package dev.voxelbrew.api.vui;

import lombok.NonNull;

/** Read-only text. */
public record Label(@NonNull String text) implements Component {}
