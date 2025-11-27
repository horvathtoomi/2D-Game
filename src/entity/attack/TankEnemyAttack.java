package entity.attack;

import main.Engine;

public class TankEnemyAttack extends Attack{

    public TankEnemyAttack(Engine eng, int startX, int startY, int targetX, int targetY) {
        super(eng,"TankEnemyAttack",15,startX,startY,targetX,targetY);
        image1 = scale("objects",name+"1");
        image2 = scale("objects",name+"2");
        image = image1;
    }
}
