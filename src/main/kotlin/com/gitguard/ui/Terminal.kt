package com.gitguard.ui

import com.gitguard.model.CommandAnalysis
import com.gitguard.model.RiskLevel

/**
 * Handles all terminal output and user interaction for the GitGuard application.
 *
 * Provides methods for displaying analysis results, warnings, error messages,
 * and prompting the user for confirmation.
 */
class Terminal {

    /**
     * Displays the GitGuard header banner.
     *
     * Shows the application name and a decorative separator.
     */
    fun displayHeader() {
        println("""
            |
            |🛡️  GitGuard - Git Safety Assistant
            |━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            |
        """.trimMargin())
    }

    /**
     * Displays the command analysis results.
     *
     * Shows the suggested Git command, risk level with icon, and explanation
     * in a user-friendly format.
     *
     * @param analysis The CommandAnalysis containing the command, explanation, and risk level
     */
    fun displayAnalysis(analysis: CommandAnalysis) {
        println("🤖 Suggested command:")
        println("   ${analysis.command}")
        println()

        println("📊 Risk Level: ${analysis.risk.icon} ${analysis.risk.name}")
        println()

        println("📖 Explanation:")
        analysis.explanation.lines().forEach { line ->
            println("   $line")
        }
        println()
    }

    /**
     * Displays a warning or informational message.
     *
     * @param message The message to display
     */
    fun displayWarning(message: String) {
        println(message)
        println()
    }

    /**
     * Prompts the user for confirmation to execute the command.
     *
     * Accepts 's', 'y', 'sim', or 'yes' as affirmative responses (case-insensitive).
     *
     * @return `true` if the user confirmed execution, `false` otherwise
     */
    fun askConfirmation(): Boolean {
        print("❓ Execute this command? (y/n): ")
        val input = readlnOrNull()?.lowercase()?.trim()
        return input == "s" || input == "y" || input == "sim" || input == "yes"
    }

    /**
     * Displays a success message.
     *
     * @param message The success message to display
     */
    fun displaySuccess(message: String) {
        println("\n✅ $message\n")
    }

    /**
     * Displays an error message.
     *
     * @param message The error message to display
     */
    fun displayError(message: String) {
        println("\n❌ $message\n")
    }

    /**
     * Displays a cancellation message when the user declines to execute the command.
     */
    fun displayCancelled() {
        println("\n🚫 Operation cancelled.\n")
    }
}