package object;

import entity.Player;
import main.Engine;

/**
 * A kulcs tárgy osztálya, amely ajtók nyitására használható.
 */
public class OBJ_Key extends GameObject implements Pickupable {
    public OBJ_Key(Engine eng, int x, int y) {
        super(eng, x, y, "key");
    }

    @Override
    public void onPickup(Player player) {
        if (!player.getInventory().isFull()) {
            player.getInventory().addItem(new KeyItem());
            eng.aSetter.list.remove(this);
        }
    }
}