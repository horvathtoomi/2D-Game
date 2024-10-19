package entity.attack;

import entity.Entity;
import entity.npc.NPC_Wayfarer;
import entity.enemy.Enemy;
import entity.enemy.FriendlyEnemy;
import main.GamePanel;

public class FriendlyEnemyAttack extends Attack {

    public FriendlyEnemyAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        super(gp, "FriendlyEnemyAttack", 20, startX, startY, targetX, targetY);
        setSpeed(8);
    }

    @Override
    public void update() {
        super.update();
        for (Entity entity : gp.entities) {
            if (entity instanceof Enemy && !(entity instanceof FriendlyEnemy) && !(entity instanceof NPC_Wayfarer)) {
                if (entity.solidArea.intersects(this.solidArea)) {
                    (entity).setHealth(entity.getHealth() - damage);
                    gp.entities.remove(this);
                    return;
                }
            }
        }
    }
}