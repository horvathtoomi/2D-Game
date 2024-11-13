package object;

import main.Engine;

public class Wearable extends SuperObject{

    public Wearable(Engine gp, int x, int y, String name, String imageName){
        super(gp, x, y, name, imageName);
    }

    @Override
    public void use(){
        setDurability(getDurability() - getUsageDamage());
    }

}
