package object;

import main.Engine;

import java.awt.image.BufferedImage;

/**
 * A láda tárgy osztálya, amely véletlenszerű tárgyakat tartalmaz.
 */
public class OBJ_Chest extends SuperObject{

    public boolean opened = false;

    /**
     * Létrehoz egy új láda objektumot.
     * @param eng játékmotor példány
     * @param x kezdő X pozíció
     * @param y kezdő Y pozíció
     */
    public OBJ_Chest(Engine eng,int x, int y){
        super(eng,x,y,"chest","chest_closed");
        image2 = scale("chest_opened");
    }

    @Override
    public void interact(){
        if (!opened) {
            image = image2;
            opened = true;
            int offsetX = worldX + eng.getTileSize() / 2;
            int offsetY = worldY + eng.getTileSize() / 2;
            eng.aSetter.spawnItemFromChest(offsetX, offsetY);
        }
    }

}