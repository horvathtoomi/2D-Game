package entity.npc;

import entity.Direction;
import entity.Entity;
import main.Engine;

import java.util.Random;

/**
 * A játékban megjelenő NPC osztály.
 * Wayfarer karaktert reprezentál, aki a pályán mozog.
 */
public class NPC_Wayfarer extends Entity {

    /**
     * Létrehoz egy új vándor NPC-t megadott pozícióval.
     * @param gp a játékmotor példánya
     * // @param x kezdő X koordináta
     * // @param y kezdő Y koordináta
     */
    public NPC_Wayfarer(Engine gp) {
        super(gp);
        direction = Direction.DOWN;
        setSpeed(1);
        getWayfarerImages();
        name = "NPC_Wayfarer";
    }

    public NPC_Wayfarer(Engine gp, int x, int y) {
        super(gp);
        direction = Direction.DOWN;
        setSpeed(1);
        getWayfarerImages();
        name = "NPC_Wayfarer";
        setWorldX(x);
        setWorldY(y);
    }

    private void getWayfarerImages(){
            right = scale("npc","right");
            left = scale("npc","left");
            down = scale("npc","down");
            up = scale("npc","up");
    }

    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter == 120) {
            Random rand = new Random();
            int i = rand.nextInt(100) + 1;
            if (i <= 25) {
                direction = Direction.UP;
            }
            if (i > 25 && i <= 50) {
                direction = Direction.DOWN;
            }
            if (i > 50 && i <= 75) {
                direction = Direction.LEFT;
            }
            if (i > 75) {
                direction = Direction.RIGHT;
            }
            actionLockCounter = 0;
        }
    }

}
