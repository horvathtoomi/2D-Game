package object;

import entity.Inventory;
import main.Engine;

/**
 * A kulcs tárgy osztálya, amely ajtók nyitására használható.
 */
public class OBJ_Key extends SuperObject{

    /**
     * Létrehoz egy új kulcs objektumot.
     * @param eng játékmotor példány
     * @param x kezdő X pozíció
     * @param y kezdő Y pozíció
     */
    public OBJ_Key(Engine eng,int x,int y){
        super(eng,x,y,"key","key");
    }

    @Override
    public void interact(){
        Inventory inv = eng.player.getInventory();
        if(!inv.isFull()) {
            inv.addItem(this);
            eng.aSetter.list.remove(this);
        }
    }
}
