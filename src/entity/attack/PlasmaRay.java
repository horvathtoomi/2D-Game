package entity.attack;

import main.Engine;
import main.logger.GameLogger;

public class PlasmaRay extends Attack{

    private static final String LOG_CONTEXT = "[PLASMA RAY]";

    public PlasmaRay(Engine eng, String name, int damage, int startX, int startY, int targetX, int targetY) {
        super(eng, name, damage, startX, startY, targetX, targetY);
        GameLogger.info(LOG_CONTEXT, "Instance initialized\n STARTING POSITION -> x:" + startX + " y:" + startY + "\n TARGET -> targetX:" + targetX + " targetY:" + targetY);
        image = scale("objects", "PlasmaRay_" + eng.player.direction.toUpperCase());
    }


}
