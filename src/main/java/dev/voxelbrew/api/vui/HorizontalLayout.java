package dev.voxelbrew.api.vui;

import java.util.List;

/** Arranges child components in a row. */
public record HorizontalLayout(List<Component> children) implements Layout {
    public HorizontalLayout(Component... children) {
        this(List.of(children));
    }

    public HorizontalLayout {
        children = List.copyOf(children);
    }
}
