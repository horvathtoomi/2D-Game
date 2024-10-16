package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class OBJ_Boots extends SuperObject{
    public OBJ_Boots(GamePanel gp, int x, int y){
        worldX = x;
        worldY = y;
        this.gp=gp;
        name="boots";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/cipo.png")));
        }catch(IOException e){e.printStackTrace();}
    }
}
