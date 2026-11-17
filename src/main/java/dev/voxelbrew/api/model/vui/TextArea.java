package dev.voxelbrew.api.model.vui;

import lombok.NonNull;

/** A multi-line editable value. */
public record TextArea(
        @NonNull String id, @NonNull String label, @NonNull String value) implements Input {}
