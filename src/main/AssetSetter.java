package main;

import entity.npc.NPC_Wayfarer;
import object.*;
import java.io.*;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class AssetSetter {
    GamePanel gp;
    public CopyOnWriteArrayList<SuperObject> list;
    private final String[] possibleChestItems = {"key", "boots"};
    private final Random rand;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
        list = new CopyOnWriteArrayList<>();
        rand = new Random();
    }

    public void setObject() throws IOException {
        // Use try-with-resources
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("res/assetsetter/assets.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length < 3) continue;
                String name = parts[0];
                int x = Integer.parseInt(parts[1]) * gp.getTileSize();
                int y = Integer.parseInt(parts[2]) * gp.getTileSize();

                createObject(name, x, y);
            }
        }
    }

    private void createObject(String name, int x, int y) {
        SuperObject obj = switch (name) {
            case "key" -> new OBJ_Key(gp, x, y);
            case "chest" -> new OBJ_Chest(gp, x, y);
            case "door" -> new OBJ_Door(gp, x, y);
            case "boots" -> new OBJ_Boots(gp, x, y);
            default -> null;
        };

        if (obj != null) {
            list.add(obj);
        }
    }

    public void spawnItemFromChest(int x, int y) {
        String randomItem = possibleChestItems[rand.nextInt(possibleChestItems.length)];
        SuperObject newItem = null;

        // Create the new item
        switch (randomItem) {
            case "key" -> newItem = new OBJ_Key(gp, x, y);
            case "boots" -> newItem = new OBJ_Boots(gp, x, y);
        }

        if (newItem != null) {
            list.add(newItem);
        }
    }

    public void setNPC(){
        NPC_Wayfarer wf = new NPC_Wayfarer(gp);
        wf.setWorldX(gp.getTileSize()*21);
        wf.setWorldY(gp.getTileSize()*21);
        gp.entities.add(wf);
    }

}
