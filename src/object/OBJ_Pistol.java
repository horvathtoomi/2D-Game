package object;

import entity.Player;
import main.Engine;

public class OBJ_Pistol extends WeaponPickup {
    private int ammo = 12;
    private int reserve = 24;

    public OBJ_Pistol(Engine eng, int x, int y) {
        super(eng, x, y, "pistol");
    }

    // Constructor for dropped weapons with specific ammo
    public OBJ_Pistol(Engine eng, int x, int y, int ammo, int reserve) {
        super(eng, x, y, "pistol");
        this.ammo = ammo;
        this.reserve = reserve;
    }

    @Override
    public void onPickup(Player player) {
        if (!player.getInventory().isFull()) {
            player.getInventory().addItem(new PistolItem(eng, ammo, reserve));
            eng.aSetter.list.remove(this);
        }
    }
}