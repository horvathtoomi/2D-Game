package main;

import entity.Entity;
import entity.enemy.*;
import entity.npc.NPC_Wayfarer;
import main.logger.GameLogger;
import object.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Az AsssetSetter osztály felelős a játék objektumainak és ellenségeinek
 * inicializálásáért és kezeléséért.
 */
public class AssetSetter {
    Engine eng;
    public List<GameObject> list;
    private final String[] possibleChestItems = { "key", "boots", "sword", "pistol", "rifle" };
    private final Random rand;
    private int mapNum = 1;
    private static final String LOG_CONTEXT = "[ASSET SETTER]";

    /**
     * Létrehoz egy új AssetSetter példányt.
     * 
     * @param eng a játékmotor példánya
     */
    public AssetSetter(Engine eng) {
        this.eng = eng;
        list = new CopyOnWriteArrayList<>();
        rand = new Random();
    }

    /**
     * Betölti a pályához tartozó objektumokat és ellenségeket.
     * 
     * @param restart jelzi, hogy újrakezdésről van-e szó
     */
    public void loadLevelAssets(boolean restart) {
        try {
            setEnemies(restart);
            setObject(restart);
        } catch (IOException e) {
            GameLogger.error(LOG_CONTEXT, "Assets could not be set.", e);
        }
    }

    /**
     * Beállítja a pálya objektumait a konfigurációs fájl alapján.
     * 
     * @param restart jelzi, hogy újrakezdésről van-e szó
     * @throws IOException ha a fájl nem olvasható
     */
    public void setObject(boolean restart) throws IOException {
        if (restart) {
            mapNum = 1;
        }
        list.clear();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("res/assets/map" + mapNum + "_assets.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length != 3)
                    continue;
                String name = parts[0];
                int x = Integer.parseInt(parts[1]) * eng.getTileSize();
                int y = Integer.parseInt(parts[2]) * eng.getTileSize();
                createObject(name, x, y);
            }
            mapNum++;
        }
    }

    /**
     * Beállítja a pálya ellenségeit a konfigurációs fájl alapján.
     * 
     * @param restart jelzi, hogy újrakezdésről van-e szó
     * @throws IOException ha a fájl nem olvasható
     */
    public void setEnemies(boolean restart) throws IOException {
        if (restart) {
            mapNum = 1;
        }
        list.clear();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("res/enemies/map" + mapNum + "_enemies.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length != 3)
                    continue;
                String name = parts[0];
                int x = Integer.parseInt(parts[1]) * eng.getTileSize();
                int y = Integer.parseInt(parts[2]) * eng.getTileSize();
                createEnemy(name, x, y);
            }
        }
    }

    /**
     * Létrehoz egy új objektumot a megadott paraméterek alapján.
     * 
     * @param name az objektum neve
     * @param x    X koordináta
     * @param y    Y koordináta
     */
    public void createObject(String name, int x, int y) {
        GameObject obj = switch (name) {
            case "key" -> new OBJ_Key(eng, x, y);
            case "chest" -> new OBJ_Chest(eng, x, y);
            case "door" -> new OBJ_Door(eng, x, y);
            case "boots" -> new OBJ_Boots(eng, x, y);
            case "sword" -> new OBJ_Sword(eng, x, y, 50);
            case "pistol" -> new OBJ_Pistol(eng, x, y);
            case "rifle" -> new OBJ_Rifle(eng, x, y);
            default -> null;
        };
        if (obj != null) {
            list.add(obj);
        } else {
            GameLogger.error(LOG_CONTEXT, "Unexpected type listed in one of the assets file",
                    new IllegalArgumentException("Illegal argument"));
        }
    }

    /**
     * Létrehoz egy új ellenséget a megadott paraméterek alapján.
     * 
     * @param name az ellenség típusa
     * @param x    X koordináta
     * @param y    Y koordináta
     */
    private void createEnemy(String name, int x, int y) {
        Entity ent = switch (name.toLowerCase()) {
            case "dragonenemy" -> new DragonEnemy(eng, x, y);
            case "friendlyenemy" -> new FriendlyEnemy(eng, x, y);
            case "giantenemy" -> new GiantEnemy(eng, x, y);
            case "smallenemy" -> new SmallEnemy(eng, x, y);
            case "tankenemy" -> new TankEnemy(eng, x, y);
            case "npc_wayfarer" -> new NPC_Wayfarer(eng, x, y);
            default -> null;
        };
        if (ent != null)
            eng.addEntity(ent);
        else
            GameLogger.error(LOG_CONTEXT, "Unexpected type listed in one of the enemies file",
                    new IllegalArgumentException("ILLEGAL ARGUMENT"));
    }

    /**
     * Létrehoz egy véletlenszerű tárgyat egy láda kinyitásakor.
     * 
     * @param x a láda X koordinátája
     * @param y a láda Y koordinátája
     */
    public void spawnItemFromChest(int x, int y) {
        String randomItem = possibleChestItems[rand.nextInt(possibleChestItems.length)];
        GameObject newItem = null;
        switch (randomItem) {
            case "key" -> newItem = new OBJ_Key(eng, x, y);
            case "boots" -> newItem = new OBJ_Boots(eng, x, y);
            case "sword" -> newItem = createRandomSword(x, y);
            case "pistol" -> newItem = new OBJ_Pistol(eng, x, y);
            case "rifle" -> newItem = new OBJ_Rifle(eng, x, y);
        }
        if (newItem != null) {
            list.add(newItem);
        }
    }

    /**
     * Létrehoz egy véletlenszerű tulajdonságokkal rendelkező kardot.
     * 
     * @param x a kard X koordinátája
     * @param y a kard Y koordinátája
     * @return az elkészített Weapon objektum
     */
    private GameObject createRandomSword(int x, int y) {
        int damageBonus = rand.nextInt(11) - 5;
        return new OBJ_Sword(eng, x, y, 20 + damageBonus);
    }

    /**
     * Meghatározza egy fegyver ritkaságát véletlenszerűen.
     * 
     * @return a meghatározott WeaponRarity érték
     */
    public WeaponRarity determineWeaponRarity() {
        int roll = rand.nextInt(100);
        if (roll < 50)
            return WeaponRarity.COMMON;
        if (roll < 70)
            return WeaponRarity.UNCOMMON;
        if (roll < 90)
            return WeaponRarity.RARE;
        return WeaponRarity.LEGENDARY;
    }

}
