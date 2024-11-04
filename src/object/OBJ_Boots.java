package object;

import main.GamePanel;

public class OBJ_Boots extends Wearable{

    private static final int bootsUsageDamage = 1;

    public OBJ_Boots(GamePanel gp, int x, int y){
        super(gp,x,y,"boots","boots");
        setUsageDamage(bootsUsageDamage);
    }

}
