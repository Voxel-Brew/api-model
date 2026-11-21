package dev.voxelbrew.api.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TranslationServiceTest {
    private static final class TestTranslationService implements TranslationService {
        @Override
        public Locale currentLocale() {
            return Locale.ENGLISH;
        }

        @Override
        public Set<Locale> supportedLocales() {
            return Set.of(Locale.ENGLISH);
        }

        @Override
        public Map<String, String> languages() {
            return Map.of("en", "English");
        }

        @Override
        public void addSupportedLocale(Locale locale) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<String> tryTranslate(String key, Object... arguments) {
            return "test.greeting".equals(key) ? Optional.of("Hello " + arguments[0]) : Optional.empty();
        }

        @Override
        public String registerTranslation(Locale locale, String key, String text) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    void translateUsesResolvedTextAndFormatsMissingKeys() {
        TranslationService service = new TestTranslationService();

        assertThat(service.translate("test.greeting", "Ada")).isEqualTo("Hello Ada");
        assertThat(service.tryTranslate("test.missing")).isEmpty();
        assertThat(service.translate("test.missing")).isEqualTo("en:test.missing[]");
        assertThat(service.translate("test.missing", "Ada", 2)).isEqualTo("en:test.missing[Ada, 2]");
    }
}
