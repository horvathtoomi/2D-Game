package entity.attack;

import main.Engine;
import main.logger.GameLogger;

public class Bullet extends Attack{

    private static final String LOG_CONTEXT = "[BULLET]";

    public Bullet(Engine eng, String name, int damage, int startX, int startY, int targetX, int targetY){
        super(eng, name, damage, startX, startY, targetX, targetY);
        GameLogger.info(LOG_CONTEXT, "Instance initialized\n STARTING POSITION -> x:" + startX + " y:" + startY + "\n TARGET -> targetX:" + targetX + " targetY:" + targetY);
        image = scale("objects", "PlayerBullet_" + eng.player.direction.toUpperCase());
    }

}
