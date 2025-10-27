package ru.nikskul.logger;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SystemLogger {

    private static final PrintWriter PRINT_WRITER = new PrintWriter(
        new OutputStreamWriter(System.out)
    );

    private SystemLogger() {}

    public static void log(String message, Class<?> clazz, Object... args) {
        System.out.printf(
            "%s: %s %s: %s%n",
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
            Thread.currentThread().getName(),
            clazz.getSimpleName(),
            String.format(message, args)
        );
    }

    public static void log(Throwable e, Class<?> clazz) {
        System.out.printf(
            "%s: %s %s: %s%n",
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
            Thread.currentThread().getName(),
            clazz.getSimpleName(),
            e.toString()
        );
        e.printStackTrace(PRINT_WRITER);
    }
}
