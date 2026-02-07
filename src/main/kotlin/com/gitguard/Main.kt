package com.gitguard

import com.gitguard.cli.CommandProcessor
import com.gitguard.config.SystemConfig

/**
 * Entry point for the GitGuard application.
 *
 * Parses command-line arguments and processes the user's natural language intent
 * to suggest and execute safe Git commands.
 *
 * @param args Array of command-line arguments. The arguments are joined to form
 *             a natural language intent (e.g., "undo last commit but keep changes")
 */
fun main(args: Array<String>) {
    // Configure UTF-8 encoding
    SystemConfig.configureUTF8()

    if (args.isEmpty()) {
        showUsage()
        return
    }

    val intent = args.joinToString(" ")
    val processor = CommandProcessor()

    processor.process(intent)
}

/**
 * Displays the usage information and examples for the GitGuard application.
 *
 * Shows application description, usage syntax, examples, workflow steps,
 * and installation instructions for required dependencies.
 */
fun showUsage() {
    println("""
        |
        |🛡️  GitGuard - Git Safety Assistant
        |━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        |
        |Usage: gitguard <your intention in natural language>
        |
        |Examples:
        |  gitguard undo last commit but keep files
        |  gitguard discard all changes
        |  gitguard go back to previous commit
        |  gitguard force push
        |  gitguard reset to main branch
        |
        |GitGuard will:
        |  1. Suggest the appropriate Git command
        |  2. Explain what the command does
        |  3. Classify the risk level
        |  4. Ask for confirmation before executing
        |
        |Requirements:
        |  - GitHub CLI (gh) installed
        |  - GitHub Copilot CLI extension installed
        |  - Currently in a Git repository
        |
        |Installation of Copilot CLI:
        |  gh extension install github/gh-copilot
        |
    """.trimMargin())
}

