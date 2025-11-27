package entity.attack;

import entity.Direction;
import entity.Entity;
import entity.enemy.Enemy;
import main.Engine;
import main.logger.GameLogger;

import java.awt.*;

public class Bullet extends Attack {
    private static final String LOG_CONTEXT = "[BULLET]";

    public Bullet(Engine eng, String name, int damage, int startX, int startY, int targetX, int targetY) {
        super(eng, name, damage, startX, startY, targetX, targetY);
        GameLogger.info(LOG_CONTEXT, "Instance initialized\n STARTING POSITION -> x:" + startX + " y:" + startY + "\n TARGET -> targetX:" + targetX + " targetY:" + targetY);
        setSpeed(eng.player.getSpeed() * 3);
        setTarget(eng, startX, startY);
        image = scale("objects", "PlayerBullet_" + eng.player.direction.toString());
    }

    private void setTarget(Engine eng, int startX, int startY){
        if(eng.player.direction == Direction.UP){
            dx = 0;
            dy = -getSpeed();
        } else if(eng.player.direction == Direction.DOWN){
            dx = 0;
            dy = getSpeed();
        } else if(eng.player.direction == Direction.LEFT){
            dx = -getSpeed();
            dy = 0;
        } else if(eng.player.direction == Direction.RIGHT){
            dx = getSpeed();
            dy = 0;
        }
    }

    @Override
    public void update() {
        setWorldX(getWorldX() + (int) dx);
        setWorldY(getWorldY() + (int) dy);
        solidArea.setLocation(getWorldX() + (int)dx, getWorldY() + (int)dy);
        if (checkTileCollision()) {
            eng.removeEnemy(this);
            return;
        }
        for (Entity entity : eng.getEntity()) {
            if (entity instanceof Enemy) {
                Rectangle entityHitbox = new Rectangle(entity.getWorldX() + entity.solidArea.x,
                        entity.getWorldY() + entity.solidArea.y,
                        entity.solidArea.width,
                        entity.solidArea.height);
                if (solidArea.intersects(entityHitbox)) {
                    entity.dealDamage(damage);
                    eng.removeEnemy(this);
                }
            }
        }
    }

}