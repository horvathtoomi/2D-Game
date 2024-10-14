package entity;

import main.GamePanel;

public class SmallEnemyAttack extends Attack{

    public SmallEnemyAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        super(gp,"SmallEnemyAttack",33, startX, startY, targetX, targetY);
    }

}
