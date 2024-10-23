package main;

import serializable.FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConsoleHandler {
    private final GamePanel gp;
    private final List<String> commandHistory;
    private final BufferedReader reader;
    private final PrintWriter writer;
    public boolean abortProcess;

    public ConsoleHandler(GamePanel gp) {
        this.gp = gp;
        this.commandHistory = new ArrayList<>();
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.writer = new PrintWriter(System.out, true);
        abortProcess = false;
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
            do{
                command = reader.readLine();
                if(command != null) {
                    command = command.trim().toLowerCase();
                    processCommand(command);
                }
            }while(!abortProcess);
        } catch (IOException e) {
            writer.println("An error occurred while reading input: " + e.getMessage());
        }
        abortProcess = false;
        gp.gameState = GamePanel.GameState.PAUSED;
        writer.println("Exiting console input mode. Returning to PAUSED state.");
    }

    private void processCommand(String command) {
        commandHistory.add(command);
        String[] parts = command.split("\\s+");
        try {
            switch (parts[0]) {
                case "help" -> printHelp();
                case "reset" -> resetGame();
                case "exit" -> abortProcess = true;
                case "exit_game" -> System.exit(0);
                case "history" -> printCommandHistory();
                case "save" -> {
                    if(parts.length == 2)
                        saveFile(parts[1]);
                    else
                        throw new IllegalArgumentException("Invalid format! save <filename.extension>");
                }
                case "load" -> {
                    if(parts.length == 2)
                        loadFile(parts[1]);
                    else
                        throw new IllegalArgumentException("Invalid format! load <filename.extension>");
                }
                case "set" -> {
                    if (parts.length == 4)
                        if(parts[1].equals("player"))
                            setGameValue(parts[2], parts[3]);
                        else
                            setEntity(parts[1], parts[2], Integer.parseInt(parts[3]));
                    else if (parts.length == 3)
                        setGameValue(parts[1], parts[2]);
                    else
                        throw new IllegalArgumentException("Invalid format for 'set' command.\n| Wrong Format | set entity arg1 value \n->entity: *Enemy, Player, args: speed,health,maxhealth");
                }
                case "get" -> {
                    if(parts.length == 3) {
                        switch (parts[1]) {
                            case "player" -> getGameValue(parts[2]);
                            case "smallenemy","giantenemy","dragonenemy","friendlyenemy" -> getEntity(parts[1], parts[2]);
                            default -> writer.println("Unknown command. Type 'help' for a list of available commands.");
                        }
                    }
                    else if(parts.length == 2)
                        getGameValue(parts[1]);
                    else
                        throw new IllegalArgumentException("Invalid format for 'set' command.\n| Wrong Format | get entity arg1 \n->entity: *Enemy, Player, args: speed,health\n");
                }
                case "script" -> {
                    if(parts.length != 2)
                        throw new IllegalArgumentException("Invalid format for 'get' command.\n| Wrong Format | scripts <filename> -> filename.extension");
                    runScript(parts[1]);
                }
                case "make" -> {
                    if(parts.length != 2)
                        throw new IllegalArgumentException("Invalid format for 'make' command.\n| Wrong Format | make <filename>");
                    createFile(parts[1]);
                }
                default -> writer.println("Unknown command. Type 'help' for a list of available commands.");
            }
        } catch (IllegalArgumentException e) {writer.println("Error: " + e.getMessage());}
    }

    private void saveFile(String filename) {
        try {
            FileManager.saveGameState(gp, "res/save/" + filename);
            writer.println(filename + " saved successfully.");
        } catch (IOException e) {
            writer.println("No file found or unable to save the file: " + filename);
        }
    }

    private void loadFile(String filename) {
        try {
            File file = new File("res/save/" + filename);
            if (!file.exists()) {
                writer.println("No file found with the name: " + filename);
                return;
            }
            FileManager.loadGameState(gp, "res/save/" + filename);
            writer.println(filename + " loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            writer.println("No file found or unable to load the file: " + filename);
        }
    }


    private void createFile(String filename){
        File saveFile = new File("res/scripts/" + filename + ".txt");
        writer.println("| Entering file creation | type 'end' to exit |");
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(saveFile))) {
            String inputLine;
            int lineNumber = 1;
            do {
                writer.printf("\t%d.\t", lineNumber);
                inputLine = reader.readLine().trim();
                if (!inputLine.equalsIgnoreCase("end")) {
                    fileWriter.write(inputLine);
                    fileWriter.newLine();
                    lineNumber++;
                }
            } while (!inputLine.equalsIgnoreCase("end"));
            writer.println("File " + filename + " saved successfully.");
        } catch (IOException e) {
            writer.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    private void setEntity(String name, String attribute, int value) {
        int nameidx = -1;
        if(attribute.equals("speed")) {
            if (value > 8 || value < 0) {
                writer.println("Value not valid! (0:8)");
                return;
            }
            for(int i=0;i<gp.entities.size();i++) {
                if (gp.entities.get(i).getName().toLowerCase().equals(name)) {
                    gp.entities.get(i).setSpeed(value);
                    nameidx = i;
                }
            }
        }
        else if(attribute.equals("health")) {
            if (value > 5000 || value < 0) {
                writer.println("Value not valid! (0:5000)");
                return;
            } else {
                for (int i = 0; i < gp.entities.size(); i++) {
                    if (gp.entities.get(i).getName().toLowerCase().equals(name)) {
                        gp.entities.get(i).setHealth(value);
                        nameidx = i;
                    }
                }
            }
        }
        else {
            throw new IllegalArgumentException("Unknown attribute: " + attribute);
        }
        if(nameidx != -1)
            writer.println(gp.entities.get(nameidx).getName() + " " +attribute + " set to: " + value);
        else
            writer.println("Entity not found");
    }

    private void getEntity(String name, String attribute) {
        for(int i=0;i<gp.entities.size();i++) {
            if (gp.entities.get(i).getName().toLowerCase().equals(name)) {
                if (attribute.equals("speed")) {
                    writer.println(gp.entities.get(i).getName() + " speed: " + gp.entities.get(i).getSpeed());
                    return;
                }
                else if (attribute.equals("health")) {
                    writer.println(gp.entities.get(i).getName() + " health: " + gp.entities.get(i).getHealth());
                    return;
                }
                else {
                    throw new IllegalArgumentException("Unknown attribute: " + attribute);
                }
            }
        }
    }

    private void runScript(String filename) {
        File scriptFile = new File("res/scripts/"+filename+".txt");
        if (!scriptFile.exists() || !scriptFile.isFile()) {
            writer.println("Error: File not found or not a valid file.");
            return;
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(scriptFile))) {
            String line;
            boolean inMakeMode = false;
            BufferedWriter fileWriter = null;

            while ((line = fileReader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("make")) {
                    // Start make mode
                    String[] parts = line.split("\\s+");
                    if(parts.length == 2) {
                        // Create file and enter 'make' mode
                        File saveFile = new File("res/scripts/" + parts[1] + ".txt");
                        fileWriter = new BufferedWriter(new FileWriter(saveFile));
                        inMakeMode = true;
                        writer.println("| Auto-make mode activated for " + parts[1] + " |");
                    } else {
                        writer.println("Invalid make command in script.");
                        return;
                    }
                }
                else if (inMakeMode && line.equalsIgnoreCase("end")) {
                    fileWriter.close();
                    writer.println("| File creation finished |");
                    inMakeMode = false;
                }
                else if (inMakeMode) {
                    fileWriter.write(line);
                    fileWriter.newLine();
                }
                else {
                    // Process other commands as usual
                    if(line.equals("exit_game"))
                        System.exit(0);
                    else if(line.equals("exit"))
                        abortProcess = true;
                    else if (!line.isEmpty())
                        processCommand(line);
                }
            }
            if (fileWriter != null)
                fileWriter.close();
        } catch (IOException e) {
            writer.println("An error occurred while reading the script file: " + e.getMessage());
        }
        writer.println("|Script executed successfully|");
    }

    private void printHelp() {
        writer.println("Available commands:");
        writer.println("  help - Show this help message");
        writer.println("  history - Show command history");
        writer.println("  script <filename> - Run a script");
        writer.println("  make <filename> - Creates a script");
        writer.println("  set <variable> <value> - Set a game variable");
        writer.println("  get <variable> - Get a game variable");
        writer.println("  reset - Reset the game");
        writer.println("  save/load <filename> -Saves/Loads a game");
        writer.println("  exit/exit_game - Exit console input/ Terminates the game");
    }

    private void resetGame() {
        gp.resetGame();
        writer.println("Game has been reset.");
    }

    private void setGameValue(String variable, String value){
        int speedLimit = 15;
        int healthLimit = 1000;
        int val = Integer.parseInt(value);
        if(val > 0 && val <= 5001) {
            switch (variable) {
                case "health" -> {
                    if (gp.player.getMaxHealth() < val) {
                        writer.println("Value exceeded max health | Change maxhealth first");
                        return;
                    }
                    gp.player.setHealth(val);
                    writer.println("Player health set to " + val);
                }
                case "maxhealth" -> {
                    if (val > healthLimit) {
                        writer.println("Value exceeded health limit (" + healthLimit + ")");
                        return;
                    } else if (val < gp.player.getMaxHealth() && val > gp.player.getHealth()) {
                        gp.player.setMaxHealth(val);
                    } else if (val < gp.player.getMaxHealth() && val < gp.player.getHealth()) {
                        gp.player.setMaxHealth(val);
                        gp.player.setHealth(val);
                    } else {
                        gp.player.setMaxHealth(val);
                    }
                    writer.println("Player maxhealth set to " + val);
                }
                case "speed" -> {
                    if (val > speedLimit) {
                        writer.println("Value exceeded speed limit (" + speedLimit + ")");
                        return;
                    }
                    gp.player.setSpeed(val);
                    writer.println("Player speed set to " + val);
                }
                default -> throw new IllegalArgumentException("Unknown variable: " + variable);
            }
        }
        else{
            writer.println("Value out of range");
        }
    }

    private void getGameValue(String variable) {
        switch (variable) {
            case "health" -> writer.println("Player health: " + gp.player.getHealth());
            case "maxhealth" -> writer.println("Player max health: " + gp.player.getMaxHealth());
            case "speed" -> writer.println("Player speed: " + gp.player.getSpeed());
            default -> throw new IllegalArgumentException("Unknown variable: " + variable);
        }
    }

    private void printCommandHistory() {
        writer.println("Command history:");
        for (int i = 0; i < commandHistory.size(); i++)
            writer.printf("%d: %s%n", i + 1, commandHistory.get(i));
    }

}