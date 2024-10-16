package main;

import entity.NPC_Wayfarer;
import object.*;
import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class AssetSetter {
    GamePanel gp;
    public CopyOnWriteArrayList<SuperObject> list;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
        list = new CopyOnWriteArrayList<>();
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
                case "key" -> list.add(new OBJ_Key(gp, x* gp.getTileSize(), y*gp.getTileSize()));
                case "chest" -> list.add(new OBJ_Chest(gp, x* gp.getTileSize(), y* gp.getTileSize()));
                case "door" -> list.add(new OBJ_Door(gp, x* gp.getTileSize(), y* gp.getTileSize()));
                case "boots" -> list.add(new OBJ_Boots(gp, x* gp.getTileSize(), y* gp.getTileSize()));
                default -> System.out.println("Object not found");
            }
        }
        br.close();
    }

    public void setNPC(){
        NPC_Wayfarer wf = new NPC_Wayfarer(gp);
        wf.setWorldX(gp.getTileSize()*21);
        wf.setWorldY(gp.getTileSize()*21);
        gp.entities.add(wf);
    }

}
