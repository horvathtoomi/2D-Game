package object;

import main.Engine;

/**
 * A láda tárgy osztálya, amely véletlenszerű tárgyakat tartalmaz.
 */
public class OBJ_Chest extends SuperObject{

    /**
     * Létrehoz egy új láda objektumot.
     * @param eng játékmotor példány
     * @param x kezdő X pozíció
     * @param y kezdő Y pozíció
     */
    public OBJ_Chest(Engine eng,int x, int y){
        super(eng,x,y,"chest","chest_closed");
        image2 = scale("chest_opened");
    }
}