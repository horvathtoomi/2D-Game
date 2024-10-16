package entity;

import main.GamePanel;

public class SmallEnemy extends Enemy{

    public SmallEnemy(GamePanel gp, int startX, int startY){
        super(gp,"SmallEnemy",startX,startY,(int)(gp.getTileSize() * 1.5), (int)(gp.getTileSize() * 1.5),125);
    }

}
