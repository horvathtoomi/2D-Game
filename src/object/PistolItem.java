package object;

import entity.Player;
import entity.attack.Bullet;
import main.Engine;

public class PistolItem extends GunItem {

    public PistolItem(Engine eng, int mag, int reserve) {
        super(eng, 12, mag, reserve, 0);
    }

    @Override
    protected void shootLogic(Player p) {
        eng.getEntity().add(new Bullet(eng, "bullet", 40, p.getWorldX(), p.getWorldY(), 0, 0));
    }

    @Override
    public String getName() {
        return "Pistol";
    }

}