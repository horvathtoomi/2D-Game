package object;

import main.GamePanel;

public class OBJ_Door extends SuperObject{

    public OBJ_Door(GamePanel gp, int x, int y){
        super(gp,x,y,"door","door_closed");
        image2 = scale("door_opened");
    }

}
