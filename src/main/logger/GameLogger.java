package main.logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class GameLogger {
    private static volatile LogSystem instance;
    private static final Object LOCK = new Object();

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    private static final String ANSI_BRIGHT_RED = "\u001B[91m";
    private static final String ANSI_BRIGHT_BLUE = "\u001B[94m";

    private GameLogger() {}

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

    public static void error(String context, String message, Throwable thrown) {
        getInstance().error(context + ": " + message, thrown);
    }

    public static void warn(String context, String messageSupplier) {
        getInstance().warn(() -> context + ": " + messageSupplier);
    }

    public static void info(String context, String messageSupplier) {
        getInstance().info(() -> context + ": " + messageSupplier);
    }
}