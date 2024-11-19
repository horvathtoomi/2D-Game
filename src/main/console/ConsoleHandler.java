package main.console;

import java.util.HashMap;
import java.util.Map;
import main.Engine;

public class ConsoleHandler {
    private final Engine gp;
    private final Commands commands;
    public boolean abortProcess;
    private final Map<String, Command> commandMap;
    private ConsoleGUI consoleGUI;

    public ConsoleHandler(Engine gp) {
        this.gp = gp;
        this.commands = new Commands(gp, this);  // Pass ConsoleHandler to Commands
        this.abortProcess = false;
        this.commandMap = initializeCommands();
    }

    public ConsoleGUI getConsoleGUI() {
        return consoleGUI;
    }

    public void printToConsole(String message) {
        if (consoleGUI != null && consoleGUI.isVisible()) {
            consoleGUI.appendToConsole(message);
        }
    }

    private Map<String, Command> initializeCommands() {
        Map<String, Command> map = new HashMap<>();

        map.put("help", args -> {
            if (args.length < 2) {
                String helpText = getHelpText();
                printToConsole(helpText);
            } else {
                commands.printHelp(args[1]);
            }
        });

        map.put("reset", _ -> {
            gp.startGame();
            printToConsole("Game has been reset");
        });

        map.put("exit", _ -> {
            abortProcess = true;
            if (consoleGUI != null) {
                consoleGUI.dispose();
            }
            gp.setGameState(Engine.GameState.PAUSED);
        });

        map.put("exit_game", _ -> System.exit(0));

        map.put("remove", args -> {
            if (args.length == 2) {
                commands.removeEntities(args[1], args[1].equalsIgnoreCase("all"));
            } else {
                printToConsole("Invalid format! Use 'help remove' for correct usage");
            }
        });

        map.put("save", args -> {
            if (args.length == 2) {
                commands.saveFile(args[1]);
            } else {
                printToConsole("Invalid format! Use 'help save' for correct usage");
            }
        });

        map.put("load", args -> {
            if (args.length == 2) {
                commands.loadFile(args[1]);
            } else {
                printToConsole("Invalid format! Use 'help load' for correct usage");
            }
        });

        map.put("set", args -> {
            switch (args.length) {
                case 4 -> {
                    if (args[1].equals("player")) {
                        commands.setGameValue(args[2], args[3]);
                    } else if (args[1].equals("entity")) {
                        commands.setAll(args[2], Integer.parseInt(args[3]));
                    } else {
                        commands.setEntity(args[1], args[2], Integer.parseInt(args[3]));
                    }
                }
                case 3 -> commands.setGameValue(args[1], args[2]);
                default -> printToConsole("""
                    Invalid format for 'set' command.
                    Usage: set entity arg1 value
                    Entity types: *Enemy, Player
                    Arguments: speed, health, maxhealth""");
            }
        });

        map.put("get", args -> {
            switch (args.length) {
                case 3 -> {
                    switch (args[1]) {
                        case "player" -> commands.getGameValue(args[2]);
                        case "smallenemy", "giantenemy", "dragonenemy", "friendlyenemy" ->
                                commands.getEntity(args[1], args[2]);
                        default -> printToConsole("Unknown entity type! Use 'help' for available commands");
                    }
                }
                case 2 -> commands.getGameValue(args[1]);
                default -> printToConsole("""
                    Invalid format for 'get' command.
                    Usage: get entity arg1
                    Entity types: *Enemy, Player
                    Arguments: speed, health""");
            }
        });

        map.put("add", args -> {
            if (args.length == 4) {
                commands.add(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            } else {
                printToConsole("""
                    Invalid format for 'add' command.
                    Usage: add <entity/object> X Y
                    Entities: GiantEnemy, SmallEnemy, DragonEnemy, FriendlyEnemy
                    Objects: chest, door, key, boots""");
            }
        });

        map.put("teleport", args -> {
            if(args.length == 3){
                commands.teleport(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            }
            else{
                printToConsole("""
                        Invalid format for 'teleport' command.
                        Usage: teleport X Y
                        """);
            }
        });

        map.put("script", args -> {
            if (args.length == 2) {
                commands.runScript(args[1]);
            } else {
                printToConsole("Invalid format! Use 'help script' for correct usage");
            }
        });

        map.put("make", args -> {
            if (args.length == 2) {
                commands.createFile(args[1], consoleGUI);
            } else {
                printToConsole("Invalid format! Use 'help make' for correct usage");
            }
        });

        return map;
    }

    public void startConsoleInput() {
        if (gp.getGameState() != Engine.GameState.CONSOLE_INPUT) {
            printToConsole("Console is only available in CONSOLE_INPUT state");
            return;
        }
        if (consoleGUI == null) {
            consoleGUI = new ConsoleGUI(gp, this);
        }
        consoleGUI.showConsole();
        consoleGUI.appendToConsole(getHelpText());
    }

    public void executeCommand(String input) {
        if (input.isEmpty()) return;
        String[] parts = input.trim().toLowerCase().split("\\s+");
        Command command = commandMap.get(parts[0]);
        if (command != null) {
            try {
                command.execute(parts);
            } catch (Exception exc) {
                printToConsole("Error executing command: " + exc.getMessage());
            }
        } else {
            printToConsole("Unknown command. Type 'help' for available commands.");
        }
    }

    private String getHelpText() {
        return """
                            Available commands:
            -------------------------------------------------------
            | help [command] : Show help for a specific command   |
            | reset          : Reset the game                     |
            | exit           : Exit console mode                  |
            | exit_game      : Exit the game                      |
            | remove         : Remove entities                    |
            | save/load      : Save/Load game state               |
            | set/get        : Set/Get game values                |
            | add            : Add entities or objects            |
            | teleport       : Teleports player                   |
            | script         : Run a script file                  |
            | make           : Create a new script file           |
            -------------------------------------------------------
            Type 'help <command>' for detailed usage information.""";
    }
}