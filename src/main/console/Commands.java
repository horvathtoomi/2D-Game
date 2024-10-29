package main.console;

import entity.Entity;
import entity.enemy.*;
import object.*;
import main.GamePanel;
import serializable.FileManager;

import java.io.*;
import java.util.ArrayList;


public class Commands {
    private final GamePanel gp;
    private final PrintWriter writer;

    public Commands(GamePanel gp) {
        this.gp = gp;
        this.writer = new PrintWriter(System.out, true);
    }

    public void removeEntities(String entityType, boolean removeAll) {
        int count = 0;
        for (Entity entity : new ArrayList<>(gp.entities)) {
            if(removeAll) {
                gp.entities.remove(entity);
                count++;
            }
            else {
                if (entity.getClass().getSimpleName().equalsIgnoreCase(entityType)) {
                    gp.entities.remove(entity);
                    count++;
                }
            }
        }
        if(count != 0)
            writer.println("Removed " + count + " entities of type " + entityType);
        else
            writer.println("No such entity as: " + entityType);
    }

    public void add(String obj, int x, int y) {
        if(!((x<50 && x>1) && (y<50 && y>1))){
            writer.println("Coordinates must be [2:49]");
            return;
        }
        obj = obj.toLowerCase();
        switch(obj){
            case "giantenemy" -> gp.addEnemy(new GiantEnemy(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "smallenemy" -> gp.addEnemy(new SmallEnemy(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "friendlyenemy" -> gp.addEnemy(new FriendlyEnemy(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "dragonenemy" -> gp.addEnemy(new DragonEnemy(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "key" -> gp.addObject(new OBJ_Key(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "door" -> gp.addObject(new OBJ_Door(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "boots" -> gp.addObject(new OBJ_Boots(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "chest" -> gp.addObject(new OBJ_Chest(gp,x * gp.getTileSize(),y * gp.getTileSize()));
        }
    }

    public void saveFile(String filename) {
        try {
            FileManager.saveGameState(gp, "res/save/" + filename);
            writer.println(filename + " saved successfully.");
        } catch (IOException e) {
            writer.println("No file found or unable to save the file: " + filename);
        }
    }

    public void loadFile(String filename) {
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

    public void createFile(String filename, BufferedReader reader) {
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

    public void runScript(String filename) {
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
                    String[] parts = line.split("\\s+");
                    if(parts.length == 2) {
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
                else if (!line.isEmpty()) {
                    gp.console.executeCommand(line);
                }
            }
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (IOException e) {
            writer.println("An error occurred while reading the script file: " + e.getMessage());
        }
        writer.println("|Script executed successfully|");
    }

    public void printHelp(String command) {
        switch(command){
            case "script" -> writer.println("Script use: script <filename> without extension");
            case "make" -> writer.println("Make use: make <filename> without extension");
            case "set" -> writer.println("""
                    Set use: set <entity_name> <value>
                    Where entity_name: <blank>,<entity>,<GiantEnemy>,<SmallEnemy>,<DragonEnemy>,<FriendlyEnemy>""");
            case "get" -> writer.println("""
                    Get use: get <entity_name>
                    Where entity_name: <blank>,<GiantEnemy>,<SmallEnemy>,<DragonEnemy>,<FriendlyEnemy>""");
            case "add" -> writer.println("""
                    Add use: add <entity/object> <x> <y>
                    Where entity/object: <GiantEnemy>,<SmallEnemy>,<DragonEnemy>,<FriendlyEnemy>,<key>,<boots>,<door>,<chest>""");
            case "remove" -> writer.println("""
                    Remove use: remove <entity_name>
                    Where entity_name: <all> <GiantEnemy> <SmallEnemy> <DragonEnemy> <FriendlyEnemy>""");
            case "reset" -> writer.println("Reset use: reset -resets the game");
            case "save" -> writer.println("Save use: save <filename> without extension");
            case "load" -> writer.println("Load use: load <filename> without extension");
            case "exit" -> writer.println("Exit use: exit - exits console mode");
            case "exit_game" -> writer.println("Exits game");
            default -> {
                writer.println("script/make/set/get/add\nreset/save/load/exit/exit_game/remove");
                writer.println("Use 'help <command>' from above commands");
            }
        }
    }

    public void setAll(String attribute, int value) {
        if(attribute.equals("speed")) {
            if (value > 8 || value < 0) {
                writer.println("Value not valid! (0:8)");
                return;
            }
            for(Entity entity : gp.entities)
                entity.setSpeed(value);
        }
        else if(attribute.equals("health")) {
            if (value > 5000 || value < 0) {
                writer.println("Value not valid! (0:5000)");
                return;
            }
            else {
                for(Entity entity : gp.entities)
                    entity.setHealth(value);
            }
        }
        else {
            throw new IllegalArgumentException("Unknown attribute: " + attribute);
        }
        writer.println("Every entity's " + attribute + " set to: " + value);
    }

    public void setEntity(String name, String attribute, int value) {
        int entityIndex = -1;
        if(attribute.equals("speed")) {
            if (value > 8 || value < 0) {
                writer.println("Value not valid! (0:8)");
                return;
            }
            for (int i = 0; i < gp.entities.size(); i++) {
                if (gp.entities.get(i).getName().equalsIgnoreCase(name)) {
                    gp.entities.get(i).setSpeed(value);
                    entityIndex = i;
                    break;
                }
            }
        }
        else if(attribute.equals("health")) {
            if (value > 5000 || value < 0) {
                writer.println("Value not valid! (0:5000)");
                return;
            }
            for (int i = 0; i < gp.entities.size(); i++) {
                if (gp.entities.get(i).getName().equalsIgnoreCase(name)) {
                    gp.entities.get(i).setHealth(value);
                    entityIndex = i;
                    break;
                }
            }
        }
        else {
            throw new IllegalArgumentException("Unknown attribute: " + attribute);
        }

        if(entityIndex != -1)
            writer.println(gp.entities.get(entityIndex).getName() + " " + attribute + " set to: " + value);
        else
            writer.println("Entity not found");
    }

    public void getEntity(String name, String attribute) {
        for (Entity entity : gp.entities) {
            if (entity.getName().toLowerCase().equals(name)) {
                if (attribute.equals("speed")) {
                    writer.println(entity.getName() + " speed: " + entity.getSpeed());
                    return;
                } else if (attribute.equals("health")) {
                    writer.println(entity.getName() + " health: " + entity.getHealth());
                    return;
                } else {
                    throw new IllegalArgumentException("Unknown attribute: " + attribute);
                }
            }
        }
        writer.println("Entity not found");
    }

    public void setGameValue(String variable, String value) {
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
                    }
                    if (val < gp.player.getMaxHealth()) {
                        if (val > gp.player.getHealth()) {
                            gp.player.setMaxHealth(val);
                        } else {
                            gp.player.setMaxHealth(val);
                            gp.player.setHealth(val);
                        }
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
        } else {
            writer.println("Value out of range");
        }
    }

    public void getGameValue(String variable) {
        switch (variable) {
            case "health" -> writer.println("Player health: " + gp.player.getHealth());
            case "maxhealth" -> writer.println("Player max health: " + gp.player.getMaxHealth());
            case "speed" -> writer.println("Player speed: " + gp.player.getSpeed());
            default -> throw new IllegalArgumentException("Unknown variable: " + variable);
        }
    }

}