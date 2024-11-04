package object;

import main.GamePanel;

public class Wearable extends SuperObject{

    public Wearable(GamePanel gp, int x, int y, String name, String imageName){
        super(gp, x, y, name, imageName);
    }

    @Override
    public void use(){
        setDurability(getDurability() - getUsageDamage());
    }

}
