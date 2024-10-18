package entity;

import main.GamePanel;

public class FriendlyEnemyAttack extends Attack {

    public FriendlyEnemyAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        super(gp,"FriendlyEnemyAttack",25,startX,startY,targetX,targetY);
        setSpeed(10);
    }

}
