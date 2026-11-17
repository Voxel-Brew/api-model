package dev.voxelbrew.api.model.vui;

import java.util.List;

/** Arranges child components in a column. */
public record VerticalLayout(List<Component> children) implements Layout {
    public VerticalLayout(Component... children) {
        this(List.of(children));
    }

    public VerticalLayout {
        children = List.copyOf(children);
    }
}
