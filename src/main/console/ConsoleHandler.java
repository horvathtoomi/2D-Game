package main.console;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import main.GamePanel;
import main.logger.GameLogger;

public class ConsoleHandler {
    private final GamePanel gp;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Commands commands;
    public boolean abortProcess;
    private final Map<String, Command> commandMap;
    private final String LOG_CONTEXT = "[CONSOLE HANDLER]";

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

        map.put("reset", name -> {
            gp.resetGame();
            GameLogger.info(LOG_CONTEXT, "GAME HAS BEEN RESET");
        });

        map.put("exit", name -> abortProcess = true);

        map.put("exit_game", name -> System.exit(0));

        map.put("remove", args -> {
            if (args.length == 2) {
                commands.removeEntities(args[1], args[1].equalsIgnoreCase("all"));
            } else {
                GameLogger.error(LOG_CONTEXT, "Invalid format! Use help remove", new IllegalArgumentException());
            }
        });

        map.put("save", args -> {
            if (args.length == 2) {
                commands.saveFile(args[1]);
            } else {
                GameLogger.warn(LOG_CONTEXT, "Invalid format! Use 'help save'");
            }
        });

        map.put("load", args -> {
            if (args.length == 2) {
                commands.loadFile(args[1]);
            } else {
                GameLogger.error(LOG_CONTEXT, "Invalid format! Use help load", new IllegalArgumentException());
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
                default -> GameLogger.warn(LOG_CONTEXT,"""
                        Invalid format for 'set' command.
                        | Wrong Format | set entity arg1 value
                        ->entity: *Enemy, Player, args: speed,health,maxhealth""");
            }
        });

        map.put("get", args -> {
            switch (args.length) {
                case 3 -> {
                    switch (args[1]) {
                        case "player" -> commands.getGameValue(args[2]);
                        case "smallenemy", "giantenemy", "dragonenemy", "friendlyenemy" ->
                            commands.getEntity(args[1], args[2]);
                        default -> GameLogger.warn(LOG_CONTEXT, "Unknown command! Use 'help'");
                    }
                }
                case 2 -> commands.getGameValue(args[1]);
                default ->
                        GameLogger.warn(LOG_CONTEXT,"""
                        Invalid format for 'get' command.
                        | Wrong Format | get entity arg1
                        ->entity: *Enemy, Player, args: speed, health""");
            }
        });

        map.put("add", args -> {
            if (args.length == 4) {
                commands.add(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            } else {
                GameLogger.warn(LOG_CONTEXT,"""
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
                GameLogger.warn(LOG_CONTEXT, "Invalid format! Use 'help'");
            }
        });

        map.put("make", args -> {
            if (args.length == 2) {
                commands.createFile(args[1], reader);
            } else {
                GameLogger.warn(LOG_CONTEXT, "Invalid format! Use 'help'");
            }
        });

        return map;
    }

    public void startConsoleInput() {
        if (gp.getGameState() != GamePanel.GameState.CONSOLE_INPUT) {
            GameLogger.warn(LOG_CONTEXT, "Only available in CONSOLE_INPUT state");
            return;
        }
        GameLogger.info(LOG_CONTEXT, "ENTERING CONSOLE INPUT. TYPE 'exit' to return");
        printHelp();

        String input;
        try {
            while (!abortProcess && (input = reader.readLine()) != null) {
                executeCommand(input.trim());
            }
        } catch (IOException e) {
            GameLogger.warn(LOG_CONTEXT, "Error occured while reading input: " + e.getMessage());
        }

        abortProcess = false;
        gp.setGameState(GamePanel.GameState.PAUSED);
        GameLogger.info(LOG_CONTEXT, "EXITING CONSOLE INPUT MODE");
    }

    public void executeCommand(String input) {
        if (input.isEmpty()) return;

        String[] parts = input.trim().toLowerCase().split("\\s+");
        Command command = commandMap.get(parts[0]);

        if (command != null) {
            try {
                command.execute(parts);
            } catch (IllegalArgumentException exc) {
                GameLogger.error(LOG_CONTEXT, "COMMAND EXECUTION DID NOT SUCCEED", exc);
            }
        } else {
            GameLogger.warn(LOG_CONTEXT, "UNKNOWN COMMAND, USE 'help'");
        }
    }

    private void printHelp() {
        GameLogger.info(LOG_CONTEXT, """
            Available commands:
            \t- help [command]   : Show help for a specific command or list all commands
            \t- reset            : Reset the game
            \t- exit             : Exit console mode
            \t- exit_game        : Exit the game
            \t- remove <entity>  : Remove entities
            \t- save/load <file> : Save/Load game state
            \t- set/get ...      : Set/Get various game values
            \t- add ...          : Add entities or objects
            \t- script <file>    : Run a script file
            \t- make <file>      : Create a new script file
            Type 'help <command>' for more details about a specific command.""");
    }
}