package main;

import entity.Entity;
import entity.NPC_Wayfarer;
import object.*;

import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class AssetSetter {
    GamePanel gp;
    public CopyOnWriteArrayList<SuperObject> lista;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
        lista = new CopyOnWriteArrayList<>();
    }

    public void setObject() throws IOException {
        File file = new File("res/assetsetter/assets.txt");
        if (!file.exists()) {
            System.out.println("File does not exist or cannot be found: " + file.getAbsolutePath());
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        while ((line = br.readLine()) != null) {
            String name = line.substring(0, line.indexOf(' '));
            String[] parts = line.split(" ");
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            switch (name) {
                case "key":
                    lista.add(new OBJ_Key(gp, x* gp.tileSize, y*gp.tileSize));
                    break;
                case "chest":
                    lista.add(new OBJ_Chest(gp, x* gp.tileSize, y* gp.tileSize));
                    break;
                case "door":
                    lista.add(new OBJ_Door(gp, x* gp.tileSize, y* gp.tileSize));
                    break;
                case "boots":
                    lista.add(new OBJ_Boots(gp, x* gp.tileSize, y* gp.tileSize));
                    break;
                case "EnemyTestAttack":
                    // KIZÁRÓLAG TESZT, A PÁLYÁN ALAPVETŐEN NINCS ELHELYEZVE EZ AZ OBJECT
                    int playerWorldX = gp.player.worldX;
                    int playerWorldY = gp.player.worldY;
                    gp.addObject(new EnemyTestAttack(gp, 20 * gp.tileSize, 20 * gp.tileSize, playerWorldX, playerWorldY));
                    break;
                default:
                    System.out.println("Object not found");
                    break;
            }
        }
        br.close();
    }

    public void setNPC(){
        NPC_Wayfarer wf = new NPC_Wayfarer(gp);
        wf.worldX = gp.tileSize*21;
        wf.worldY = gp.tileSize*21;
        gp.npc.add(wf);

    }

}
