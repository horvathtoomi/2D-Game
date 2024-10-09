package entity;

import main.GamePanel;
import main.UtilityTool;
import object.SuperObject;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Entity {
    public int worldX,worldY;
    public int speed;
    public int health;
    public BufferedImage right,left,up,down;
    public String direction;
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    public BufferedImage scale(GamePanel gp, String folderName, String imageName){
        UtilityTool uTool = new UtilityTool();
        BufferedImage bufim = null;
        try{
            bufim = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(folderName + "/" + imageName + ".png")));
            bufim = uTool.scaleImage(bufim, gp.tileSize, gp.tileSize);

        }catch(IOException e){e.printStackTrace();}
        return bufim;
    }

}
