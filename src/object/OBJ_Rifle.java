package object;

import entity.Player;
import main.Engine;

// --- RIFLE PICKUP ---
public class OBJ_Rifle extends WeaponPickup {
    private int ammo = 30;
    private int reserve = 60;

    public OBJ_Rifle(Engine eng, int x, int y) {
        super(eng, x, y, "rifle");
    }

    @Override
    public void onPickup(Player player) {
        if (!player.getInventory().isFull()) {
            player.getInventory().addItem(new RifleItem(eng, ammo, reserve));
            eng.aSetter.list.remove(this);
        }
    }
}