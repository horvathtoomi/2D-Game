package entity.attack;

import main.GamePanel;

public class DragonEnemyAttack extends Attack {

    public DragonEnemyAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        super(gp,"DragonEnemyAttack",25,startX,startY,targetX,targetY);
    }

}
