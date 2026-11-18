package dev.voxelbrew.api.model.vui;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordFieldTest {
    @Test
    void secretIsAvailableToHostButExcludedFromDiagnostics() {
        var field = new PasswordField("token", "Token", "secret-value");
        assertThat(field.value()).isEqualTo("secret-value");
        assertThat(field.toString()).contains("<redacted>").doesNotContain("secret-value");
    }
}
