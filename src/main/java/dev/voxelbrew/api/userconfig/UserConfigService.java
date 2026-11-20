package dev.voxelbrew.api.userconfig;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Reads and updates persistent preferences for the current user. */
public interface UserConfigService {
    /** @return the application data directory for the current user */
    Path homeFolder();

    /**
     * Reads the latest saved settings.
     *
     * @return a fresh snapshot of the saved settings, empty if none are saved
     * @throws UserConfigException if the configuration cannot be read
     */
    Map<String, String> readUserConfig();

    /**
     * @param key preference key
     * @return the latest saved value, or an empty optional if the key is absent
     * @throws UserConfigException if the configuration cannot be read
     */
    Optional<String> readUserConfig(String key);

    /**
     * Atomically merges the supplied settings with saved preferences.
     *
     * @param config non-null keys and values to add or replace
     * @throws UserConfigException if the configuration cannot be saved
     */
    void saveUserConfig(Map<String, String> config);

    /**
     * @param key preference to update
     * @param value value to save, or null to remove the preference
     * @throws UserConfigException if the preference cannot be saved or removed
     */
    void saveUserConfig(String key, String value);

    /**
     * @param key preference key to remove
     * @throws UserConfigException if the preference cannot be removed
     */
    void remove(String key);

    /**
     * Atomically removes settings and then merges the supplied replacement values.
     *
     * @param values settings to add or replace
     * @param removed keys to remove
     * @throws UserConfigException if the update cannot be saved
     */
    void update(Map<String, String> values, Set<String> removed);

    /**
     * @param key preference to remove if present
     * @throws UserConfigException if the preference cannot be removed
     */
    default void clearUserConfig(String key) {
        saveUserConfig(key, null);
    }

    /** @return the location reserved for user-installed plugins */
    default Path pluginsFolder() {
        return homeFolder().resolve("plugins");
    }

    /** @return the location reserved for user printer profiles */
    default Path profilesFolder() {
        return homeFolder().resolve("profiles");
    }

    /** @return the location reserved for user printer configurations */
    default Path printersFolder() {
        return homeFolder().resolve("printers");
    }

    /** @return the location reserved for user slicer configurations */
    default Path slicersFolder() {
        return homeFolder().resolve("slicers");
    }

    /** @return the location reserved for application logs */
    default Path logsFolder() {
        return homeFolder().resolve("log");
    }
}
