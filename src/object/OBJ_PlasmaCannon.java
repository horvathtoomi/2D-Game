package object;

import entity.Player;
import main.Engine;

// --- PLASMA CANNON PICKUP ---
public class OBJ_PlasmaCannon extends WeaponPickup {
    public OBJ_PlasmaCannon(Engine eng, int x, int y) {
        super(eng, x, y, "plasma_cannon");
    }

    @Override
    public void onPickup(Player player) {
        if (!player.getInventory().isFull()) {
            player.getInventory().addItem(new PlasmaCannonItem(eng));
            eng.aSetter.list.remove(this);
        }
    }
}