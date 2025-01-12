package object;

import entity.attack.PlasmaRay;
import main.Engine;

public class PlasmaCannon extends Shooter{

    private static final int maxDamage = 300;
    private static final int minDamage = 10;
    private static final double minToMaxTime = 3.0;
    private static final double dT = 0.001;
    private static Engine eng;

    public PlasmaCannon(Engine eng, int x, int y) {
        super(eng, x, y, "plasma_cannon", "plasma_cannon", Integer.MAX_VALUE);
        PlasmaCannon.eng = eng;
    }

    public static PlasmaRay getAdjustedRay(double duration){
        double frac = duration * dT / minToMaxTime;                                   //Duration must be in milliseconds
        int damage = (maxDamage * frac) < minDamage ? minDamage : (int)(maxDamage * frac);
        return new PlasmaRay(eng, "plasma_ray", damage, 5,5,5,5 );
    }


}
