package entity;

import main.GamePanel;

public class DragonEnemy extends Enemy {

    public DragonEnemy(GamePanel gp, int startX, int startY){
        super(gp,"DragonEnemy",startX,startY,(int)(2.25 * gp.tileSize),(int)(1.5 * gp.tileSize),250);
    }

}