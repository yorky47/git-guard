package com.gitguard.risk

import com.gitguard.model.CopilotResponse
import com.gitguard.model.RiskLevel

/**
 * Classifies the risk level of Git commands based on their potential for data loss.
 *
 * This classifier is LANGUAGE-AGNOSTIC - it analyzes the actual Git command syntax,
 * not the natural language explanation. This ensures it works regardless of the
 * language used to interact with GitHub Copilot CLI.
 */
class RiskClassifier {

    /**
     * Git command patterns that indicate HIGH RISK operations.
     * These patterns are based on Git's command-line syntax, which is always in English.
     */
    private val destructiveCommandPatterns = listOf(
        // CRITICAL: Hard resets - permanent data loss
        DestructivePattern(
            pattern = "reset --hard",
            severity = Severity.CRITICAL,
            reason = "Permanently deletes all uncommitted changes"
        ),
        DestructivePattern(
            pattern = "reset.*--hard",
            severity = Severity.CRITICAL,
            reason = "Hard reset variant",
            isRegex = true
        ),

        // CRITICAL: Checkout/restore discard changes
        DestructivePattern(
            pattern = "checkout \\.",
            severity = Severity.CRITICAL,
            reason = "Discards all uncommitted changes in working directory",
            isRegex = true
        ),
        DestructivePattern(
            pattern = "checkout -- \\.",
            severity = Severity.CRITICAL,
            reason = "Discards all uncommitted changes",
            isRegex = true
        ),
        DestructivePattern(
            pattern = "restore \\.",
            severity = Severity.CRITICAL,
            reason = "Discards all uncommitted changes (Git 2.23+)",
            isRegex = true
        ),
        DestructivePattern(
            pattern = "restore --worktree",
            severity = Severity.CRITICAL,
            reason = "Discards working directory changes"
        ),

        // CRITICAL: Clean removes untracked files
        DestructivePattern(
            pattern = "clean -f",
            severity = Severity.CRITICAL,
            reason = "Permanently deletes untracked files"
        ),
        DestructivePattern(
            pattern = "clean -fd",
            severity = Severity.CRITICAL,
            reason = "Permanently deletes untracked files and directories"
        ),
        DestructivePattern(
            pattern = "clean -df",
            severity = Severity.CRITICAL,
            reason = "Permanently deletes untracked files and directories"
        ),
        DestructivePattern(
            pattern = "clean.*-[xX]",
            severity = Severity.CRITICAL,
            reason = "Deletes ignored files",
            isRegex = true
        ),

        // HIGH: Force push - can overwrite remote work
        DestructivePattern(
            pattern = "push --force",
            severity = Severity.HIGH,
            reason = "Can overwrite remote commits"
        ),
        DestructivePattern(
            pattern = "push -f",
            severity = Severity.HIGH,
            reason = "Can overwrite remote commits"
        ),

        // MEDIUM: Rebase rewrites history
        DestructivePattern(
            pattern = "rebase",
            severity = Severity.MEDIUM,
            reason = "Rewrites commit history"
        ),

        // MEDIUM: Filter-branch rewrites history
        DestructivePattern(
            pattern = "filter-branch",
            severity = Severity.HIGH,
            reason = "Rewrites entire repository history"
        ),

        // MEDIUM: Branch deletion
        DestructivePattern(
            pattern = "branch -D",
            severity = Severity.MEDIUM,
            reason = "Force deletes branch"
        ),

        // MEDIUM: Tag deletion
        DestructivePattern(
            pattern = "tag -d",
            severity = Severity.MEDIUM,
            reason = "Deletes tag"
        ),

        // MEDIUM: Reflog expire
        DestructivePattern(
            pattern = "reflog expire",
            severity = Severity.MEDIUM,
            reason = "Removes reflog entries (affects recovery)"
        )
    )

    /**
     * Safe patterns that override destructive classification.
     * These are "escape hatches" for otherwise dangerous commands.
     */
    private val safeOverrides = listOf(
        SafePattern(
            pattern = "--force-with-lease",
            reason = "Safer alternative to --force, checks remote state"
        ),
        SafePattern(
            pattern = "clean -n",
            reason = "Dry run - only shows what would be deleted"
        ),
        SafePattern(
            pattern = "clean --dry-run",
            reason = "Dry run - only shows what would be deleted"
        ),
        SafePattern(
            pattern = "reset --soft",
            reason = "Keeps changes in staging area"
        )
    )

