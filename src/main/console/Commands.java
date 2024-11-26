package main.console;

import entity.Entity;
import entity.enemy.DragonEnemy;
import entity.enemy.FriendlyEnemy;
import entity.enemy.GiantEnemy;
import entity.enemy.SmallEnemy;
import main.Engine;
import main.logger.GameLogger;
import object.*;
import serializable.FileManager;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A játék konzol parancsait kezelő osztály.
 * Végrehajtja a különböző játékbeli műveleteket a konzol parancsok alapján.
 */
public class Commands {
    private final Engine eng;
    private static int numMakeEnd = 0;
    private final ConsoleHandler consoleHandler;
    private static final String RES_SAVE_PATH = "res/save/";
    private static final String RES_SCRIPTS_PATH = "res/scripts/";
    private static final String UNKNOWN_ATTRIBUTE = "Unknown attribute: ";
    private static final String SPEED = "speed";
    private static final String HEALTH = "health";
    private static final String LOG_CONTEXT = "[COMMANDS]";

    public Commands(Engine eng, ConsoleHandler consoleHandler) {
        this.eng = eng;
        this.consoleHandler = consoleHandler;
    }

    /**
     * Eltávolítja a megadott típusú entitásokat a játékból.
     * @param entityType az entitás típusa
     * @param removeAll ha igaz, minden entitást eltávolít
     */
    public void removeEntities(String entityType, boolean removeAll) {
        int count = 0;
        for (Entity entity : eng.getEntity()) {
            if(removeAll) {
                eng.removeEnemy(entity);
                count++;
            }
            else {
                if (entity.getClass().getSimpleName().equalsIgnoreCase(entityType)) {
                    eng.removeEnemy(entity);
                    count++;
                }
            }
        }
        if(count != 0)
            consoleHandler.printToConsole("Removed " + count + " entities of type " + entityType);
        else
            consoleHandler.printToConsole("No such entity as: " + entityType);
    }

    /**
     * Új objektumot vagy entitást ad a játékhoz.
     * @param obj az objektum vagy entitás típusa
     * @param x X koordináta
     * @param y Y koordináta
     */
    public void add(String obj, int x, int y) {
        if(!((x< eng.getMaxWorldCol() && x>=1) && (y< eng.getMaxWorldRow() && y>=1))){
            consoleHandler.printToConsole("Coordinates must be [1:" + (eng.getMaxWorldCol() -1) + "]");
            return;
        }
        obj = obj.toLowerCase();
        switch(obj){
            case "giantenemy" -> eng.addEntity(new GiantEnemy(eng,x * eng.getTileSize(),y * eng.getTileSize()));
            case "smallenemy" -> eng.addEntity(new SmallEnemy(eng,x * eng.getTileSize(),y * eng.getTileSize()));
            case "friendlyenemy" -> eng.addEntity(new FriendlyEnemy(eng,x * eng.getTileSize(),y * eng.getTileSize()));
            case "dragonenemy" -> eng.addEntity(new DragonEnemy(eng,x * eng.getTileSize(),y * eng.getTileSize()));
            case "key" -> eng.addObject(new OBJ_Key(eng,x * eng.getTileSize(),y * eng.getTileSize()));
            case "door" -> eng.addObject(new OBJ_Door(eng,x * eng.getTileSize(),y * eng.getTileSize()));
            case "boots" -> eng.addObject(new OBJ_Boots(eng,x * eng.getTileSize(),y * eng.getTileSize()));
            case "chest" -> eng.addObject(new OBJ_Chest(eng,x * eng.getTileSize(),y * eng.getTileSize()));
            case "sword" -> eng.addObject(new OBJ_Sword(eng,x * eng.getTileSize(),y * eng.getTileSize(), 50));
            default -> {
                consoleHandler.printToConsole("Unknown entity or object: " + obj);
                return;
            }
        }
        consoleHandler.printToConsole(obj + " added to the game");
    }

