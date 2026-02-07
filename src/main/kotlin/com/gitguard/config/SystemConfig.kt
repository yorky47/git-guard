package com.gitguard.config

import java.io.PrintStream
import java.nio.charset.StandardCharsets

/**
 * System configuration utilities for GitGuard.
 * Handles UTF-8 encoding setup across different operating systems.
 */
object SystemConfig {

    /**
     * Configures the system to use UTF-8 encoding for output.
     * This is especially important on Windows where the default codepage is often not UTF-8.
     */
    fun configureUTF8() {
        try {
            // Set System.out and System.err to use UTF-8
            System.setOut(PrintStream(System.out, true, StandardCharsets.UTF_8))
            System.setErr(PrintStream(System.err, true, StandardCharsets.UTF_8))

            // On Windows, try to set console to UTF-8
            if (isWindows()) {
                setWindowsCodePage()
            }
        } catch (e: Exception) {
            // Silently fail - not critical
            System.err.println("Warning: Could not configure UTF-8 encoding: ${e.message}")
        }
    }

    private fun isWindows(): Boolean {
        return System.getProperty("os.name").lowercase().contains("windows")
    }

    private fun setWindowsCodePage() {
        try {
            // Try to set Windows console to UTF-8 (code page 65001)
            Runtime.getRuntime().exec(arrayOf("cmd.exe", "/c", "chcp", "65001"))
        } catch (e: Exception) {
            // Ignore if fails
        }
    }
}