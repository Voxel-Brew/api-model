package dev.voxelbrew.api.model.vui;

import lombok.NonNull;

/** A single-line editable value. */
public record TextField(
        @NonNull String id, @NonNull String label, @NonNull String value) implements Input {}