    public void teleport(int x, int y){
        int[][] maphelp = eng.tileman.getMapTileNum();
        if(x >= eng.getMaxWorldCol() || x < 1 || y >= eng.getMaxWorldRow() || y < 1){
            GameLogger.warn(LOG_CONTEXT, "Coordinates must be X->[1:" + (eng.getMaxWorldCol() -1) + "] and Y->[1:" + (eng.getMaxWorldRow() -1) + "]");
            return;
        }
        else if(eng.tileman.getTile(maphelp[x][y]).collision){
            GameLogger.warn(LOG_CONTEXT, "Can not teleport on a solid tile!");
            return;
        }
        eng.player.setWorldX(x * eng.getTileSize());
        eng.player.setWorldY(y * eng.getTileSize());
        consoleHandler.printToConsole("Player teleported to <" + x + "> <" + y + ">");
    }

    public void saveFile(String filename) {
        try {
            FileManager.saveGameState(eng, RES_SAVE_PATH + filename + ".sav");
            consoleHandler.printToConsole(filename + " saved successfully");
        } catch (IOException e) {
            consoleHandler.printToConsole("UNABLE TO SAVE: " + filename + " - " + e.getMessage());
        }
    }

    public void loadFile(String filename) {
        try {
            String fileName = RES_SAVE_PATH + filename + ".sav";
            File file = new File(fileName);
            if (!file.exists()) {
                consoleHandler.printToConsole(filename + " not found");
                return;
            }
            FileManager.loadGameState(eng, fileName);
            consoleHandler.printToConsole(filename + " loaded successfully");
        } catch (IOException | ClassNotFoundException e) {
            consoleHandler.printToConsole("UNABLE TO LOAD: " + filename + " - " + e.getMessage());
        }
    }

