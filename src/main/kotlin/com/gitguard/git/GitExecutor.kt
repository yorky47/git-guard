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
            // Normalizamos o comando para garantir que começa com 'git '
            val fullCommand = if (command.trim().startsWith("git ")) {
                command.trim()
            } else {
                "git ${command.trim()}"
            }

            println("\n [Executing]: $fullCommand\n")

            // Detetamos o SO para usar o interpretador correto
            val isWindows = System.getProperty("os.name").lowercase().contains("windows")
            val processBuilder = if (isWindows) {
                // No Windows, usamos 'cmd /c' para que as aspas sejam respeitadas
                ProcessBuilder("cmd.exe", "/c", fullCommand)
            } else {
                // Em Unix/Linux/macOS, usamos 'sh -c'
                ProcessBuilder("sh", "-c", fullCommand)
            }

            val process = processBuilder
                .redirectErrorStream(false)
                .start()

            val output = process.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val error = process.errorStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                ExecutionResult.Success(output.ifBlank { "Command executed successfully!" })
            } else {
                // IMPORTANTE: Se o error estiver vazio, tentamos mostrar o output
                val errorMessage = when {
                    error.isNotBlank() -> error
                    output.isNotBlank() -> output
                    else -> "Git returned exit code $exitCode with no message."
                }
                ExecutionResult.Error(errorMessage)
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