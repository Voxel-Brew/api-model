package dev.voxelbrew.engine.model;

import static org.assertj.core.api.Assertions.*;

import dev.voxelbrew.engine.mesh.MeshOptions;
import dev.voxelbrew.engine.parameters.BuiltinParameters;
import dev.voxelbrew.engine.printing.ManufacturingModel;
import dev.voxelbrew.engine.printing.PrintProfile;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ModelApiTest {
    @Test
    void customBuildVolumeResolvesAndRejectsInvalidDimensions() {
        var setup = CadDocument.empty().printSetup();
        var custom = setup.withProfile(
                setup.backendId(),
                PrintProfile.rectangularPrinterId(250, 220, 300),
                setup.materialId(),
                setup.process());
        var printer = PrintProfile.resolve(custom).printer();
        assertThat(printer.bed().get(2)).isEqualTo(new PrintProfile.Point(250, 220));
        assertThat(printer.usableHeight()).isEqualTo(300);
        assertThat(PrintProfile.resolve(setup).printer().usableHeight()).isEqualTo(200);
        for (double value : new double[] {0, -1, Double.NaN, Double.POSITIVE_INFINITY, 10001})
            assertThatThrownBy(() -> PrintProfile.rectangularPrinterId(value, 200, 200))
                    .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createsDocumentAndDetachedManufacturingValuesWithoutApplicationModules() {
        var document = CadDocument.empty();
        assertThat(document.cursor()).isEqualTo(0);
        assertThat(document.uuidGenerator().next())
                .isNotEqualTo(document.uuidGenerator().next());
        var model = new ManufacturingModel(
                document.id(),
                "revision",
                document.printSetup(),
                PrintProfile.resolve(document.printSetup()),
                Map.of());
        assertThat(model.documentId()).isEqualTo(document.id());
        assertThat(model.instances().isEmpty()).isTrue();
        assertThatThrownBy(() -> model.mesh("missing")).isInstanceOf(IllegalArgumentException.class);
        assertThat(MeshOptions.DEFAULT).isNotNull();
        assertThat(((String) new BuiltinParameters().value("vb.version")).contains("${"))
                .isFalse();
    }

    @Test
    void documentIdentifiersRemainMonotonicAndRejectOverflow() {
        var ids = new UuidGenerator(7);
        assertThat(ids.next()).isEqualTo(7);
        assertThat(ids.nextValue()).isEqualTo(8);
        assertThatThrownBy(() -> new UuidGenerator(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new UuidGenerator(Long.MAX_VALUE).next()).isInstanceOf(IllegalStateException.class);
    }
}
