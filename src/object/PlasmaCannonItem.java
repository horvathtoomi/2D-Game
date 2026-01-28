package object;

import entity.Player;
import entity.attack.PlasmaRay;
import main.Engine;

public class PlasmaCannonItem extends GunItem {
    public PlasmaCannonItem(Engine eng) {
        super(eng, Integer.MAX_VALUE, 0, 30);
    }

    @Override
    protected void shootLogic(Player player) {
        // Original specific logic
    }

    // Preserving static helper from original code
    public static PlasmaRay getAdjustedRay(Engine eng, double duration) {
        int maxDamage = 300;
        int minDamage = 10;
        double minToMaxTime = 3.0;
        double dT = 0.001;

        double frac = duration * dT / minToMaxTime;
        int damage = (maxDamage * frac) < minDamage ? minDamage : (int) (maxDamage * frac);
        return new PlasmaRay(eng, "plasma_ray", damage, 5, 5, 5, 5);
    }

    @Override
    public String getName() {
        return "Plasma Cannon";
    }

}