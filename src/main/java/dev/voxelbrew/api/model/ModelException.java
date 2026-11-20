package dev.voxelbrew.api.model;

public final class ModelException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public ModelException(String featureId, String message) {
        super("Feature '" + featureId + "': " + message);
    }

    public ModelException(String featureId, String message, Throwable cause) {
        super("Feature '" + featureId + "': " + message, cause);
    }
}
