package dev.voxelbrew.api.model;

import static org.assertj.core.api.Assertions.*;

import dev.voxelbrew.api.model.mesh.MeshOptions;
import dev.voxelbrew.api.model.parameters.BuiltinParameters;
import dev.voxelbrew.api.model.printing.ManufacturingModel;
import dev.voxelbrew.api.model.printing.PrintProfile;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ModelApiTest {
    @Test
    void slicerQuantitiesFollowTheDocumentSnapshot() {
        var base = CadDocument.empty();
        var changed = new CadDocument(
                base.id(), base.features(), base.cursor(), base.printSetup().withNozzle(0.8), base.parameters());
        var unit = dev.voxelbrew.api.model.parameters.ParameterUnit.MM;
        var parameters = changed.parameters().put("wall", unit, "$vb.slicer.nozzle_diameter * $vb.slicer.walls", "");
        assertThat(parameters.value("wall", unit)).isEqualTo(1.6);
        assertThat(base.parameters().value("$vb.slicer.nozzle_diameter", unit)).isEqualTo(0.4);
        assertThat(parameters.value("$vb.slicer.layer_height", unit)).isEqualTo(0.2);
        assertThat(parameters.allParameters())
                .filteredOn(p -> p.name().equals("vb.slicer.nozzle_diameter"))
                .singleElement()
                .satisfies(p -> assertThat(p.unit()).isEqualTo(unit));
        assertThat(BuiltinParameters.isString("vb.slicer.nozzle_diameter")).isFalse();
        assertThatThrownBy(() -> parameters.remove("vb.slicer.layer_height"))
                .isInstanceOf(IllegalArgumentException.class);
    }

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
        for (double value : new double[] {0, -1, Double.NaN, Double.POSITIVE_INFINITY, 10001}) {
            assertThatThrownBy(() -> PrintProfile.rectangularPrinterId(value, 200, 200))
                    .isInstanceOf(IllegalArgumentException.class);
        }
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
        assertThat(new BuiltinParameters().value("vb.version")).isEqualTo("0.0.0");
    }

    @Test
    void projectLocationHandlesFilesystemRoot() {
        Path root = Path.of("").toAbsolutePath().getRoot();
        var parameters = new BuiltinParameters();
        parameters.projectPath(root);

        assertThat(parameters.value("vb.project.path")).isEqualTo(root.toString());
        assertThat(parameters.value("vb.project.folder")).isEqualTo("");
        assertThat(parameters.value("vb.project.filename")).isEqualTo("");
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
