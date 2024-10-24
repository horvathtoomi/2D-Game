package object;

import main.GamePanel;

public class OBJ_Chest extends SuperObject{

    public OBJ_Chest(GamePanel gp,int x, int y){
        super(gp,x,y,"chest","chest_closed");
        image2 = scale("chest_opened");
    }
}