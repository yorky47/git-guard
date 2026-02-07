package com.gitguard.model

/**
 * Represents the parsed response from GitHub Copilot CLI.
 *
 * Contains the raw output, extracted explanation, suggested Git commands,
 * and a flag indicating whether the response includes warnings about destructive operations.
 *
 * @property rawOutput The complete unprocessed output from GitHub Copilot CLI
 * @property explanation The human-readable explanation of the suggested command(s)
 * @property commands List of suggested Git command(s) extracted from code blocks
 * @property hasWarning `true` if the response contains warnings about destructive operations
 */
data class CopilotResponse(
    val rawOutput: String,
    val explanation: String,
    val commands: List<String>,
    val hasWarning: Boolean
)

/**
 * Enumeration of risk levels for Git commands.
 *
 * Used to classify the safety and destructiveness of Git operations.
 * Each level includes a visual icon and color code for terminal display.
 *
 * @property icon Visual emoji representation of the risk level
 * @property color Color code for terminal display (green, red, yellow)
 */
enum class RiskLevel(val icon: String, val color: String) {
    /** Command is safe or reversible with minimal risk of data loss */
    LOW("🟢", "green"),
    /** Command is destructive and may cause permanent data loss */
    HIGH("🔴", "red"),
    /** Risk level could not be determined reliably */
    UNKNOWN("⚪", "yellow")
}

/**
 * Complete analysis of a Git command suggested by GitHub Copilot.
 *
 * Contains the proposed command, its explanation, and the assessed risk level.
 *
 * @property command The Git command suggested by Copilot
 * @property explanation The explanation of what the command does
 * @property risk The classified risk level of executing this command
 */
data class CommandAnalysis(
    val command: String,
    val explanation: String,
    val risk: RiskLevel
)