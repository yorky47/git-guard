package com.gitguard.copilot

import com.gitguard.model.CopilotResponse
import java.io.IOException

/**
 * Service for communicating with GitHub Copilot CLI.
 *
 * Handles execution of Copilot commands (suggest, explain) and parsing of responses
 * to extract Git commands, explanations, and warnings about destructive operations.
 */
class CopilotService {

    /**
     * Suggests a Git command based on a natural language intent.
     *
     * @param intent A description of what the user wants to accomplish (e.g., "undo last commit")
     * @return A parsed CopilotResponse containing the suggested command(s) and explanation
     * @throws IOException if Copilot CLI is not installed or execution fails
     */
    fun suggest(intent: String): CopilotResponse {
        val raw = executeCommand("suggest $intent")
        return parseResponse(raw)
    }

    /**
     * Explains what a given Git command does.
     *
     * @param command The Git command to explain
     * @return A parsed CopilotResponse containing the explanation of the command
     * @throws IOException if Copilot CLI is not installed or execution fails
     */
    fun explain(command: String): CopilotResponse {
        val raw = executeCommand("explain $command")
        return parseResponse(raw)
    }

    /**
     * Executes a GitHub Copilot CLI command with the given prompt.
     *
     * @param prompt The prompt to send to Copilot (e.g., "suggest undo last commit")
     * @return The raw output from Copilot CLI
     * @throws IOException if the process fails or Copilot CLI is not installed
     */
    private fun executeCommand(prompt: String): String {
        try {
            val process = ProcessBuilder("gh", "copilot", "-p", prompt)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

            val exitCode = process.waitFor()

            if (exitCode != 0 && output.contains("not recognized")) {
                throw IOException("GitHub Copilot CLI is not installed. Execute: gh extension install github/gh-copilot")
            }

            return output

        } catch (e: IOException) {
            throw IOException("Error executing GitHub Copilot CLI: ${e.message}")
        }
    }

    /**
     * Parses the raw response from GitHub Copilot CLI.
     *
     * Extracts Git commands (from code blocks), explanation text, and detects
     * warnings about destructive operations.
     *
     * @param raw The raw output string from Copilot CLI
     * @return A structured CopilotResponse ready for further processing
     */
    private fun parseResponse(raw: String): CopilotResponse {
        // Remove usage statistics from the end
        val cleanOutput = raw.substringBefore("Total usage est:").trim()

        // Extract commands from code blocks (```...```)
        val commandRegex = Regex("```(?:bash|powershell|sh|git)?\n(.*?)\n```", RegexOption.DOT_MATCHES_ALL)
        val commands = commandRegex.findAll(cleanOutput)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotBlank() }
            .toList()

        // Detect warnings about destructive operations
        val hasWarning = cleanOutput.contains("destructive", ignoreCase = true) ||
                cleanOutput.contains("permanently", ignoreCase = true) ||
                cleanOutput.contains("lost", ignoreCase = true) ||
                cleanOutput.contains("cannot be reversed", ignoreCase = true) ||
                cleanOutput.contains("careful", ignoreCase = true)

        // Extract explanation (text outside code blocks)
        val explanation = cleanOutput
            .replace(commandRegex, "")
            .trim()
            .lines()
            .filter { it.isNotBlank() }
            .joinToString("\n")

        return CopilotResponse(
            rawOutput = raw,
            explanation = explanation,
            commands = commands,
            hasWarning = hasWarning
        )
    }
}