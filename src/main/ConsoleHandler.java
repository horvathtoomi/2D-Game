package main;

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
                case "set" -> {
                    if (parts.length == 4) {
                        switch (parts[1]) {
                            case "player" -> setGameValue(parts[2], parts[3]);
                            case "dragonenemy","smallenemy","giantenemy","friendlyenemy" -> setEntity(parts[1], parts[2], Integer.parseInt(parts[3]));
                            default -> writer.println("Unknown command. Type 'help' for a list of available commands.");
                        }
                    }
                    else if (parts.length == 3) {
                        setGameValue(parts[1], parts[2]);
                    }
                    else{
                        writer.println("| Wrong Format | set entity arg1 value \n->entity: *Enemy, Player, args: speed,health,maxhealth");
                        throw new IllegalArgumentException("Invalid format for 'set' command.");
                    }
                }
                case "get" -> {
                    if(parts.length == 3) {
                        switch (parts[1]) {
                            case "player" -> getGameValue(parts[2]);
                            case "smallenemy","giantenemy","dragonenemy","friendlyenemy" -> getEntity(parts[1], parts[2]);
                            default -> writer.println("Unknown command. Type 'help' for a list of available commands.");
                        }
                    }
                    else if(parts.length == 2) {
                        getGameValue(parts[1]);
                    }
                    else{
                        writer.println("| Wrong Format | get entity arg1 \n->entity: *Enemy, Player, args: speed,health");
                        throw new IllegalArgumentException("Invalid format for 'set' command.");
                    }
                }
                case "script" -> {
                    if(parts.length != 2) {
                        writer.println("| Wrong Format | scripts <filename> -> filename.extension");
                        throw new IllegalArgumentException("Invalid format for 'get' command.");
                    }
                    runScript(parts[1]);
                }
                default -> writer.println("Unknown command. Type 'help' for a list of available commands.");
            }
        } catch (IllegalArgumentException e) {
            writer.println("Error: " + e.getMessage());
        }
    }

    private void setEntity(String name, String attribute, int value) {
          for(int i=0;i<gp.entities.size();i++) {
              if (gp.entities.get(i).getName().toLowerCase().equals(name)) {
                  if (attribute.equals("speed")) {
                      if (value <= 8 && value >= 0)
                          gp.entities.get(i).setSpeed(value);
                      else
                          writer.println("Value not valid! (0:8)");
                  }
                  else if (attribute.equals("health")) {
                      if (value < 5001 && value >= 0)
                          gp.entities.get(i).setHealth(value);
                      else
                          writer.println("Value not valid! (0:5000)");
                  }
                  else {
                      throw new IllegalArgumentException("Unknown attribute: " + attribute);
                  }
              }
          }
    }

    private void getEntity(String name, String attribute) {
        for(int i=0;i<gp.entities.size();i++) {
            if (gp.entities.get(i).getName().toLowerCase().equals(name)) {
                if (attribute.equals("speed")) {
                    writer.println(gp.entities.get(i).getName() + ": " + gp.entities.get(i).getSpeed());
                }
                else if (attribute.equals("health")) {
                    writer.println(gp.entities.get(i).getName() + ": " + gp.entities.get(i).getHealth());
                }
                else {
                    throw new IllegalArgumentException("Unknown attribute: " + attribute);
                }
            }
        }
    }

    private void runScript(String filename) {
        File scriptFile = new File("res/scripts/"+filename);
        if (!scriptFile.exists() || !scriptFile.isFile()) {
            writer.println("Error: File not found or not a valid file.");
            return;
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(scriptFile))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                line = line.trim();
                if(line.equals("exit_game"))
                    System.exit(0);
                else if(line.equals("exit"))
                    abortProcess = true;
                else if (!line.isEmpty())
                    processCommand(line);
            }
        } catch (IOException e) {
            writer.println("An error occurred while reading the script file: " + e.getMessage());
            return;
        }
        writer.println("|Script executed successfully|");
    }

    private void printHelp() {
        writer.println("Available commands:");
        writer.println("  help - Show this help message");
        writer.println("  script <filename> - Run a script");
        writer.println("  reset - Reset the game");
        writer.println("  set <variable> <value> - Set a game variable");
        writer.println("  get <variable> - Get the value of a game variable");
        writer.println("  history - Show command history");
        writer.println("  exit - Exit console input mode");
        writer.println("  exit_game - Terminates the game");
    }

    private void resetGame() {
        gp.resetGame();
        writer.println("Game has been reset.");
    }

    private void setGameValue(String variable, String value){
        int speedLimit = 15;
        int healthLimit = 1000;
        switch (variable) {
            case "health"-> {
                int health = Integer.parseInt(value);
                if (gp.player.getMaxHealth() < health) {
                    writer.println("Value exceeded max health | Change maxhealth first");
                    return;
                }
                gp.player.setHealth(health);
                writer.println("Player health set to " + health);
            }
            case "maxhealth" -> {
                int health = Integer.parseInt(value);
                if(health > healthLimit){
                    writer.println("Value exceeded health limit (" + healthLimit + ")");
                    return;
                }
                else if(health < gp.player.getMaxHealth() && health > gp.player.getHealth()) {
                    gp.player.setMaxHealth(health);
                }
                else if(health < gp.player.getMaxHealth() && health < gp.player.getHealth()){
                    gp.player.setMaxHealth(health);
                    gp.player.setHealth(health);
                }
                else {
                    gp.player.setMaxHealth(health);
                }
                writer.println("Player maxhealth set to " + health);
            }
            case "speed"-> {
                int speed = Integer.parseInt(value);
                if(speed > speedLimit){
                    writer.println("Value exceeded speed limit (" + speedLimit + ")");
                    return;
                }
                gp.player.setSpeed(speed);
                writer.println("Player speed set to " + speed);
            }
            default -> throw new IllegalArgumentException("Unknown variable: " + variable);
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