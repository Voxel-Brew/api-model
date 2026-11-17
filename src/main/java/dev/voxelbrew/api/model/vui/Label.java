package dev.voxelbrew.api.model.vui;

import lombok.NonNull;

/** Read-only text. */
public record Label(@NonNull String text) implements Component {}
