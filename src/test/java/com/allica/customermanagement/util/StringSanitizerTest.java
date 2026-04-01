package com.allica.customermanagement.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringSanitizerTest {

    @Test
    void shouldTrimLeadingWhitespace() {
        assertThat(StringSanitizer.sanitize("  John")).isEqualTo("John");
    }

    @Test
    void shouldTrimTrailingWhitespace() {
        assertThat(StringSanitizer.sanitize("John  ")).isEqualTo("John");
    }

    @Test
    void shouldTrimBothLeadingAndTrailingWhitespace() {
        assertThat(StringSanitizer.sanitize("  John  ")).isEqualTo("John");
    }

    @Test
    void shouldRemoveNullCharacters() {
        assertThat(StringSanitizer.sanitize("John\u0000Doe")).isEqualTo("JohnDoe");
    }

    @Test
    void shouldRemoveNewlineCharacters() {
        assertThat(StringSanitizer.sanitize("John\nDoe")).isEqualTo("JohnDoe");
    }

    @Test
    void shouldRemoveCarriageReturnCharacters() {
        assertThat(StringSanitizer.sanitize("John\rDoe")).isEqualTo("JohnDoe");
    }

    @Test
    void shouldRemoveTabCharacters() {
        assertThat(StringSanitizer.sanitize("John\tDoe")).isEqualTo("JohnDoe");
    }

    @Test
    void shouldRemoveMultipleControlCharacters() {
        assertThat(StringSanitizer.sanitize("John\n\r\t\u0000Doe")).isEqualTo("JohnDoe");
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertThat(StringSanitizer.sanitize(null)).isNull();
    }

    @Test
    void shouldReturnEmptyStringForEmptyInput() {
        assertThat(StringSanitizer.sanitize("")).isEqualTo("");
    }

    @Test
    void shouldReturnEmptyStringForWhitespaceOnlyInput() {
        assertThat(StringSanitizer.sanitize("   ")).isEqualTo("");
    }

    @Test
    void shouldPreserveValidCharacters() {
        assertThat(StringSanitizer.sanitize("John O'Brien-Smith")).isEqualTo("John O'Brien-Smith");
    }
}
