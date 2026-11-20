package dev.voxelbrew.api.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Thread-safe service for resolving and registering localized application messages. */
public interface TranslationService {
    /** @return the locale selected by the host for display text and argument formatting */
    Locale currentLocale();

    /** @return an immutable snapshot of the locales supported by this service */
    Set<Locale> supportedLocales();

    /** @return an unmodifiable map from language tags to their display names */
    Map<String, String> languages();

    /**
     * Adds a supported locale before translations are registered. Repeated calls preserve any
     * translations already registered for the locale.
     *
     * @param locale locale to add
     */
    void addSupportedLocale(Locale locale);

    /** @return the fallback locale used when the selected locale lacks a message */
    default Locale defaultLocale() {
        return Locale.ENGLISH;
    }

    /**
     * Looks up a message in the current locale and then in the default locale.
     *
     * @param key namespaced catalog key
     * @param arguments values substituted using {@link java.text.MessageFormat} when provided
     * @return localized text, or an empty optional if neither locale contains the key
     */
    Optional<String> translate(String key, Object... arguments);

    /**
     * Adds or replaces a translation for the supplied locale and key.
     *
     * @param locale locale for the translation
     * @param key namespaced catalog key
     * @param text text or message pattern
     * @return the previous registered text, or null if there was none
     */
    String registerTranslation(Locale locale, String key, String text);
}
