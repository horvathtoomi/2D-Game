package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Key extends SuperObject{

    public OBJ_Key(GamePanel gp,int x,int y){
        worldX=x;
        worldY=y;
        this.gp=gp;
        name="key";
        try {
            image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("objects/kulsc.png"));
            uTool.scaleImage(image,gp.tileSize,gp.tileSize);
        }catch(IOException e){e.printStackTrace();}
    }

}
