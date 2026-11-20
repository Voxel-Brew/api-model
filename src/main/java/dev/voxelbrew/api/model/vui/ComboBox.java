package dev.voxelbrew.api.model.vui;

import java.util.List;
import lombok.NonNull;

/** A single selection from a drop-down list. */
public record ComboBox(@NonNull String id, @NonNull String label, List<String> choices, String selected)
        implements Input {
    public ComboBox {
        choices = List.copyOf(choices);
        if (choices.isEmpty() || !choices.contains(selected)) {
            throw new IllegalArgumentException("Selected value must be one of the choices");
        }
    }
}
