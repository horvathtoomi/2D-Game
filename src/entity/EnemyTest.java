package entity;

import main.GamePanel;

public class EnemyTest extends Enemy {

    public EnemyTest(GamePanel gp, int startX, int startY){
        super(gp,"EnemyTest",startX,startY,(int)(2.25 * gp.tileSize),(int)(1.5 * gp.tileSize),250);
    }

}