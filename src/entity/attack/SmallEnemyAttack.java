package entity.attack;

import main.Engine;

public class SmallEnemyAttack extends Attack {

    public SmallEnemyAttack(Engine gp, int startX, int startY, int targetX, int targetY) {
        super(gp,"SmallEnemyAttack",0, startX, startY, targetX, targetY);
    }

}
