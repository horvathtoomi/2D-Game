package entity;

import main.GamePanel;

public class EnemyTestAttack extends Attack{

    public EnemyTestAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        super(gp,"EnemyTestAttack",50,startX,startY,targetX,targetY);
    }

}
