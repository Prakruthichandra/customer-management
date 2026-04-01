package com.allica.customermanagement.util;

/**
 * Utility class for sanitizing string inputs.
 * Removes unwanted characters and normalizes whitespace.
 */
public class StringSanitizer {

    private StringSanitizer() {
        // Utility class, prevent instantiation
    }

    /**
     * Sanitizes a string by trimming whitespace and removing control characters.
     *
     * @param input the string to sanitize
     * @return sanitized string, or null if input is null
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }

        // Trim leading and trailing whitespace
        String sanitized = input.trim();

        // Remove null characters
        sanitized = sanitized.replaceAll("\\u0000", "");

        // Remove control characters (newlines, tabs, etc.)
        sanitized = sanitized.replaceAll("\\p{Cntrl}", "");

        return sanitized;
    }
}
