package object;

import entity.Inventory;
import main.Engine;

/**
 * Az ajtó tárgy osztálya, amely kulccsal nyitható.
 */
public class OBJ_Door extends SuperObject{

    public boolean collision;

    /**
     * Létrehoz egy új ajtó objektumot.
     * @param eng játékmotor példány
     * @param x kezdő X pozíció
     * @param y kezdő Y pozíció
     */
    public OBJ_Door(Engine eng, int x, int y){
        super(eng,x,y,"door","door_closed");
        image2 = scale("door_opened");
        collision = true;
    }

    @Override
    public void interact(){
        if(collision) {
            Inventory inv = eng.player.getInventory();
            if (inv.equalsKey()) {
                inv.removeItem("key");
                collision = false;
                image = image2;
            }
        }
    }

}
