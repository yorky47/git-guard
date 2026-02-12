package com.gitguard.ui
import com.github.ajalt.mordant.rendering.*
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import com.gitguard.model.CommandAnalysis
import com.gitguard.model.RiskLevel

/**
 * Terminal UI using ASCII-only characters for maximum compatibility.
 * Works perfectly on all Windows terminals without encoding issues.
 */
class TerminalUI  {
    private val terminal = Terminal()

    fun displayHeader() {
        terminal.println()
        terminal.println(TextColors.cyan("=".repeat(70)))
        terminal.println(TextColors.cyan(TextStyles.bold("   GitGuard - Git Safety Assistant")))
        terminal.println(TextColors.cyan("=".repeat(70)))
        terminal.println()
    }

    fun displayAnalysis(analysis: CommandAnalysis) {
        terminal.println(TextStyles.bold("[SUGGESTED COMMAND]"))
        terminal.println(TextColors.yellow("   ${analysis.command}"))
        terminal.println()

        val riskLabel = when (analysis.risk) {
            RiskLevel.LOW -> "LOW"
            RiskLevel.HIGH -> "HIGH"
            RiskLevel.UNKNOWN -> "UNKNOWN"
        }
        val riskColor = when (analysis.risk) {
            RiskLevel.LOW -> TextColors.green
            RiskLevel.HIGH -> TextColors.red
            RiskLevel.UNKNOWN -> TextColors.yellow
        }

        val riskIcon = when (analysis.risk) {
            RiskLevel.LOW -> TextColors.green("[OK]")
            RiskLevel.HIGH -> TextColors.red("[!]")
            RiskLevel.UNKNOWN -> TextColors.yellow("[?]")
        }

        terminal.println(TextStyles.bold("[RISK LEVEL] ") + riskIcon + " " + riskColor(riskLabel))
        terminal.println()

        terminal.println(TextStyles.bold("[EXPLANATION]"))
        analysis.explanation.lines().forEach { line ->
            if (line.isNotBlank()) {
                terminal.println(TextColors.gray("   $line"))
            }
        }
        terminal.println()
    }

    fun displayWarning(message: String) {
        // Parse message to color specific parts
        message.lines().forEach { line ->
            when {
                line.startsWith("[!]") -> terminal.println(TextColors.red(TextStyles.bold(line)))
                line.startsWith("[OK]") -> terminal.println(TextColors.green(line))
                line.startsWith("[?]") -> terminal.println(TextColors.yellow(line))
                line.contains("PERMANENTLY") -> terminal.println(TextColors.red(line))
                line.contains("Safer alternative") -> terminal.println(TextColors.green(line))
                line.trim().startsWith("•") || line.trim().startsWith("*") -> {
                    terminal.println(TextColors.gray(line))
                }
                else -> terminal.println(line)
            }
        }
        terminal.println()
    }

    fun askConfirmation(): Boolean {
        terminal.print(TextColors.cyan("Execute this command? ") + TextColors.gray("(y/n): "))
        val input = readlnOrNull()?.lowercase()?.trim()
        return input == "y" || input == "yes"
    }

    fun displaySuccess(message: String) {
        terminal.println()
        terminal.println(TextColors.green(TextStyles.bold("[SUCCESS] ")) + message)
        terminal.println()
    }

    fun displayError(message: String) {
        terminal.println()
        terminal.println(TextColors.red(TextStyles.bold("[ERROR] ")) + message)
        terminal.println()
    }

    fun displayCancelled() {
        terminal.println()
        terminal.println(TextColors.yellow(TextStyles.bold("[CANCELLED] ")) + "Operation cancelled.")
        terminal.println(TextColors.green(TextStyles.bold("[INFO] ")) + "Your work is safe! ")
        terminal.println()
    }

    fun displayInfo(message: String) {
        terminal.println()
        terminal.println(TextColors.cyan(TextStyles.bold("[INFO] ")) + message)
        terminal.println()
    }
}