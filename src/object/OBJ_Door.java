package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class OBJ_Door extends SuperObject{

    public OBJ_Door(GamePanel gp, int x, int y){
        worldX=x;
        worldY=y;
        this.gp=gp;
        name="door";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/ajto.png")));
        }catch(IOException e){e.printStackTrace();}
        collision = true;
    }


}
