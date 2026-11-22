package dev.voxelbrew.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import dev.voxelbrew.api.model.sketch.SketchGeometry;
import dev.voxelbrew.api.model.sketch.SketchText;
import java.util.List;
import org.junit.jupiter.api.Test;

class SketchTextLayoutTest {
    @Test
    void centersAndTopAlignsTheOutlineBeforeRotation() {
        var text = SketchText.create(
                "label",
                "Ag",
                "SansSerif",
                3,
                20,
                30,
                8,
                Math.PI / 2,
                0,
                "",
                false,
                SketchText.Horizontal.CENTER,
                SketchText.Vertical.TOP);
        var geometry = new SketchGeometry(List.of(text), List.of(), List.of("label"));
        var points =
                geometry.curves(text).stream().flatMap(e -> e.sample().stream()).toList();
        var x = points.stream().mapToDouble(SketchGeometry.Vec::x).summaryStatistics();
        var y = points.stream().mapToDouble(SketchGeometry.Vec::y).summaryStatistics();
        assertThat(x.getMin()).isCloseTo(20, within(1e-6));
        assertThat((y.getMin() + y.getMax()) / 2).isCloseTo(30, within(1e-6));
    }
}
