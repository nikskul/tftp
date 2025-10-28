package ru.nikskul.logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SystemLogger {

    private static final Thread.Builder.OfVirtual loggerThread
        = Thread.ofVirtual().name("System-Logger");

    private static final PrintWriter PRINT_WRITER;

    static {
        Path filename = Path.of(String.format(
            "log/%s.txt",
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy__HH_mm_ss")
            )
        ));
        try {
            var dir = Path.of("log");
            if (Files.notExists(dir)) {
                Files.createDirectory(dir);
            }
            if (!Files.notExists(filename)) {
                Files.createFile(filename);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            PRINT_WRITER = new PrintWriter(Files.newOutputStream(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SystemLogger() {}

    public static void log(String message, Class<?> clazz, Object... args) {
        loggerThread.start(() -> {
            synchronized (PRINT_WRITER) {
                PRINT_WRITER.printf(
                    "%s: %s — %s%n",
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
                    clazz.getSimpleName(),
                    String.format(message, args)
                );
                PRINT_WRITER.flush();
            }
        });
    }

    public static void log(Throwable e, Class<?> clazz) {
        loggerThread.start(() -> {
            synchronized (PRINT_WRITER) {
                PRINT_WRITER.printf(
                    "%s: %s — %s%n",
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
                    clazz.getSimpleName(),
                    e.toString()
                );
                e.printStackTrace(PRINT_WRITER);
                PRINT_WRITER.flush();
            }
        });
    }
}
