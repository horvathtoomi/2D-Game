package entity;

import main.GamePanel;

public class GiantEnemyAttack extends Attack {

    public GiantEnemyAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        super(gp,"GiantEnemyAttack",200,startX,startY,targetX,targetY);
    }

}
