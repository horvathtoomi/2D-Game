package entity;

import main.GamePanel;

public class SmallEnemy extends Enemy{

    public SmallEnemy(GamePanel gp, int startX, int startY){
        super(gp,"SmallEnemy",startX,startY,gp.getTileSize(), gp.getTileSize(),125);
    }

}
