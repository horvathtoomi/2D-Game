package main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConsoleHandler {
    private final GamePanel gp;
    private final List<String> commandHistory;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public ConsoleHandler(GamePanel gp) {
        this.gp = gp;
        this.commandHistory = new ArrayList<>();
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.writer = new PrintWriter(System.out, true);
    }

    public void startConsoleInput() {
        if (gp.gameState != GamePanel.GameState.CONSOLE_INPUT) {
            writer.println("Console input is only available in CONSOLE_INPUT state.");
            return;
        }

        writer.println("Entering console input mode. Type 'exit' to return to the game.");
        printHelp();

        String command;
        try {
            while ((command = reader.readLine()) != null) {
                command = command.trim().toLowerCase();
                if (command.equals("exit")) {
                    break;
                }
                processCommand(command);
            }
        } catch (IOException e) {
            writer.println("An error occurred while reading input: " + e.getMessage());
        }

        gp.gameState = GamePanel.GameState.PAUSED;
        writer.println("Exiting console input mode. Returning to PAUSED state.");
    }

    private void processCommand(String command) {
        commandHistory.add(command);
        String[] parts = command.split("\\s+");

        try {
            switch (parts[0]) {
                case "help":
                    printHelp();
                    break;
                case "reset":
                    resetGame();
                    break;
                case "set":
                    if (parts.length != 3) {
                        throw new IllegalArgumentException("Invalid format for 'set' command.");
                    }
                    setGameValue(parts[1], parts[2]);
                    break;
                case "get":
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid format for 'get' command.");
                    }
                    getGameValue(parts[1]);
                    break;
                case "history":
                    printCommandHistory();
                    break;
                default:
                    writer.println("Unknown command. Type 'help' for a list of available commands.");
            }
        } catch (IllegalArgumentException e) {
            writer.println("Error: " + e.getMessage());
        }
    }

    private void printHelp() {
        writer.println("Available commands:");
        writer.println("  help - Show this help message");
        writer.println("  reset - Reset the game");
        writer.println("  set <variable> <value> - Set a game variable");
        writer.println("  get <variable> - Get the value of a game variable");
        writer.println("  history - Show command history");
        writer.println("  exit - Exit console input mode");
    }

    private void resetGame() {
        gp.resetGame();
        writer.println("Game has been reset.");
    }

    private void setGameValue(String variable, String value) {
        switch (variable) {
            case "health":
                int health = Integer.parseInt(value);
                gp.player.setHealth(health);
                writer.println("Player health set to " + health);
                break;
            case "speed":
                int speed = Integer.parseInt(value);
                gp.player.setSpeed(speed);
                writer.println("Player speed set to " + speed);
                break;
            // Add more variables as needed
            default:
                throw new IllegalArgumentException("Unknown variable: " + variable);
        }
    }

    private void getGameValue(String variable) {
        switch (variable) {
            case "health":
                writer.println("Player health: " + gp.player.getHealth());
                break;
            case "speed":
                writer.println("Player speed: " + gp.player.getSpeed());
                break;
            // Add more variables as needed
            default:
                throw new IllegalArgumentException("Unknown variable: " + variable);
        }
    }

    private void printCommandHistory() {
        writer.println("Command history:");
        for (int i = 0; i < commandHistory.size(); i++) {
            writer.printf("%d: %s%n", i + 1, commandHistory.get(i));
        }
    }
}