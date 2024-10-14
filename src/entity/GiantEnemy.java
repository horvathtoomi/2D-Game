package entity;

import main.GamePanel;

public class GiantEnemy extends Enemy{

    public GiantEnemy(GamePanel gp, int startX, int startY){
        super(gp,"GiantEnemy",startX,startY,(int)(1.5 * gp.tileSize),(int)(2.75 * gp.tileSize),500);
    }

}
