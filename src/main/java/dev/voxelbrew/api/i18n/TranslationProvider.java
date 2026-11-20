package dev.voxelbrew.api.i18n;

import java.util.Locale;
import java.util.Map;

public interface TranslationProvider {
    Map<Locale, Map<String, String>> getTranslations();
}
