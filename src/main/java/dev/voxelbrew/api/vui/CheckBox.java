package dev.voxelbrew.api.vui;

import lombok.NonNull;

/** A boolean option. */
public record CheckBox(@NonNull String id, @NonNull String label, boolean selected) implements Input {}