    /**
     * Classifies the risk level of a command from Copilot's response.
     *
     * This method is LANGUAGE-AGNOSTIC because it analyzes the Git command itself,
     * which is always in English regardless of the user's language.
     *
     * @param response The parsed Copilot CLI response
     * @return RiskLevel - CRITICAL/HIGH for destructive, LOW for safe
     */
    fun classify(response: CopilotResponse): RiskLevel {
        if (response.commands.isEmpty()) {
            return RiskLevel.UNKNOWN
        }

        val command = response.commands.first()
        val normalizedCommand = normalizeCommand(command)

        // First check for safe overrides
        val hasSafeOverride = safeOverrides.any { safe ->
            normalizedCommand.contains(safe.pattern.lowercase())
        }

        if (hasSafeOverride) {
            return RiskLevel.LOW
        }

        // Check for destructive patterns
        val matchedPatterns = destructiveCommandPatterns.filter { pattern ->
            if (pattern.isRegex) {
                normalizedCommand.matches(Regex(".*${pattern.pattern}.*", RegexOption.IGNORE_CASE))
            } else {
                normalizedCommand.contains(pattern.pattern.lowercase())
            }
        }

        if (matchedPatterns.isEmpty()) {
            return RiskLevel.LOW
        }

        // Determine overall risk from highest severity match
        val highestSeverity = matchedPatterns.maxByOrNull { it.severity.level }?.severity

        return when (highestSeverity) {
            Severity.CRITICAL, Severity.HIGH -> RiskLevel.HIGH
            Severity.MEDIUM -> RiskLevel.HIGH // Conservative: treat medium as high
            else -> RiskLevel.LOW
        }
    }

    /**
     * Normalizes a Git command for consistent pattern matching.
     * Removes 'git' prefix, extra whitespace, etc.
     */
    private fun normalizeCommand(command: String): String {
        return command
            .trim()
            .lowercase()
            .removePrefix("git")
            .trim()
            .replace(Regex("\\s+"), " ") // Normalize whitespace
    }

    /**
     * Gets a detailed warning message based on the risk level and command.
     *
     * Uses command pattern matching (language-agnostic) to provide specific warnings.
     *
     * @param risk The assessed risk level
     * @param command The Git command
     * @return A formatted warning message
     */
    fun getWarningMessage(risk: RiskLevel, command: String): String {
        val normalizedCommand = normalizeCommand(command)

        // Find the most specific warning for this command
        val matchedPattern = destructiveCommandPatterns.firstOrNull { pattern ->
            if (pattern.isRegex) {
                normalizedCommand.matches(Regex(".*${pattern.pattern}.*", RegexOption.IGNORE_CASE))
            } else {
                normalizedCommand.contains(pattern.pattern.lowercase())
            }
        }

        return when {
            normalizedCommand.contains("checkout .") ||
                    normalizedCommand.contains("checkout -- .") ||
                    normalizedCommand.contains("restore .") -> """
                |[!] WARNING: This command DISCARDS ALL UNCOMMITTED CHANGES!
                |    
                |    What will be PERMANENTLY LOST:
                |    • All modifications to tracked files
                |    • Cannot be recovered (no reflog for working directory)
                |    
                |    Safer alternative:
                |    • git stash (temporarily saves changes)
                |    • git stash pop (restores them later)
            """.trimMargin()

            normalizedCommand.contains("reset --hard") -> """
                |[!] DANGER: --hard flag PERMANENTLY DELETES changes!
                |    
                |    What will be PERMANENTLY LOST:
                |    • All uncommitted changes
                |    • All staged changes
                |    • Can only recover via reflog (if commits existed)
                |    
                |    Safer alternative:
                |    • git reset --soft HEAD~1 (keeps changes staged)
                |    • git reset --mixed HEAD~1 (keeps changes unstaged)
            """.trimMargin()

            normalizedCommand.contains("push --force") ||
                    normalizedCommand.contains("push -f") -> """
                |[!] DANGER: Force push can OVERWRITE others' work!
                |    
                |    Risks:
                |    • Deletes commits that teammates pushed
                |    • Causes conflicts for entire team
                |    • Can lose work permanently
                |    
                |    Safer alternative:
                |    • git push --force-with-lease (checks remote first)
            """.trimMargin()

            normalizedCommand.contains("clean -f") ||
                    normalizedCommand.contains("clean -fd") -> """
                |[!] DANGER: Git clean PERMANENTLY DELETES untracked files!
                |    
                |    What will be PERMANENTLY LOST:
                |    • Files never added to git
                |    • Build artifacts, logs, etc.
                |    • Cannot be recovered
                |    
                |    Safer approach:
                |    • Run 'git clean -n' first (dry run - shows what will be deleted)
            """.trimMargin()

            matchedPattern != null -> """
                |[!] WARNING: This command is potentially DESTRUCTIVE!
                |    
                |    Reason: ${matchedPattern.reason}
                |    Severity: ${matchedPattern.severity.name}
                |    
                |    Make sure you understand what it does before proceeding.
            """.trimMargin()

            risk == RiskLevel.HIGH -> """
                |[!] WARNING: This command is DESTRUCTIVE!
                |    It may cause permanent data loss.
                |    Make sure you understand what it does before proceeding.
            """.trimMargin()

            risk == RiskLevel.LOW -> """
                |[OK] This command is safe.
                |     The operation is reversible or poses no risk of data loss.
            """.trimMargin()

            else -> """
                |[?] Could not determine risk level.
                |    Please review the command carefully before executing.
            """.trimMargin()
        }
    }
}

/**
 * Represents a destructive Git command pattern.
 */
private data class DestructivePattern(
    val pattern: String,
    val severity: Severity,
    val reason: String,
    val isRegex: Boolean = false
)

/**
 * Safe pattern that overrides destructive classification.
 */
private data class SafePattern(
    val pattern: String,
    val reason: String
)

/**
 * Severity levels for destructive operations.
 */
private enum class Severity(val level: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4)
}