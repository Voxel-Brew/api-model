package dev.voxelbrew.api.model.vui;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class VuiComponentsTest {
    @Test
    void fileInputsPreserveOptionalDefaultsAndRequireIdentifiersAndExtensions() {
        var path = java.nio.file.Path.of("part.vxb");
        assertThat(new FileSource("source", "vxb").defaultPath()).isNull();
        assertThat(new FileDestination("target", "vxb").defaultPath()).isNull();
        assertThat(new FileSource("source", "vxb", path).defaultPath()).isEqualTo(path);
        assertThat(new FileDestination("target", "vxb", path).defaultPath()).isEqualTo(path);
        assertThatThrownBy(() -> new FileSource(null, "vxb")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new FileSource("source", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new FileDestination(null, "vxb")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new FileDestination("target", null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void nestedLayoutsKeepAnImmutableSnapshotOfChildren() {
        var children = new ArrayList<Component>();
        children.add(new TextField("name", "Name", "draft"));
        var row = new HorizontalLayout(children);
        var column = new VerticalLayout(row, new Separator(), new Label("Done"));
        children.clear();

        assertThat(row.children()).hasSize(1);
        assertThat(column.children()).hasSize(3);
        assertThat(column.children().getFirst()).isSameAs(row);
        assertThatThrownBy(() -> row.children().clear()).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> new VerticalLayout(new Label("ok"), null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void selectionInputsRequireAValidDefaultAndCopyChoices() {
        var choices = new ArrayList<>(List.of("draft", "final"));
        var radio = new RadioButtonGroup("stage", "Stage", choices, "draft");
        choices.add("archived");
        assertThat(radio.choices()).containsExactly("draft", "final");
        assertThatThrownBy(() -> radio.choices().clear()).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> new RadioButtonGroup("stage", "Stage", List.of("draft"), "final"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ComboBox("stage", "Stage", List.of(), "draft"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sliderRejectsNonFiniteOrOutOfRangeValues() {
        assertThatThrownBy(() -> new Slider("scale", "Scale", 0, 1, Double.NaN))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Slider("scale", "Scale", 1, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Slider("scale", "Scale", 0, 1, -0.1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(new Slider("scale", "Scale", 0, 1, 0.5).value()).isEqualTo(0.5);
    }
}
