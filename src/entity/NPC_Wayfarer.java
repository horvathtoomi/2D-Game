package entity;

import main.GamePanel;

import java.util.Random;

public class NPC_Wayfarer extends Entity{

    public NPC_Wayfarer(GamePanel gp) {
        super(gp);
        direction = "down";
        setSpeed(1);
        getWayfarerImages();
        name = "NPC_Wayfarer";
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
                direction = "up";
            }
            if (i > 25 && i <= 50) {
                direction = "down";
            }
            if (i > 50 && i <= 75) {
                direction = "left";
            }
            if (i > 75) {
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }

}
