package ua.testing.demo_jpa.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
public class Internationalization {

    private Internationalization() {
    }

    public static String getMessage(String key) {
        log.error("{}", LocaleContextHolder.getLocale());

        ResourceBundle bundle = ResourceBundle.getBundle("messages/messages",
                getCurrentLocale());
        return bundle.getString(key);
    }

    public static String getMessage(String key, Object... arguments) {
        return MessageFormat.format(getMessage(key), arguments);
    }

    public static String getMessage(Enum<?> enumVal) {
        log.error("{}", enumVal);
        String key = enumVal.getClass().getSimpleName().toLowerCase() + '.' + enumVal.name().toLowerCase();
        log.error("{}", key);
        return getMessage(key);
    }

    public static Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }
}
