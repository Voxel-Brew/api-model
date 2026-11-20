package dev.voxelbrew.api.i18n;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/** Loads the properties bundles for the locales declared by an API module. */
public abstract class PropertiesTranslationProvider implements TranslationProvider {
    private final String bundleName;

    protected PropertiesTranslationProvider(String bundleName) {
        this.bundleName = bundleName;
    }

    @Override
    public Map<Locale, Map<String, String>> getTranslations() {
        var translations = new HashMap<Locale, Map<String, String>>();
        for (Locale locale : getLocales()) {
            translations.put(locale, messages(locale));
        }
        return Map.copyOf(translations);
    }

    protected abstract List<Locale> getLocales();

    private Map<String, String> messages(Locale locale) {
        var control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);
        var bundle = ResourceBundle.getBundle(bundleName, locale, getClass().getClassLoader(), control);
        var entries = new HashMap<String, String>();
        bundle.keySet().forEach(key -> entries.put(key, bundle.getString(key)));
        return Map.copyOf(entries);
    }
}
