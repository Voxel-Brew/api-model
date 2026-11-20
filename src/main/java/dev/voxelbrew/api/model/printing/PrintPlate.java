package dev.voxelbrew.api.model.printing;

import dev.voxelbrew.api.model.BodyInstance;
import dev.voxelbrew.api.model.CadDocument;
import java.util.HashSet;
import java.util.List;
import lombok.NonNull;

/** One independent build plate in the shared printer/process setup. */
public record PrintPlate(String id, @NonNull String name, List<BodyInstance> instances, List<SupportVolume> supports) {
    public PrintPlate(String id, List<BodyInstance> instances, List<SupportVolume> supports) {
        this(id, "", instances, supports);
    }

    public PrintPlate {
        CadDocument.identifier(id, "plate ID");
        if (!name.isEmpty()) CadDocument.requireBodyName(name);
        instances = List.copyOf(instances);
        supports = List.copyOf(supports);
        var ids = new HashSet<String>();
        for (var instance : instances) {
            CadDocument.identifier(instance.id(), "instance ID");
            CadDocument.identifier(instance.bodyId(), "instance body ID");
            if (!ids.add(instance.id())) throw new IllegalArgumentException("Duplicate instance " + instance.id());
        }
        ids.clear();
        for (var support : supports) {
            if (!ids.add(support.id())) throw new IllegalArgumentException("Duplicate support " + support.id());
        }
    }

    public PrintPlate withName(@NonNull String value) {
        String normalized = value.strip();
        CadDocument.requireBodyName(normalized);
        return new PrintPlate(id, normalized, instances, supports);
    }
}
