package main;

import object.*;
import java.util.*;

public class AssetSetter {
    GamePanel gp;
    public ArrayList<SuperObject> lista;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
        lista = new ArrayList<>();
    }

    public void setObject(String name,int x, int y){
        switch(name){
            case "key":
                lista.add(new OBJ_Key(gp,x,y));
                break;
            case "chest":
                lista.add(new OBJ_Chest(gp,x,y));
                break;
            case "door":
                lista.add(new OBJ_Door(gp,x,y));
                break;
            case "boots":
                lista.add(new OBJ_Boots(gp,x,y));
                break;
            case "EnemyTestAttack":
                lista.add(new EnemyTestAttack(gp,x,y));
                break;
            default:
                System.out.println("Object not found");
                break;
        }
    }
}
