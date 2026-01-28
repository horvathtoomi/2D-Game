package object;

import main.Engine;
import entity.Player;

public class PistolItem extends GunItem {

    public PistolItem(Engine eng) {
        super(eng, 12, 24, 30);
    }

    public PistolItem(Engine eng, int mag, int reserve) {
        super(eng, 12, mag, reserve, 30);
    }

    @Override
    protected void shootLogic(Player player) {
        // Create projectile entity here
        // new Projectile(eng, player.x, player.y, ...);
    }

    @Override
    public String getName() {
        return "Pistol";
    }

}