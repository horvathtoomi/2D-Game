package main.console;


import main.GamePanel;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConsoleHandler {
    private final GamePanel gp;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Commands commands;
    public boolean abortProcess;
    private final Map<String, Command> commandMap;

    public ConsoleHandler(GamePanel gp) {
        this.gp = gp;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.writer = new PrintWriter(System.out, true);
        this.commands = new Commands(gp);
        this.abortProcess = false;
        this.commandMap = initializeCommands();
    }

    private Map<String, Command> initializeCommands() {
        Map<String, Command> map = new HashMap<>();

        map.put("help", args -> {
            if (args.length < 2) {
                commands.printHelp(" ");
            } else {
                commands.printHelp(args[1]);
            }
        });

        map.put("reset", _ -> {
            gp.resetGame();
            writer.println("Game has been reset.");
        });

        map.put("exit", _ -> abortProcess = true);

        map.put("exit_game", _ -> System.exit(0));

        map.put("remove", args -> {
            if (args.length == 2) {
                commands.removeEntities(args[1], args[1].equalsIgnoreCase("all"));
            } else {
                throw new IllegalArgumentException("Invalid format! remove <entity_name>");
            }
        });

        map.put("save", args -> {
            if (args.length == 2) {
                commands.saveFile(args[1]);
            } else {
                throw new IllegalArgumentException("Invalid format! save <filename.extension>");
            }
        });

        map.put("load", args -> {
            if (args.length == 2) {
                commands.loadFile(args[1]);
            } else {
                throw new IllegalArgumentException("Invalid format! load <filename.extension>");
            }
        });

        map.put("set", args -> {
            if (args.length == 4) {
                if (args[1].equals("player")) {
                    commands.setGameValue(args[2], args[3]);
                } else if (args[1].equals("entity")) {
                    commands.setAll(args[2], Integer.parseInt(args[3]));
                } else {
                    commands.setEntity(args[1], args[2], Integer.parseInt(args[3]));
                }
            } else if (args.length == 3) {
                commands.setGameValue(args[1], args[2]);
            } else {
                throw new IllegalArgumentException("""
                    Invalid format for 'set' command.
                    | Wrong Format | set entity arg1 value
                    ->entity: *Enemy, Player, args: speed,health,maxhealth""");
            }
        });

        map.put("get", args -> {
            if (args.length == 3) {
                switch (args[1]) {
                    case "player" -> commands.getGameValue(args[2]);
                    case "smallenemy", "giantenemy", "dragonenemy", "friendlyenemy" ->
                            commands.getEntity(args[1], args[2]);
                    default -> writer.println("Unknown command. Type 'help' for a list of available commands.");
                }
            } else if (args.length == 2) {
                commands.getGameValue(args[1]);
            } else {
                throw new IllegalArgumentException("""
                    Invalid format for 'get' command.
                    | Wrong Format | get entity arg1
                    ->entity: *Enemy, Player, args: speed,health""");
            }
        });

        map.put("add", args -> {
            if (args.length == 4) {
                commands.add(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            } else {
                throw new IllegalArgumentException("""
                    Invalid format for 'add'
                    | add <entity/object> X Y
                    entity: Giant-,Small-,Dragon-,Friendly-Enemy
                    object: chest,door,key,boots""");
            }
        });

        map.put("script", args -> {
            if (args.length == 2) {
                commands.runScript(args[1]);
            } else {
                throw new IllegalArgumentException("Invalid format for 'script' command.\n| Wrong Format | scripts <filename> -> filename.extension");
            }
        });

        map.put("make", args -> {
            if (args.length == 2) {
                commands.createFile(args[1], reader);
            } else {
                throw new IllegalArgumentException("Invalid format for 'make' command.\n| Wrong Format | make <filename>");
            }
        });

        return map;
    }

    public void startConsoleInput() {
        if (gp.gameState != GamePanel.GameState.CONSOLE_INPUT) {
            writer.println("Console input is only available in CONSOLE_INPUT state.");
            return;
        }
        writer.println("Entering console input mode. Type 'exit' to return to the game.");
        printHelp();

        String input;
        try {
            while (!abortProcess && (input = reader.readLine()) != null) {
                executeCommand(input.trim());
            }
        } catch (IOException e) {
            writer.println("An error occurred while reading input: " + e.getMessage());
        }

        abortProcess = false;
        gp.gameState = GamePanel.GameState.PAUSED;
        writer.println("Exiting console input mode. Returning to PAUSED state.");
    }

    public void executeCommand(String input) {
        if (input.isEmpty()) return;

        String[] parts = input.trim().toLowerCase().split("\\s+");
        Command command = commandMap.get(parts[0]);

        if (command != null) {
            try {
                command.execute(parts);
            } catch (IllegalArgumentException e) {
                writer.println("Error: " + e.getMessage());
            } catch (Exception e) {
                writer.println("An error occurred while executing the command: " + e.getMessage());
            }
        } else {
            writer.println("Unknown command. Type 'help' for a list of available commands.");
        }
    }

    private void printHelp() {
        writer.println("""
            Available commands:
            - help [command]   : Show help for a specific command or list all commands
            - reset            : Reset the game
            - exit             : Exit console mode
            - exit_game        : Exit the game
            - remove <entity>  : Remove entities
            - save/load <file> : Save/Load game state
            - set/get ...      : Set/Get various game values
            - add ...          : Add entities or objects
            - script <file>    : Run a script file
            - make <file>      : Create a new script file
            Type 'help <command>' for more details about a specific command.""");
    }
}