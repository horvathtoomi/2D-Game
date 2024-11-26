package object;

import main.Engine;

/**
 * Az ajtó tárgy osztálya, amely kulccsal nyitható.
 */
public class OBJ_Door extends SuperObject{

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

}
