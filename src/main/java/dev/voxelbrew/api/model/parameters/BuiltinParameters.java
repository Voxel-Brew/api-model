package dev.voxelbrew.api.model.parameters;

import dev.voxelbrew.api.model.printing.HardwareSettings;
import dev.voxelbrew.api.model.printing.PrintProfile;
import dev.voxelbrew.api.model.printing.PrintSetup;
import dev.voxelbrew.api.model.printing.ProcessSettings;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.ToDoubleFunction;
import javax.measure.Quantity;
import lombok.RequiredArgsConstructor;

/** Runtime metadata shared across document edits; save count advances only after publication. */
public final class BuiltinParameters {
    private static final PrintSetup DEFAULT_SETUP = PrintSetup.empty();
    private static final PrintProfile DEFAULT_PROFILE = PrintProfile.resolve(DEFAULT_SETUP);
    private static final Map<String, SlicerSetting> SLICER_SETTINGS = Arrays.stream(SlicerSetting.values())
            .collect(java.util.stream.Collectors.toUnmodifiableMap(SlicerSetting::key, setting -> setting));
    private final FileMetadata file;
    private final ProcessSettings process;
    private final HardwareSettings hardware;

    private static final class FileMetadata {
        private volatile long version;
        private volatile Path path;
    }

    @RequiredArgsConstructor
    private enum SlicerSetting {
        NOZZLE_DIAMETER(
                ParameterUnit.MM,
                "Printer nozzle diameter",
                s -> s.hardware == null
                        ? DEFAULT_PROFILE.printer().nozzleMm()
                        : s.hardware.printer().nozzleMm()),
        FILAMENT_DIAMETER(
                ParameterUnit.MM,
                "Filament diameter",
                s -> s.hardware == null
                        ? DEFAULT_PROFILE.material().filamentDiameter()
                        : s.hardware.filamentDiameter()),
        LAYER_HEIGHT(ParameterUnit.MM, "Layer height", s -> s.process.layerHeight()),
        WALLS(ParameterUnit.NUMBER, "Number of perimeter walls", s -> s.process.walls()),
        INFILL_PERCENT(ParameterUnit.NUMBER, "Infill percentage (0 to 100)", s -> s.process.infillPercent()),
        BRIM_WIDTH(ParameterUnit.MM, "Brim width", s -> s.process.brimWidth()),
        SUPPORT_CONTACT_GAP(ParameterUnit.MM, "Support contact gap", s -> s.process.supportContactGap()),
        SUPPORT_THRESHOLD(ParameterUnit.DEG, "Support overhang threshold", s -> s.process.supportThreshold()),
        INTERFACE_LAYERS(ParameterUnit.NUMBER, "Number of support interface layers", s -> s.process.interfaceLayers()),
        RAFT_LAYERS(ParameterUnit.NUMBER, "Number of raft layers", s -> s.process.raftLayers());

        private final ParameterUnit unit;
        private final String description;
        private final ToDoubleFunction<BuiltinParameters> read;

        String key() {
            return "vb.slicer." + name().toLowerCase(Locale.ROOT);
        }
    }

    public static boolean isString(String name) {
        return name.startsWith("vb.") && !name.equals("vb.project.version") && !SLICER_SETTINGS.containsKey(name);
    }

    private static String applicationVersion() {
        try (var input = BuiltinParameters.class.getResourceAsStream("/META-INF/voxelbrew/application.properties")) {
            if (input == null) return "0.0.0";
            var properties = new java.util.Properties();
            properties.load(input);
            return properties.getProperty("version", "0.0.0");
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Cannot read VoxelBrew application version", exception);
        }
    }

    public BuiltinParameters() {
        this(new FileMetadata(), DEFAULT_SETUP);
    }

    private BuiltinParameters(FileMetadata file, PrintSetup setup) {
        this.file = file;
        this.process = setup.process();
        this.hardware = setup.printerSnapshot();
    }

