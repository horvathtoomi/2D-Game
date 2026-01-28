package object;

import entity.Player;
import main.Engine;

// --- BOOTS PICKUP ---
public class OBJ_Boots extends GameObject implements Pickupable {

    public OBJ_Boots(Engine eng, int x, int y) {
        super(eng, x, y, "boots");
    }

    @Override
    public void onPickup(Player player) {
        if (!player.getInventory().isFull()) {
            player.getInventory().addItem(new BootsItem(eng));
            eng.aSetter.list.remove(this);
        }
    }
}