package object;

import main.Engine;

public class OBJ_Door extends SuperObject{

    public OBJ_Door(Engine gp, int x, int y){
        super(gp,x,y,"door","door_closed");
        image2 = scale("door_opened");
        collision = true;
    }

}