    BuiltinParameters withPrintSetup(PrintSetup next) {
        if (process.equals(next.process()) && Objects.equals(hardware, next.printerSnapshot())) {
            return this;
        }
        return new BuiltinParameters(file, next);
    }

    Map<String, Quantity<?>> numericValues() {
        var values = new LinkedHashMap<String, Quantity<?>>();
        values.put("vb.project.version", ParameterUnit.NUMBER.quantity(fileVersion()));
        for (var setting : SlicerSetting.values()) {
            values.put(setting.key(), setting.unit.quantity(setting.read.applyAsDouble(this)));
        }
        return values;
    }

    /** File location is runtime context, never embedded into portable project definitions. */
    public void projectPath(Path path) {
        file.path = path == null ? null : path.toAbsolutePath().normalize();
    }

    private String projectLocation(String property) {
        Path projectPath = file.path;
        if (projectPath == null) return property.equals("name") ? "Untitled" : "";
        Path file = projectPath.getFileName();
        String filename = file == null ? "" : file.toString();
        Path parent = projectPath.getParent();
        return switch (property) {
            case "path" -> projectPath.toString();
            case "folder" -> parent == null ? "" : parent.toString();
            case "filename" -> filename;
            case "name" -> {
                int dot = filename.lastIndexOf('.');
                yield dot > 0 ? filename.substring(0, dot) : filename;
            }
            default -> throw new IllegalArgumentException("Unknown project property: " + property);
        };
    }

    public long fileVersion() {
        return file.version;
    }

    public void fileVersion(long value) {
        if (value < 0) throw new IllegalArgumentException("Negative file version");
        file.version = value;
    }

    public Object value(String name) {
        var setting = SLICER_SETTINGS.get(name);
        if (setting != null) return setting.unit.quantity(setting.read.applyAsDouble(this));
        if (name.equals("vb.project.version")) return ParameterUnit.NUMBER.quantity(fileVersion());
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        return switch (name) {
            case "vb.project.name" -> projectLocation("name");
            case "vb.project.path" -> projectLocation("path");
            case "vb.project.folder" -> projectLocation("folder");
            case "vb.project.filename" -> projectLocation("filename");
            case "vb.version" -> applicationVersion();
            case "vb.date" -> now.toLocalDate().toString();
            case "vb.time" -> now.format(DateTimeFormatter.ISO_OFFSET_TIME);
            case "vb.date_time" -> now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            case "vb.os.name" -> System.getProperty("os.name", "unknown");
            case "vb.os.version" -> System.getProperty("os.version", "unknown");
            case "vb.os.arch" -> System.getProperty("os.arch", "unknown");
            default -> throw new IllegalArgumentException("Unknown built-in parameter: " + name);
        };
    }

    public List<ParameterSet.Parameter> parameters() {
        var rows = new ArrayList<>(List.of(
                row("version", "Running application version"),
                row("project.version", "Successful save count; unsaved and legacy documents start at zero"),
                row("project.name", "Project filename without its extension; Untitled before first save"),
                row("project.path", "Absolute project file path; empty before first save"),
                row("project.folder", "Absolute project folder; empty before first save"),
                row("project.filename", "Project filename including its extension; empty before first save"),
                row("date", "Current ISO date in UTC"),
                row("date_time", "Current ISO date and time in UTC"),
                row("time", "Current ISO time in UTC"),
                row("os.name", "Operating system name"),
                row("os.version", "Operating system version"),
                row("os.arch", "Operating system architecture")));
        for (var setting : SlicerSetting.values()) {
            String key = setting.key();
            rows.add(new ParameterSet.Parameter(
                    key, key, setting.unit, Expressions.format(value(key)), setting.description + " (read-only)"));
        }
        return List.copyOf(rows);
    }

    private ParameterSet.Parameter row(String suffix, String comment) {
        String name = "vb." + suffix;
        String value = Expressions.format(value(name));
        String expression = isString(name) ? "=\"" + value.replace("\"", "\"\"") + "\"" : value;
        return new ParameterSet.Parameter(name, name, ParameterUnit.NUMBER, expression, comment + " (read-only)");
    }
}
