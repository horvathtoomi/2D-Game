package entity.attack;

import main.Engine;

public class GiantEnemyAttack extends Attack {

    public GiantEnemyAttack(Engine gp, int startX, int startY, int targetX, int targetY) {
        super(gp,"GiantEnemyAttack",100,startX,startY,targetX,targetY);
    }

}
