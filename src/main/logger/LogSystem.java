package main.logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.*;

/**
 * A naplózó rendszer alapvető működését megvalósító osztály.
 * Kezeli a naplófájlokat, a naplózási szinteket és a formázást.
 */
public final class LogSystem {
    private static volatile LogSystem instance;
    private static final Object INSTANCE_LOCK = new Object();

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    private static final String ANSI_BRIGHT_RED = "\u001B[91m";
    private static final String ANSI_BRIGHT_BLUE = "\u001B[94m";

    private static final int MAX_RECENT_LOGS = 100;
    private static final int LOG_QUEUE_CAPACITY = 10000;
    private static final int LOG_WORKER_THREADS = 2;


    private final Logger logger;
    private final ConcurrentLinkedQueue<String> recentLogs;
    private final BlockingQueue<LogRecord> logQueue;
    private final ExecutorService loggerExecutor;
    private final AtomicBoolean isRunning;
    private final AtomicInteger activeLoggers;

    private volatile Handler fileHandler;
    private volatile Handler consoleHandler;
    private Formatter consoleFormatter;

    private LogSystem() {
        this.logger = Logger.getLogger(LogSystem.class.getName());
        this.recentLogs = new ConcurrentLinkedQueue<>();
        this.logQueue = new ArrayBlockingQueue<>(LOG_QUEUE_CAPACITY);
        this.loggerExecutor = createLoggerExecutor();
        this.isRunning = new AtomicBoolean(true);
        this.activeLoggers = new AtomicInteger(0);
        this.consoleFormatter = createColorFormatter();

        initializeLogger();
        startLogWorkers();
    }

    public static LogSystem getInstance() {
        LogSystem result = instance;
        if (result == null) {
            synchronized (INSTANCE_LOCK) {
                result = instance;
                if (result == null) {
                    instance = result = new LogSystem();
                }
            }
        }
        return result;
    }

    /**
     * Létrehozza a naplózó szálakat kezelő ExecutorService-t.
     * @return az inicializált ExecutorService
     */
    private ExecutorService createLoggerExecutor() {
        return Executors.newFixedThreadPool(LOG_WORKER_THREADS, new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "LogWorker-" + counter.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    private void initializeLogger() {

        consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(consoleFormatter);
        // Configure logger
        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
    }

    private Formatter createColorFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                String color = switch (record.getLevel().getName()) {
                    case "INFO" -> ANSI_BRIGHT_GREEN;
                    case "SEVERE" -> ANSI_BRIGHT_RED;
                    default -> ANSI_BRIGHT_BLUE;
                };

                return String.format("[%s] [%s] %s%s%s%n",
                        record.getLevel(),
                        Thread.currentThread().getName(),
                        color,
                        record.getMessage(),
                        ANSI_RESET);
            }
        };
    }

    /**
     * Elindítja a naplózó szálakat.
     */
    private void startLogWorkers() {
        for (int i = 0; i < LOG_WORKER_THREADS; i++) {
            loggerExecutor.submit(this::processLogQueue);
        }
    }

    /**
     * Feldolgozza a naplózási sorban lévő bejegyzéseket.
     * Külön szálban fut, amíg a rendszer aktív.
     */
    private void processLogQueue() {
        activeLoggers.incrementAndGet();
        try {
            while (isRunning.get() || !logQueue.isEmpty()) {
                try {
                    LogRecord record = logQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (record != null) {
                        synchronized (logger) {
                            logger.log(record);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } finally {
            if (activeLoggers.decrementAndGet() == 0 && !isRunning.get()) {
                shutdownHandlers();
            }
        }
    }

    /**
     * Sorba állítja a naplózási kéréseket.
     * @param level a naplózási szint
     * @param messageSupplier az üzenet szolgáltató függvény
     */
    private void queueLog(Level level, Supplier<String> messageSupplier) {
        if (logger.isLoggable(level) && isRunning.get()) {
            try {
                String message = messageSupplier.get();
                LogRecord record = new LogRecord(level, message);
                record.setSourceClassName(logger.getName());

                if (!logQueue.offer(record, 100, TimeUnit.MILLISECONDS)) {
                    synchronized (logger) {
                        logger.warning("Log queue is full - message dropped: " + message);
                    }
                }

                addToRecentLogs(level, message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Hozzáadja a bejegyzést a legutóbbi naplók listájához.
     * @param level a naplózási szint
     * @param message az üzenet
     */
    private void addToRecentLogs(Level level, String message) {
        recentLogs.offer(String.format("[%s] %s", level, message));
        while (recentLogs.size() > MAX_RECENT_LOGS) {
            recentLogs.poll();
        }
    }

    /**
     * Hibaüzenet naplózása kivétellel.
     * @param message az üzenet
     * @param thrown a kivétel
     */
    public void error(String message, Throwable thrown) {
        queueLog(Level.SEVERE, () -> {
            StringBuilder sb = new StringBuilder(message);
            if (thrown != null) {
                sb.append(": ").append(thrown.getMessage());
                for (StackTraceElement element : thrown.getStackTrace()) {
                    sb.append("\n    at ").append(element);
                }
            }
            return sb.toString();
        });
    }

    /**
     * Figyelmeztetés naplózása.
     * @param messageSupplier az üzenet szolgáltató függvény
     */
    public void warn(Supplier<String> messageSupplier) {
        queueLog(Level.WARNING, messageSupplier);
    }

    /**
     * Információs üzenet naplózása.
     * @param messageSupplier az üzenet szolgáltató függvény
     */
    public void info(Supplier<String> messageSupplier) {
        queueLog(Level.INFO, messageSupplier);
    }

    private void shutdownHandlers() {
        synchronized (INSTANCE_LOCK) {
            if (fileHandler != null) {
                fileHandler.close();
                logger.removeHandler(fileHandler);
                fileHandler = null;
            }
            if (consoleHandler != null) {
                consoleHandler.close();
                logger.removeHandler(consoleHandler);
                consoleHandler = null;
            }
        }
    }

    /**
     * Beállítja a konzol kimenet formázóját.
     * @param formatter az új formázó
     */
    public void setConsoleFormatter(Formatter formatter) {
        this.consoleFormatter = formatter;
        if (consoleHandler != null) {
            consoleHandler.setFormatter(formatter);
        }
    }
}