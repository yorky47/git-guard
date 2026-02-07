package com.gitguard.git

import java.io.IOException

/**
 * Executes Git commands and captures their output and error streams.
 *
 * Handles the execution of Git commands in a separate process and provides
 * structured results indicating success or failure with corresponding output messages.
 */
class GitExecutor {

    /**
     * Executes a Git command in a new process.
     *
     * Cleans up the command format (removes leading "git" if present, which Copilot may include)
     * and executes it as a Git command. Captures both standard output and error streams.
     *
     * @param command The Git command to execute (e.g., "commit -m 'message'" or "git commit -m 'message'")
     * @return ExecutionResult.Success containing the command output, or ExecutionResult.Error with error message
     */
    fun execute(command: String): ExecutionResult {
        return try {
            // Remove 'git' prefix if present (Copilot sometimes includes it)
            val cleanCommand = command.removePrefix("git").trim()
            val fullCommand = "git $cleanCommand"

            println("\n🔧 Executing: $fullCommand\n")

            val process = ProcessBuilder("git", *cleanCommand.split(" ").toTypedArray())
                .redirectErrorStream(false)
                .start()

            val output = process.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val error = process.errorStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                ExecutionResult.Success(output.ifBlank { "Command executed successfully!" })
            } else {
                ExecutionResult.Error(error.ifBlank { "Command failed with exit code: $exitCode" })
            }

        } catch (e: IOException) {
            ExecutionResult.Error("Error executing command: ${e.message}")
        }
    }
}

/**
 * Sealed class representing the result of executing a Git command.
 *
 * Use pattern matching to handle Success and Error cases.
 */
sealed class ExecutionResult {
    /**
     * Indicates successful command execution.
     *
     * @property output The standard output from the executed command
     */
    data class Success(val output: String) : ExecutionResult()

    /**
     * Indicates failed command execution.
     *
     * @property message The error message or standard error output from the command
     */
    data class Error(val message: String) : ExecutionResult()
}