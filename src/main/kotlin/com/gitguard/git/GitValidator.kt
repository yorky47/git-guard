package com.gitguard.git

import java.io.File

/**
 * Validates Git repository state and retrieves repository information.
 *
 * Provides functionality to check if the current directory is a Git repository
 * and to extract information such as the current branch and uncommitted changes.
 */
class GitValidator {

    /**
     * Checks whether the current working directory is a valid Git repository.
     *
     * @return `true` if a Git repository is found, `false` otherwise
     */
    fun isGitRepository(): Boolean {
        return try {
            val process = ProcessBuilder("git", "rev-parse", "--git-dir")
                .redirectErrorStream(true)
                .start()

            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Retrieves information about the current Git repository.
     *
     * Fetches the current branch name and checks for uncommitted changes.
     *
     * @return RepositoryInfo containing current branch and uncommitted changes status,
     *         or `null` if not in a Git repository
     */
    fun getRepositoryInfo(): RepositoryInfo? {
        if (!isGitRepository()) return null

        return try {
            val branch = executeGitCommand("git rev-parse --abbrev-ref HEAD")
            val hasUncommitted = executeGitCommand("git status --porcelain").isNotBlank()

            RepositoryInfo(
                currentBranch = branch.trim(),
                hasUncommittedChanges = hasUncommitted
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Executes a Git command and returns its output.
     *
     * @param command The Git command to execute (e.g., "git rev-parse --abbrev-ref HEAD")
     * @return The standard output of the command
     */
    private fun executeGitCommand(command: String): String {
        val process = ProcessBuilder(*command.split(" ").toTypedArray())
            .redirectErrorStream(true)
            .start()

        return process.inputStream
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
    }
}

/**
 * Contains metadata about a Git repository.
 *
 * @property currentBranch The name of the currently checked-out branch
 * @property hasUncommittedChanges `true` if there are staged or unstaged changes in the working directory
 */
data class RepositoryInfo(
    val currentBranch: String,
    val hasUncommittedChanges: Boolean
)