package com.gitguard.cli

import com.gitguard.copilot.CopilotService
import com.gitguard.git.ExecutionResult
import com.gitguard.git.GitExecutor
import com.gitguard.git.GitValidator
import com.gitguard.model.CommandAnalysis
import com.gitguard.risk.RiskClassifier
import com.gitguard.ui.TerminalUI

/**
 * Processes natural language user intent and orchestrates the entire GitGuard workflow.
 *
 * Coordinates between multiple services to:
 * 1. Validate the user is in a Git repository
 * 2. Request a Git command suggestion from GitHub Copilot
 * 3. Classify the risk level of the suggested command
 * 4. Display the analysis to the user
 * 5. Request user confirmation
 * 6. Execute the command if approved
 */
class CommandProcessor(
    private val copilot: CopilotService = CopilotService(),
    private val riskClassifier: RiskClassifier = RiskClassifier(),
    private val gitValidator: GitValidator = GitValidator(),
    private val gitExecutor: GitExecutor = GitExecutor(),
    private val terminal: TerminalUI = TerminalUI()
) {

    /**
     * Processes a user's natural language intent to suggest and execute a Git command.
     *
     * Follows the complete workflow:
     * 1. Display header
     * 2. Validate Git repository
     * 3. Get command suggestion from Copilot
     * 4. Classify risk level
     * 5. Display analysis and warnings
     * 6. Ask for user confirmation
     * 7. Execute command if approved
     *
     * @param intent The user's natural language description of what they want to do
     *               (e.g., "undo last commit but keep changes")
     */
    fun process(intent: String) {
        terminal.displayHeader()

        // Step 1: Validate that we are in a Git repository
        if (!gitValidator.isGitRepository()) {
            terminal.displayError("Not in a Git repository!")
            terminal.displayError("Navigate to a folder with a Git repository before using GitGuard.")
            return
        }

        // Step 2: Display repository context
        val repoInfo = gitValidator.getRepositoryInfo()
        if (repoInfo != null) {
            println("[REPOSITORY] Branch: ${repoInfo.currentBranch}")
            if (repoInfo.hasUncommittedChanges) {
                println("[!] You have uncommitted changes")
            }
            println()
        }

        // Step 3: Request suggestion from Copilot
        println("[ANALYZING] \"$intent\"")
        println("[INFO] Consulting GitHub Copilot CLI...")
        println()


        val response = try {
            copilot.suggest(intent)
        } catch (e: Exception) {
            terminal.displayError("Error consulting Copilot: ${e.message}")
            return
        }

        // Step 4: Verify that we got a command
        if (response.commands.isEmpty()) {
            terminal.displayError("Could not find a Git command for: \"$intent\"")
            terminal.displayError("Try rephrasing your intent.")
            return
        }

        val command = response.commands.first()

        // Step 5: Classify risk level
        val risk = riskClassifier.classify(response)

        // Step 6: Create analysis
        val analysis = CommandAnalysis(
            command = command,
            explanation = response.explanation,
            risk = risk
        )

        // Step 7: Display analysis
        terminal.displayAnalysis(analysis)

        // Step 8: Display risk warning
        val warningMessage = riskClassifier.getWarningMessage(risk, command)
        terminal.displayWarning(warningMessage)

        // Step 9: Request confirmation
        val confirmed = terminal.askConfirmation()

        if (!confirmed) {
            terminal.displayCancelled()
            return
        }

        // Step 10: Execute command
        when (val result = gitExecutor.execute(command)) {
            is ExecutionResult.Success -> {
                terminal.displaySuccess("Command executed successfully!")
                if (result.output.isNotBlank()) {
                    println(result.output)
                }
            }
            is ExecutionResult.Error -> {
                terminal.displayError("Error executing command:")
                println(result.message)
            }
        }
    }
}