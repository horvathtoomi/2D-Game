package main.console;

import entity.Entity;
import entity.enemy.*;
import main.logger.GameLogger;
import object.*;
import main.GamePanel;
import serializable.FileManager;

import java.io.*;
import java.util.ArrayList;


public class Commands {
    private final GamePanel gp;
    private final PrintWriter writer;
    private static final String LOG_CONTEXT = "[COMMANDS]";
    private static final String RES_SAVE_PATH = "res/save/";
    private static final String RES_SCRIPTS_PATH = "res/scripts/";
    private static final String UNKNOWN_ATTRIBUTE = "Unknown attribute: ";
    private static final String SPEED = "speed";
    private static final String HEALTH = "health";

    public Commands(GamePanel gp) {
        this.gp = gp;
        this.writer = new PrintWriter(System.out, true);
    }

    public void removeEntities(String entityType, boolean removeAll) {
        int count = 0;
        for (Entity entity : new ArrayList<>(gp.getEntity())) {
            if(removeAll) {
                gp.removeEnemy(entity);
                count++;
            }
            else {
                if (entity.getClass().getSimpleName().equalsIgnoreCase(entityType)) {
                    gp.removeEnemy(entity);
                    count++;
                }
            }
        }
        if(count != 0)
            GameLogger.info(LOG_CONTEXT, "Removed " + count + " entities of type " + entityType);
        else
            GameLogger.warn(LOG_CONTEXT,"No such entity as: " + entityType);
    }

