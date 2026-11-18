package dev.voxelbrew.api.model.vui;

import lombok.NonNull;

/** A secret single-line input. Hosts must mask its value and never echo it in diagnostics. */
public record PasswordField(
        @NonNull String id, @NonNull String label, @NonNull String value) implements Input {
    @Override
    public String toString() {
        return "PasswordField[id=" + id + ", label=" + label + ", value=<redacted>]";
    }
}
