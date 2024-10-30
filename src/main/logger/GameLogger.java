package main.logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.function.Supplier;

public final class GameLogger {
    // Private static instance - lazily initialized
    private static volatile LogSystem instance;
    private static final Object LOCK = new Object();

    //Color
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    private static final String ANSI_BRIGHT_RED = "\u001B[91m";
    private static final String ANSI_BRIGHT_BLUE = "\u001B[94m";

    // Prevent instantiation
    private GameLogger() {}

    // Private getter for the singleton instance
    private static LogSystem getInstance() {
        LogSystem result = instance;
        if (result == null) {
            synchronized (LOCK) {
                result = instance;
                if (result == null) {
                    instance = result = initializeLogger();
                }
            }
        }
        return result;
    }

    private static LogSystem initializeLogger() {
        LogSystem logSystem = LogSystem.getInstance();
        logSystem.setConsoleFormatter(new ColorFormatter());
        return logSystem;
    }

    private static class ColorFormatter extends Formatter {
        private static final String FORMAT = "[%s] [%s] %s%n";

        @Override
        public String format(LogRecord record){
            String levelColor = switch(record.getLevel().getName()){
                case "INFO" -> ANSI_BRIGHT_GREEN;
                case "SEVERE" -> ANSI_BRIGHT_RED;
                default -> ANSI_BRIGHT_BLUE;
            };

            return String.format(FORMAT,
                    record.getLevel().getName(),
                    Thread.currentThread().getName(),
                    levelColor + record.getMessage() + ANSI_RESET
            );
        }
    }

    // Static facade methods for logging
    public static void gameState(String context, Supplier<String> messageSupplier) {
        getInstance().gameState(() -> context + ": " + messageSupplier.get());
    }

    public static void entityEvent(String context, Supplier<String> messageSupplier) {
        getInstance().entityEvent(() -> context + ": " + messageSupplier.get());
    }

    public static void error(String context, String message, Throwable thrown) {
        getInstance().error(context + ": " + message, thrown);
    }

    public static void warn(String context, String messageSupplier) {
        getInstance().warn(() -> context + ": " + messageSupplier);
    }

    public static void info(String context, String messageSupplier) {
        getInstance().info(() -> context + ": " + messageSupplier);
    }

    public static void debug(String context, Supplier<String> messageSupplier) {
        getInstance().debug(() -> context + ": " + messageSupplier.get());
    }

    public static void performance(String context, Supplier<String> messageSupplier) {
        getInstance().performance(() -> context + ": " + messageSupplier.get());
    }

    // Cleanup method
    public static void cleanup() {
        synchronized (LOCK) {
            if (instance != null) {
                instance.cleanup();
                instance = null;
            }
        }
    }
}