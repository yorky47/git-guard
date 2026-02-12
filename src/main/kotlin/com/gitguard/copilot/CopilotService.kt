package com.gitguard.copilot

import com.gitguard.model.CopilotResponse
import java.io.IOException
import com.gitguard.model.CopilotJsonDto
import kotlinx.serialization.json.Json

/**
 * Service for communicating with GitHub Copilot CLI.
 *
 * Handles execution of Copilot commands (suggest, explain) and parsing of responses
 * to extract Git commands, explanations, and warnings about destructive operations.
 */
class CopilotService {

    private val TOOM_PROMPT = "Act:GitGuard Task:Intent2Git Output:JSON_Format:{command,explanation} Rules:StrictJSON,NoThinking,NoLogs,NoPreamble Intent:"
    private val jsonParser = Json { ignoreUnknownKeys = true }

    fun suggest(intent: String): CopilotResponse {
        val fullPrompt = "suggest $TOOM_PROMPT $intent"
        val raw = executeCommand(fullPrompt)
        //print for debugging
        //println(raw)
        return parseJsonResponse(raw)
    }

    private fun parseJsonResponse(raw: String): CopilotResponse {
        // Aislamos el JSON eliminando las estadísticas de uso de la CLI
        val cleanOutput = raw.substringBefore("Total usage est:").trim()

        return try {
            // Buscamos el bloque JSON si la IA añade texto extra por error
            val jsonMatch = Regex("\\{.*\\}", RegexOption.DOT_MATCHES_ALL).find(cleanOutput)?.value
                ?: throw IOException("No JSON found in response")

            val dto = jsonParser.decodeFromString<CopilotJsonDto>(jsonMatch)

            CopilotResponse(
                rawOutput = raw,
                explanation = dto.explanation,
                commands = listOf(dto.command),
                hasWarning = raw.contains("destructive", ignoreCase = true) || raw.contains("lost", ignoreCase = true)
            )
        } catch (e: Exception) {
            // Fallback en caso de error de parsing
            CopilotResponse(raw, "Error parsing AI response: ${e.message}", emptyList(), false)
        }
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
        // Remover estatísticas do final
        val cleanOutput = raw.substringBefore("Total usage est:").trim()

        // Extrair comandos (entre ```)
        val commandRegex = Regex("```(?:bash|powershell|sh|git)?\n(.*?)\n```", RegexOption.DOT_MATCHES_ALL)
        val commands = commandRegex.findAll(cleanOutput)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotBlank() }
            .toList()

        // Detectar avisos
        val hasWarning = cleanOutput.contains("destructive", ignoreCase = true) ||
                cleanOutput.contains("permanently", ignoreCase = true) ||
                cleanOutput.contains("lost", ignoreCase = true) ||
                cleanOutput.contains("cannot be reversed", ignoreCase = true) ||
                cleanOutput.contains("careful", ignoreCase = true) ||
                cleanOutput.contains("danger", ignoreCase = true)

        // Lista de padrões de "ruído" técnico para filtrar
        val noisePatterns = listOf(
            // Erros de execução
            "exited with error",
            "Command failed",
            "not recognized",
            "Permission denied",
            "could not request permission",

            // Erros de PowerShell/sistema
            "PowerShell 6+",
            "pwsh.exe",
            "pwsh",
            "is not available",

            // Erros de API/GitHub
            "failed to list commits",
            "404 Not Found",
            "GET https://api.github.com",

            // Ruído de MCP/GitHub tools
            "github-mcp-server",
            "mcp-server",

            // Símbolos de status (aparecem antes de comandos executados)
            "✓",
            "✗",
            "Ô£ù", // Símbolo do Copilot
            "ÔùÅ",
            "Ôöö",

            // Padrões de tentativas de execução
            "$ git",
            "$ cd",
            "$ powershell",

            // Mensagens técnicas comuns
            "Error: Error:",
            "operable program or batch file"
        )

        // Também filtrar linhas que são apenas JSON
        val jsonPattern = Regex("^\\s*[{\\[].*[}\\]]\\s*$")

        // Extrair explicação limpa
        val explanation = cleanOutput
            .replace(commandRegex, "") // Remove blocos de código
            .trim()
            .lines()
            .filter { line ->
                // Manter apenas linhas que:
                // 1. Não estão em branco
                // 2. Não contêm padrões de ruído
                // 3. Não são JSON puro
                // 4. Não começam com $ (comandos shell)
                val trimmedLine = line.trim()
                trimmedLine.isNotBlank() &&
                        noisePatterns.none { pattern -> trimmedLine.contains(pattern, ignoreCase = true) } &&
                        !jsonPattern.matches(trimmedLine) &&
                        !trimmedLine.startsWith("$")
            }
            .joinToString("\n")
            .trim()

        // Se a explicação ficou vazia ou muito curta, usar fallback
        val finalExplanation = if (explanation.length < 20) {
            "Command suggested by GitHub Copilot CLI."
        } else {
            formatExplanation(explanation)
        }

        return CopilotResponse(
            rawOutput = raw,
            explanation = finalExplanation,
            commands = commands,
            hasWarning = hasWarning
        )
    }
    /**
     * Formats and cleans explanation text for better readability.
     */
    private fun formatExplanation(text: String): String {
        return text
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString("\n")
            .replace(Regex("\n{3,}"), "\n\n") // Max 2 line breaks
    }
}