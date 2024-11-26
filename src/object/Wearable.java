package object;

import main.Engine;

/**
 * A viselhető tárgyak absztrakt ősosztálya.
 */
public class Wearable extends SuperObject{

    /**
     * Létrehoz egy új viselhető tárgyat.
     * @param eng játékmotor példány
     * @param x kezdő X pozíció
     * @param y kezdő Y pozíció
     * @param name tárgy neve
     * @param imageName a tárgy képének neve
     */
    public Wearable(Engine eng, int x, int y, String name, String imageName){
        super(eng, x, y, name, imageName);
    }

    /**
     * A tárgy használatának kezelése, csökkenti a tartósságot.
     */
    @Override
    public void use(){
        setDurability(getDurability() - getUsageDamage());
    }

}
