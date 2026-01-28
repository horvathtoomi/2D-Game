package object;

import main.Engine;
import entity.Player;

public class RifleItem extends GunItem {
    public RifleItem(Engine eng) {
        super(eng, 30, 60, 5);
    }

    public RifleItem(Engine eng, int mag, int reserve) {
        super(eng, 30, mag, reserve, 5);
    }

    @Override
    protected void shootLogic(Player player) {
        // Rapid fire logic
    }

    @Override
    public String getName() {
        return "Rifle";
    }
}