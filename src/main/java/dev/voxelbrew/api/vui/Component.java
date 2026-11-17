package dev.voxelbrew.api.vui;

/** Frontend-neutral component in a Virtual UI tree. */
public sealed interface Component permits Layout, Input, Label, Separator {}
