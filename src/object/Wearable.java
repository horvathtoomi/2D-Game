package object;

import entity.Inventory;
import main.Engine;

/**
 * A viselhető tárgyak absztrakt ősosztálya.
 */
public class Wearable extends SuperObject{

    private int MAX_DURABILITY;
    private int durability = 100;
    private int usageDamage = 0;

    public int getMaxDurability(){return MAX_DURABILITY;}
    public int getDurability(){return durability;}
    public int getUsageDamage(){return usageDamage;}

    public void setMaxDurability(int a) {MAX_DURABILITY = a;}
    public void setDurability(int a){durability = a;}
    public void setUsageDamage(int a){usageDamage = a;}


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

    @Override
    public void interact(){
        Inventory inv = eng.player.getInventory();
        if(!inv.isFull()) {
            inv.addItem(this);
            eng.aSetter.list.remove(this);
        }
    }

}