    public void add(String obj, int x, int y) {
        if(!((x<50 && x>1) && (y<50 && y>1))){
            GameLogger.warn(LOG_CONTEXT,"Coordinates must be [2:49]");
            return;
        }
        obj = obj.toLowerCase();
        switch(obj){
            case "giantenemy" -> gp.addEntity(new GiantEnemy(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "smallenemy" -> gp.addEntity(new SmallEnemy(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "friendlyenemy" -> gp.addEntity(new FriendlyEnemy(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "dragonenemy" -> gp.addEntity(new DragonEnemy(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "key" -> gp.addObject(new OBJ_Key(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "door" -> gp.addObject(new OBJ_Door(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "boots" -> gp.addObject(new OBJ_Boots(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            case "chest" -> gp.addObject(new OBJ_Chest(gp,x * gp.getTileSize(),y * gp.getTileSize()));
            default -> GameLogger.warn(LOG_CONTEXT, "Unknown command or object: " + obj);
        }
    }

    public void saveFile(String filename) {
        try {
            FileManager.saveGameState(gp, RES_SAVE_PATH + filename);
            GameLogger.info(LOG_CONTEXT, filename + " saved successfully");
        } catch (IOException e) {
            GameLogger.error(LOG_CONTEXT, "UNABLE TO SAVE: " + filename, e);
        }
    }

    public void loadFile(String filename) {
        try {
            File file = new File(RES_SAVE_PATH + filename);
            if (!file.exists()) {
                GameLogger.warn(LOG_CONTEXT, filename + " not found");
                return;
            }
            FileManager.loadGameState(gp, RES_SAVE_PATH + filename);
            writer.println(filename + " loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            GameLogger.error(LOG_CONTEXT, "UNABLE TO LOAD: " + filename, e);
        }
    }

    public void createFile(String filename, BufferedReader reader) {
        File saveFile = new File(RES_SCRIPTS_PATH + filename + ".txt");
        GameLogger.info(LOG_CONTEXT, "Entering script creation |type 'end' to exit|");
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
            GameLogger.info(LOG_CONTEXT, filename + " created and saved successfully.");
        } catch (IOException e) {
            GameLogger.warn(LOG_CONTEXT, "An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public void runScript(String filename) {
        File scriptFile = new File(RES_SCRIPTS_PATH + filename + ".txt");
        if (!scriptFile.exists() || !scriptFile.isFile()) {
            GameLogger.warn(LOG_CONTEXT, "ERROR: " +filename + " not found");
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
                        File saveFile = new File(RES_SCRIPTS_PATH + parts[1] + ".txt");
                        fileWriter = new BufferedWriter(new FileWriter(saveFile));
                        inMakeMode = true;
                        GameLogger.info(LOG_CONTEXT, "|Auto-make mode activated for" + parts[1] + "|");
                    } else {
                        GameLogger.warn(LOG_CONTEXT, "INVALID COMMAND");
                        return;
                    }
                }
                else if (inMakeMode && line.equalsIgnoreCase("end")) {
                    fileWriter.close();
                    GameLogger.info(LOG_CONTEXT, "|FILE CREATION FINISHED");
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
            GameLogger.error(LOG_CONTEXT, "ERROR OCCURED: " + e.getMessage(), e);
        }
        GameLogger.info(LOG_CONTEXT, filename + " executed successfully.");
    }

    public void printHelp(String command) {
        switch(command){
            case "script" -> GameLogger.info(LOG_CONTEXT,"Script use: script <filename> without extension");
            case "make" -> GameLogger.info(LOG_CONTEXT,"Make use: make <filename> without extension");
            case "set" -> GameLogger.info(LOG_CONTEXT,"""
                    Set use: set <entity_name> <value>
                    Where entity_name: <blank>,<entity>,<GiantEnemy>,<SmallEnemy>,<DragonEnemy>,<FriendlyEnemy>""");
            case "get" -> GameLogger.info(LOG_CONTEXT,"""
                    Get use: get <entity_name>
                    Where entity_name: <blank>,<GiantEnemy>,<SmallEnemy>,<DragonEnemy>,<FriendlyEnemy>""");
            case "add" -> GameLogger.info(LOG_CONTEXT,"""
                    Add use: add <entity/object> <x> <y>
                    Where entity/object: <GiantEnemy>,<SmallEnemy>,<DragonEnemy>,<FriendlyEnemy>,<key>,<boots>,<door>,<chest>""");
            case "remove" -> GameLogger.info(LOG_CONTEXT,"""
                    Remove use: remove <entity_name>
                    Where entity_name: <all> <GiantEnemy> <SmallEnemy> <DragonEnemy> <FriendlyEnemy>""");
            case "reset" -> GameLogger.info(LOG_CONTEXT,"Reset use: reset -resets the game");
            case "save" -> GameLogger.info(LOG_CONTEXT,"Save use: save <filename> without extension");
            case "load" -> GameLogger.info(LOG_CONTEXT,"Load use: load <filename> without extension");
            case "exit" -> GameLogger.info(LOG_CONTEXT,"Exit use: exit - exits console mode");
            case "exit_game" -> GameLogger.info(LOG_CONTEXT,"Exits game");
            default ->
                GameLogger.info(LOG_CONTEXT, """
                        script/make/set/get/add/reset
                        save/load/exit/exit_game/remove
                        | Use 'help <command>' from above commands |
                        """);
        }
    }

    public void setAll(String attribute, int value) {
        if(attribute.equals(SPEED)) {
            if (value > 8 || value < 0) {
                GameLogger.warn(LOG_CONTEXT,"Value not valid! (0:8)");
                return;
            }
            for(Entity entity : gp.getEntity())
                entity.setSpeed(value);
        }
        else if(attribute.equals(HEALTH)) {
            if (value > 5000 || value < 0) {
                GameLogger.warn(LOG_CONTEXT,"Value not valid! (0:5000)");
                return;
            }
            else {
                for(Entity entity : gp.getEntity())
                    entity.setHealth(value);
            }
        }
        else {
            GameLogger.warn(LOG_CONTEXT,UNKNOWN_ATTRIBUTE + attribute);
        }
        GameLogger.info(LOG_CONTEXT, "Every entity's " + attribute + " set to: " + value);
    }

    public void setEntity(String name, String attribute, int value) {
        int entityIndex = -1;
        if(attribute.equals(SPEED)) {
            if (value > 8 || value < 0) {
                GameLogger.warn(LOG_CONTEXT,"Value not valid! (0:8)");
                return;
            }
            for (int i = 0; i < gp.getEntity().size(); i++) {
                if (gp.getEntity().get(i).getName().equalsIgnoreCase(name)) {
                    gp.getEntity().get(i).setSpeed(value);
                    entityIndex = i;
                    break;
                }
            }
        }
        else if(attribute.equals(HEALTH)) {
            if (value > 5000 || value < 0) {
                GameLogger.warn(LOG_CONTEXT, "Value not valid! (0:5000)");
                return;
            }
            for (int i = 0; i < gp.getEntity().size(); i++) {
                if (gp.getEntity().get(i).getName().equalsIgnoreCase(name)) {
                    gp.getEntity().get(i).setHealth(value);
                    entityIndex = i;
                    break;
                }
            }
        }
        else {
            GameLogger.warn(LOG_CONTEXT, UNKNOWN_ATTRIBUTE + attribute);
        }

        if(entityIndex != -1)
            writer.println(gp.getEntity().get(entityIndex).getName() + " " + attribute + " set to: " + value);
        else
            GameLogger.warn(LOG_CONTEXT, "Entity not found");
    }

    public void getEntity(String name, String attribute) {
        for (Entity entity : gp.getEntity()) {
            if (entity.getName().toLowerCase().equals(name)) {
                if (attribute.equals(SPEED)) {
                    writer.println(entity.getName() + " speed: " + entity.getSpeed());
                    return;
                } else if (attribute.equals(HEALTH)) {
                    writer.println(entity.getName() + " health: " + entity.getHealth());
                    return;
                } else {
                    GameLogger.warn(LOG_CONTEXT, UNKNOWN_ATTRIBUTE + attribute);
                }
            }
        }
        GameLogger.warn(LOG_CONTEXT, "Entity not found");
    }

    public void setGameValue(String variable, String value) {
        int speedLimit = 15;
        int healthLimit = 1000;
        int val = Integer.parseInt(value);
        if(val > 0 && val <= 5001) {
            switch (variable) {
                case HEALTH -> {
                    if (gp.player.getMaxHealth() < val) {
                        GameLogger.warn(LOG_CONTEXT, "Value exceeded max health | Change maxhealth first");
                        return;
                    }
                    gp.player.setHealth(val);
                    GameLogger.info(LOG_CONTEXT,"Player health set to " + val);
                }
                case "maxhealth" -> {
                    if (val > healthLimit) {
                        GameLogger.info(LOG_CONTEXT, "Value exceeded health limit (" + healthLimit + ")");
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
                    GameLogger.info(LOG_CONTEXT,"Player maxhealth set to " + val);
                }
                case SPEED -> {
                    if (val > speedLimit) {
                        GameLogger.warn(LOG_CONTEXT,"Value exceeded speed limit (" + speedLimit + ")");
                        return;
                    }
                    gp.player.setSpeed(val);
                    GameLogger.info(LOG_CONTEXT,"Player speed set to " + val);
                }
                default -> GameLogger.warn(LOG_CONTEXT,UNKNOWN_ATTRIBUTE + variable);
            }
        } else {
            GameLogger.warn(LOG_CONTEXT,"Value out of range");
        }
    }

    public void getGameValue(String variable) {
        switch (variable) {
            case HEALTH -> GameLogger.info(LOG_CONTEXT,"Player health: " + gp.player.getHealth());
            case "maxhealth" -> GameLogger.info(LOG_CONTEXT,"Player max health: " + gp.player.getMaxHealth());
            case SPEED -> GameLogger.info(LOG_CONTEXT,"Player speed: " + gp.player.getSpeed());
            default -> GameLogger.warn(LOG_CONTEXT, "VARIABLE NOT FOUND");
        }
    }

}