package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class OBJ_Chest extends SuperObject{

    public OBJ_Chest(GamePanel gp,int x, int y){
        worldX=x;
        worldY=y;
        this.gp=gp;
        name="chest";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/lada.png")));
        }catch(IOException e){e.getCause();}
    }
}
