package object;

import main.Engine;

/**
 * A cipő tárgy osztálya, amely növeli a játékos sebességét.
 */
public class OBJ_Boots extends Wearable{

    private static final int bootsUsageDamage = 1;

    /**
     * Létrehoz egy új csizma objektumot.
     * @param eng játékmotor példány
     * @param x kezdő X pozíció
     * @param y kezdő Y pozíció
     */
    public OBJ_Boots(Engine eng, int x, int y){
        super(eng,x,y,"boots","boots");
        setMaxDurability(60 * eng.getFPS());
        setDurability(getMaxDurability());
        setUsageDamage(bootsUsageDamage);
    }

}
