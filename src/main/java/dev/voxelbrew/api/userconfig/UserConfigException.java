package dev.voxelbrew.api.userconfig;

/** Indicates a failure to read or persist user configuration. */
public final class UserConfigException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * @param message description of the failed configuration operation
     * @param cause underlying failure
     */
    public UserConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
