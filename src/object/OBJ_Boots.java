package object;

import main.GamePanel;

public class OBJ_Boots extends SuperObject{

    private final int MAX_DURABILITY;
    private int durability;
    private static final int usageDamage = 1;

    public int getMaxDurability(){return MAX_DURABILITY;}
    public int getDurability(){return durability;}

    public OBJ_Boots(GamePanel gp, int x, int y){
        super(gp,x,y,"boots","boots");
        MAX_DURABILITY = 30 * gp.getFPS();
        durability = MAX_DURABILITY;
    }

    @Override
    public void use(){
        if(durability > 0)
            durability-=usageDamage;
    }

}
