package main;

import entity.Entity;
import entity.enemy.*;
import entity.npc.NPC_Wayfarer;
import main.logger.GameLogger;
import object.*;
import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class AssetSetter {
    Engine gp;
    public List<SuperObject> list;
    private final String[] possibleChestItems = {"key", "boots", "sword"};
    private final Random rand;
    private int mapNum = 1;
    private static final String LOG_CONTEXT = "[ASSET SETTER]";

    public AssetSetter(Engine gp) {
        this.gp = gp;
        list = new CopyOnWriteArrayList<>();
        rand = new Random();
    }

    public void loadLevelAssets(boolean restart){
        try {
            setEnemies(restart);
            setObject(restart);
        } catch (IOException e) {
            GameLogger.error(LOG_CONTEXT,"Assets could not be set.", e);
        }
    }

    public void setObject(boolean restart) throws IOException {
        if(restart) {
            mapNum = 1;
        }
        list.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("res/assets/map" + mapNum + "_assets.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length != 3) continue;
                String name = parts[0];
                int x = Integer.parseInt(parts[1]) * gp.getTileSize();
                int y = Integer.parseInt(parts[2]) * gp.getTileSize();
                createObject(name, x, y);
            }
            mapNum++;
        }
    }

    public void setEnemies(boolean restart) throws IOException {
        if(restart) {
            mapNum = 1;
        }
        list.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("res/enemies/map" + mapNum + "_enemies.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length != 3) continue;
                String name = parts[0];
                int x = Integer.parseInt(parts[1]) * gp.getTileSize();
                int y = Integer.parseInt(parts[2]) * gp.getTileSize();
                createEnemy(name, x, y);
            }
        }
    }

    public void createObject(String name, int x, int y) {
        SuperObject obj = switch (name) {
            case "key" -> new OBJ_Key(gp, x, y);
            case "chest" -> new OBJ_Chest(gp, x, y);
            case "door" -> new OBJ_Door(gp, x, y);
            case "boots" -> new OBJ_Boots(gp, x, y);
            case "sword" -> new OBJ_Sword(gp, x, y, 50);
            default -> null;
        };
        if (obj != null) {
            list.add(obj);
        }
        else{
            GameLogger.error(LOG_CONTEXT,"Unexpected type listed in one of the assets file", new IllegalArgumentException("Illegal argument"));
        }
    }

    private void createEnemy(String name, int x, int y) {
        Entity ent = switch (name.toLowerCase()){
            case "dragonenemy" -> new DragonEnemy(gp, x, y);
            case "friendlyenemy" -> new FriendlyEnemy(gp, x, y);
            case "giantenemy" -> new GiantEnemy(gp, x, y);
            case "smallenemy" -> new SmallEnemy(gp, x, y);
            case "npc_wayfarer" -> new NPC_Wayfarer(gp, x, y);
            default -> null;
        };
        if(ent != null)
            gp.addEntity(ent);
        else
            GameLogger.error(LOG_CONTEXT, "Unexpected type listed in one of the enemies file", new IllegalArgumentException("ILLEGAL ARGUMENT"));
    }

    public void spawnItemFromChest(int x, int y) {
        String randomItem = possibleChestItems[rand.nextInt(possibleChestItems.length)];
        SuperObject newItem = null;
        switch (randomItem) {
            case "key" -> newItem = new OBJ_Key(gp, x, y);
            case "boots" -> newItem = new OBJ_Boots(gp, x, y);
            case "sword" -> newItem = createRandomSword(x,y);
        }
        if (newItem != null) {
            list.add(newItem);
        }
    }

    private Weapon createRandomSword(int x, int y) {
        int damageBonus = rand.nextInt(11) -5;
        return new OBJ_Sword(gp, x, y, 20 + damageBonus);
    }

    public WeaponRarity determineWeaponRarity(){
        int roll = rand.nextInt(100);
        if(roll < 50) return WeaponRarity.COMMON;
        if(roll < 70) return WeaponRarity.UNCOMMON;
        if(roll < 90) return WeaponRarity.RARE;
        return WeaponRarity.LEGENDARY;
    }

}
