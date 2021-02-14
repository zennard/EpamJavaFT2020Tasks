package ua.testing.demo_jpa.util;

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
        log.info("{}", LocaleContextHolder.getLocale());

        ResourceBundle bundle = ResourceBundle.getBundle("messages/messages",
                getCurrentLocale());
        return bundle.getString(key);
    }

    public static String getMessage(String key, Object... arguments) {
        return MessageFormat.format(getMessage(key), arguments);
    }

    public static String getMessage(Enum<?> enumVal) {
        log.info("{}", enumVal);
        String key = enumVal.getClass().getSimpleName().toLowerCase() + '.' + enumVal.name().toLowerCase();
        log.info("{}", key);
        return getMessage(key);
    }

    public static String getMessageFromException(Throwable exception) {
        //@TODO transform exception class name to have dot format
        log.info(exception.getClass().getSimpleName().toLowerCase());
        String key = exception.getClass().getSimpleName().toLowerCase();
        return getMessage(key);
    }

    public static Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }
}
