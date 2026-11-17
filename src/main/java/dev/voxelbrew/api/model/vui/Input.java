package dev.voxelbrew.api.model.vui;

/** Component whose value can be supplied by a frontend. */
public sealed interface Input extends Component
        permits TextField, TextArea, CheckBox, ComboBox, Slider, FileSource, FileDestination, RadioButtonGroup {
    String id();
}
