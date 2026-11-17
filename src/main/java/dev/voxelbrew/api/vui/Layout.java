package dev.voxelbrew.api.vui;

/** A component that arranges child components in a dialog. */
public sealed interface Layout extends Component permits HorizontalLayout, VerticalLayout {}