    public void createFile(String filename, ConsoleGUI gui) {
        new Thread(() -> {
            File saveFile = new File(RES_SCRIPTS_PATH + filename + ".txt");
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(saveFile))) {
                consoleHandler.printToConsole("Enter commands for the script (type 'end' to finish):");
                while (true) {
                    String input = gui.getInput();
                    if(input.startsWith("make")){
                        numMakeEnd++;
                    }
                    if ("end".equalsIgnoreCase(input.trim())) {
                        if(numMakeEnd <= 0) {
                            consoleHandler.printToConsole(filename + " created and saved successfully.");
                            break;
                        } else{
                            numMakeEnd--;
                        }
                    }
                    fileWriter.write(input);
                    fileWriter.newLine();
                }
            } catch (IOException e) {
                consoleHandler.printToConsole("An error occurred while writing to the file: " + e.getMessage());
            }
        }).start();
    }

    public void runScript(String filename) {
        runScript(filename, new HashSet<>());
    }

    /**
     * Végrehajtja a script fájlban található parancsokat.
     * @param filename a script fájl neve
     */
    private void runScript(String filename, Set<String> visitedScripts) {
        String normalizedFilename = RES_SCRIPTS_PATH + filename + ".txt";
        if (visitedScripts.contains(normalizedFilename)) {
            consoleHandler.printToConsole(
            "\n----------------------------------------------------------\n" +
            "ERROR: Circular script reference detected: " + filename +
            "\n----------------------------------------------------------");
            return;
        }
        visitedScripts.add(normalizedFilename);
        File scriptFile = new File(normalizedFilename);
        if (!scriptFile.exists() || !scriptFile.isFile()) {
            consoleHandler.printToConsole("ERROR: " + filename + " not found");
            return;
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(scriptFile))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("script")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length == 2) {
                        runScript(parts[1], visitedScripts);
                    } else {
                        consoleHandler.printToConsole("Invalid script command: " + line);
                    }
                } else if(line.startsWith("make")){
                    String[] parts = line.split("\\s+");
                    String newFileName = parts[1];
                    File saveFile = new File(RES_SCRIPTS_PATH + newFileName + ".txt");
                    try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(saveFile))) {
                        consoleHandler.printToConsole("|Entering make mode: " + newFileName + "|");
                        while (true) {
                            String input = fileReader.readLine();
                            if (input.equalsIgnoreCase("end") || input.startsWith("end")) {
                                fileWriter.write(input);
                                fileWriter.newLine();
                                consoleHandler.printToConsole("|" + newFileName + " created and saved|");
                                break;
                            }
                            fileWriter.write(input);
                            fileWriter.newLine();
                        }
                    } catch (IOException e) {
                        consoleHandler.printToConsole("An error occurred while writing to the file: " + e.getMessage());
                    }
                } else {
                    consoleHandler.executeCommand(line);
                }
            }
        } catch (IOException e) {
            consoleHandler.printToConsole("An error occurred while reading the script file: " + e.getMessage());
        }
        visitedScripts.remove(normalizedFilename);
        consoleHandler.printToConsole("Finished executing script: " + filename);
    }

    public void printHelp(String command) {
        switch(command){
            case "script" -> consoleHandler.printToConsole("Script use: script <filename> without extension");
            case "make" -> consoleHandler.printToConsole("Make use: make <filename> without extension");
            case "set" -> consoleHandler.printToConsole("""
                    Set use: set <entity_name> <value>
                    Where entity_name: <blank>,<entity>,<GiantEnemy>,<SmallEnemy>,<DragonEnemy>,<FriendlyEnemy>""");
            case "get" -> consoleHandler.printToConsole("""
                    Get use: get <entity_name>
                    Where entity_name: <blank>,<GiantEnemy>,<SmallEnemy>,<DragonEnemy>,<FriendlyEnemy>""");
            case "add" -> consoleHandler.printToConsole("""
                    Add use: add <entity/object> <x> <y>
                    Where entity/object: <GiantEnemy>,<SmallEnemy>,<DragonEnemy>,<FriendlyEnemy>,<key>,<boots>,<door>,<chest>,<sword>""");
            case "remove" -> consoleHandler.printToConsole("""
                    Remove use: remove <entity_name>
                    Where entity_name: <all> <GiantEnemy> <SmallEnemy> <DragonEnemy> <FriendlyEnemy>""");
            case "teleport" -> consoleHandler.printToConsole("Usage: teleport <X> <Y>, teleports player to: <X> <Y>");
            case "reset" -> consoleHandler.printToConsole("Reset use: reset -resets the game");
            case "save" -> consoleHandler.printToConsole("Save use: save <filename> without extension");
            case "load" -> consoleHandler.printToConsole("Load use: load <filename> without extension");
            case "exit" -> consoleHandler.printToConsole("Exit use: exit - exits console mode");
            case "exit_game" -> consoleHandler.printToConsole("Exits game");
            default -> consoleHandler.printToConsole("""
                    script/make/set/get/add/reset
                    save/load/exit/exit_game/remove
                    | Use 'help <command>' from above commands |
                    """);
        }
    }

    public void setAll(String attribute, int value) {
        if(attribute.equals(SPEED)) {
            if (value > 8 || value < 0) {
                consoleHandler.printToConsole("Value not valid! (0:8)");
                return;
            }
            for(Entity entity : eng.getEntity())
                entity.setSpeed(value);
        }
        else if(attribute.equals(HEALTH)) {
            if (value > 5000 || value < 0) {
                consoleHandler.printToConsole("Value not valid! (0:5000)");
                return;
            }
            else {
                for(Entity entity : eng.getEntity())
                    entity.setHealth(value);
            }
        }
        else {
            consoleHandler.printToConsole(UNKNOWN_ATTRIBUTE + attribute);
        }
        consoleHandler.printToConsole("Every entity's " + attribute + " set to: " + value);
    }

    public void setEntity(String name, String attribute, int value) {
        int entityIndex = -1;
        if(attribute.equals(SPEED)) {
            if (value > 8 || value < 0) {
                consoleHandler.printToConsole("Value not valid! (0:8)");
                return;
            }
            for (int i = 0; i < eng.getEntity().size(); i++) {
                if (eng.getEntity().get(i).getName().equalsIgnoreCase(name)) {
                    eng.getEntity().get(i).setSpeed(value);
                    entityIndex = i;
                    break;
                }
            }
        }
        else if(attribute.equals(HEALTH)) {
            if (value > 5000 || value < 0) {
                consoleHandler.printToConsole("Value not valid! (0:5000)");
                return;
            }
            for (int i = 0; i < eng.getEntity().size(); i++) {
                if (eng.getEntity().get(i).getName().equalsIgnoreCase(name)) {
                    eng.getEntity().get(i).setHealth(value);
                    entityIndex = i;
                    break;
                }
            }
        }
        else {
            consoleHandler.printToConsole(UNKNOWN_ATTRIBUTE + attribute);
        }

        if(entityIndex != -1)
            consoleHandler.printToConsole(eng.getEntity().get(entityIndex).getName() + " " + attribute + " set to: " + value);
        else
            consoleHandler.printToConsole("Entity not found");
    }

    public void getEntity(String name, String attribute) {
        for (Entity entity : eng.getEntity()) {
            if (entity.getName().toLowerCase().equals(name)) {
                if (attribute.equals(SPEED)) {
                    consoleHandler.printToConsole(entity.getName() + " speed: " + entity.getSpeed());
                    return;
                } else if (attribute.equals(HEALTH)) {
                    consoleHandler.printToConsole(entity.getName() + " health: " + entity.getHealth());
                    return;
                } else {
                    consoleHandler.printToConsole(UNKNOWN_ATTRIBUTE + attribute);
                }
            }
        }
        consoleHandler.printToConsole("Entity not found");
    }

    public void setGameValue(String variable, String value) {
        int speedLimit = 15;
        int healthLimit = 1000;
        int val = Integer.parseInt(value);
        if(val > 0 && val <= 5001) {
            switch (variable) {
                case HEALTH -> {
                    if (eng.player.getMaxHealth() < val) {
                        consoleHandler.printToConsole("Value exceeded max health | Change maxhealth first");
                        return;
                    }
                    eng.player.setHealth(val);
                    consoleHandler.printToConsole("Player health set to " + val);
                }
                case "maxhealth" -> {
                    if (val > healthLimit) {
                        consoleHandler.printToConsole("Value exceeded health limit (" + healthLimit + ")");
                        return;
                    }
                    if (val < eng.player.getMaxHealth()) {
                        if (val > eng.player.getHealth()) {
                            eng.player.setMaxHealth(val);
                        } else {
                            eng.player.setMaxHealth(val);
                            eng.player.setHealth(val);
                        }
                    } else {
                        eng.player.setMaxHealth(val);
                    }
                    consoleHandler.printToConsole("Player maxhealth set to " + val);
                }
                case SPEED -> {
                    if (val > speedLimit) {
                        consoleHandler.printToConsole("Value exceeded speed limit (" + speedLimit + ")");
                        return;
                    }
                    eng.player.setSpeed(val);
                    consoleHandler.printToConsole("Player speed set to " + val);
                }
                default -> consoleHandler.printToConsole(UNKNOWN_ATTRIBUTE + variable);
            }
        } else {
            consoleHandler.printToConsole("Value out of range");
        }
    }

    public void getGameValue(String variable) {
        switch (variable) {
            case HEALTH -> consoleHandler.printToConsole("Player health: " + eng.player.getHealth());
            case "maxhealth" -> consoleHandler.printToConsole("Player max health: " + eng.player.getMaxHealth());
            case SPEED -> consoleHandler.printToConsole("Player speed: " + eng.player.getSpeed());
            default -> consoleHandler.printToConsole("VARIABLE NOT FOUND");
        }
    }
}