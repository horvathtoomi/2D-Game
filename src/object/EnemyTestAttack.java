package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EnemyTestAttack extends DefaultAttack{
    int imageChange = 0;
    public boolean collisionOn = false;

    public EnemyTestAttack(GamePanel gp, int x, int y) {
        worldX = x;
        worldY = y;
        this.gp=gp;
        name="EnemyTestAttack";
        try {
            image1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack1.png"));
            image2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack2.png"));
        }catch(IOException e){e.printStackTrace();}
        image=image1;
    }

    public void update(){
        imageChange++;
        if(imageChange>20) {
            if(image==image1)
                image=image2;
            else
                image=image1;
            imageChange = 0;
        }
        double direction = calculateAngle(4.5,6.8);

    }

    private double calculateAngle(double vecX, double vecY) {
        double angle = Math.toDegrees(Math.atan(vecY / vecX));
        return vecX > 0 ? angle : 180 + angle;
    }


    public void draw(Graphics2D g2){
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        g2.drawImage(image,screenX,screenY,null);
    }

}
