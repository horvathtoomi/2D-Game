package object;

import entity.Player;
import main.Engine;

/**
 * A kard tárgy osztálya, amely támadásra használható.
 */
public class OBJ_Sword extends WeaponPickup {
    private final int baseDamage;

    public OBJ_Sword(Engine eng, int x, int y, int baseDamage) {
        super(eng, x, y, "sword");
        this.baseDamage = baseDamage;
    }

    @Override
    public void onPickup(Player player) {
        if (!player.getInventory().isFull()) {
            player.getInventory().addItem(new SwordItem(eng, baseDamage, rarity));
            eng.aSetter.list.remove(this);
        }
    }
}