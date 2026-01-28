package object;

import entity.Player;
import main.Engine;
import main.UtilityTool;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * A láda tárgy osztálya, amely véletlenszerű tárgyakat tartalmaz.
 */
public class OBJ_Chest extends GameObject implements Interactable {

    private final BufferedImage openedImage;
    public boolean opened = false;

    /**
     * Létrehoz egy új láda objektumot.
     * 
     * @param eng játékmotor példány
     * @param x   kezdő X pozíció
     * @param y   kezdő Y pozíció
     */
    public OBJ_Chest(Engine eng, int x, int y) {
        super(eng, x, y, "chest_closed");
        this.name = "chest";
        openedImage = UtilityTool.getImage("objects","chest_opened");
    }

    @Override
    public void interact(Player player) {
        if (!opened) {
            image = openedImage;
            opened = true;
            int offsetX = worldX + eng.getTileSize() / 2;
            int offsetY = worldY + eng.getTileSize() / 2;
            eng.aSetter.spawnItemFromChest(offsetX, offsetY);
        }
    }
}