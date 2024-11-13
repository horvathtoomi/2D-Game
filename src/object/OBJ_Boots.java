package object;

import main.Engine;

public class OBJ_Boots extends Wearable{

    private static final int bootsUsageDamage = 1;

    public OBJ_Boots(Engine gp, int x, int y){
        super(gp,x,y,"boots","boots");
        setMaxDurability(60 * gp.getFPS());
        setDurability(getMaxDurability());
        setUsageDamage(bootsUsageDamage);
    }

}
