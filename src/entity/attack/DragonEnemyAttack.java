package entity.attack;

import main.Engine;

public class DragonEnemyAttack extends Attack {

    public DragonEnemyAttack(Engine gp, int startX, int startY, int targetX, int targetY) {
        super(gp,"DragonEnemyAttack",25,startX,startY,targetX,targetY);
    }

}
