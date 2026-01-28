package object;

import entity.attack.Bullet;
import main.Engine;
import entity.Player;

public class RifleItem extends GunItem {

    public RifleItem(Engine eng, int mag, int reserve) {
        super(eng, 30, mag, reserve, 5);
    }

    @Override
    protected void shootLogic(Player p) {
        eng.getEntity().add(new Bullet(eng, "bullet", 40, p.getWorldX(), p.getWorldY(), 0, 0));
    }

    @Override
    public String getName() {
        return "Rifle";
    }
}