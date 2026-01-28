package object;

import entity.Player;
import main.Engine;
import main.UtilityTool;

import java.awt.image.BufferedImage;

/**
 * Az ajtó tárgy osztálya, amely kulccsal nyitható.
 */
public class OBJ_Door extends GameObject implements Interactable {
    private boolean opened = false;
    public BufferedImage imageOpened;

    public OBJ_Door(Engine eng, int x, int y) {
        super(eng, x, y, "door_closed");
        imageOpened = scale("objects", "door_opened");
        this.collision = true;
    }

    @Override
    public void interact(Player player) {
        if (opened)
            return;
        boolean hasKey = eng.player.getInventory().equalsKey();

        if (hasKey) {
            player.getInventory().removeItem("Key");
            opened = true;
            collision = false;
            image = imageOpened;
        }
    }
}