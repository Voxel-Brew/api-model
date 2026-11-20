package dev.voxelbrew.api.i18n;

import java.util.Locale;
import java.util.Map;

/** Supplies message keys and translated text grouped by locale for the host translation service. */
public interface TranslationProvider {
    Map<Locale, Map<String, String>> getTranslations();
}
