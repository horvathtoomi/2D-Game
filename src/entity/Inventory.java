package entity;

import main.Engine;
import object.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A játékos leltárát kezelő osztály.
 * Kezeli a tárgyak felvételét, eldobását és használatát.
 */
public class Inventory {
    private final Engine gp;
    private final ArrayList<SuperObject> items;
    private static final int maxSize = 3;

    public ArrayList<SuperObject> getItems() {
        return items;
    }

    /**
     * Létrehoz egy új leltárat.
     * @param gp a játékmotor példánya
     */
    public Inventory(Engine gp) {
        this.gp = gp;
        items = new ArrayList<>(maxSize);
    }

    /**
     * Visszaadja az aktuálisan kiválasztott tárgyat.
     * @return az aktuális tárgy vagy null ha a leltár üres
     */
    public SuperObject getCurrent() {
        if (!items.isEmpty())
            return items.getFirst();
        else
            return null;
    }

    public boolean isFull() {
        return items.size() >= maxSize;
    }

    public void addItem(SuperObject item) {
        if (items.size() < maxSize) {
            items.add(item);
        }
    }

    private void destroy(SuperObject item) {
        items.remove(item);
    }

    public void removeItem(String itemName) {
        for(int i=0;i<items.size();i++){
            if(items.get(i).name.equals(itemName)){
                items.remove(i);
                return;
            }
        }
    }

    public boolean equalsKey(){
        return getCurrent() instanceof OBJ_Key;
    }

    /**
     * Elforgatja a leltár tartalmát.
     * Az első elem a lista végére kerül.
     */
    public void rotate() {
        if (items.size() > 1) {
            SuperObject temp = items.getFirst();
            items.removeFirst();
            items.add(temp);
        }
    }

    public SuperObject createDroppable(Entity ent){
        int offSetX;
        int offSetY;
        switch (ent.direction) {
            case UP -> {
                offSetX = 0;
                offSetY = gp.getTileSize();
            }
            case DOWN -> {
                offSetX = 0;
                offSetY = -(gp.getTileSize());
            }
            case LEFT -> {
                offSetX = gp.getTileSize();
                offSetY = 0;
            }
            default -> {
                offSetX = -(gp.getTileSize());
                offSetY = 0;
            }
        }
        int x = ent.getWorldX() + offSetX;
        int y = ent.getWorldY() + offSetY;
        items.getFirst().setWorldX(x);
        items.getFirst().setWorldY(y);
        return items.getFirst();
    }

    /**
     * Eldobja az aktuálisan kiválasztott tárgyat.
     * A tárgy a játékos előtt jelenik meg a pályán.
     */
    public void drop(){
        if(!items.isEmpty()){
            SuperObject obj = createDroppable(gp.player);
            if(obj != null) {
                gp.aSetter.list.add(createDroppable(gp.player));
                items.removeFirst();
            }
        }
    }

    /**
     * Frissíti a leltár állapotát.
     * Kezeli a tárgyak használatát és elhasználódását.
     */
    public void update(){
        if(getCurrent() instanceof Wearable) {
            getCurrent().use();
        }
        Iterator<SuperObject> iterator = items.iterator();
        while (iterator.hasNext()) {
            SuperObject obj = iterator.next();
            if (obj.getDurability() < 1) {
                iterator.remove();
                destroy(obj);
            }
        }
    }

    private void drawUsageBar(Graphics2D g2, int index) {
        int screenX = 10 + 4;
        int screenY = 3 * gp.getTileSize() + (gp.getTileSize() + 10) * index;

        SuperObject obj = items.get(index);

        g2.setColor(Color.BLACK);
        g2.fillRect(screenX, screenY, gp.getTileSize() - 7, 7);
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY, gp.getTileSize() - 7, 5);
        g2.setColor(Color.BLUE);
        int blueWidth = (int) ((double) obj.getDurability() / obj.getMaxDurability() * gp.getTileSize());
        g2.fillRect(screenX, screenY, blueWidth - 7, 5);
    }

    private void drawAmmoBar(Graphics2D g2, int index) {
        int screenX = 16;
        int screenY = 3 * gp.getTileSize() + (gp.getTileSize() + 10) * index - 2;
        Shooter sht = (Shooter)items.get(index);
        g2.drawString(sht.getCurrentMagSize() + "/" + sht.getRemainingAmmo(), screenX, screenY);
    }

    /**
     * Kirajzolja a leltár felületét.
     * @param g2 a grafikus kontextus
     */
    public void draw(Graphics2D g2) {
        int padding = 10;
        for (int i = 0; i < maxSize; i++) {
            g2.setColor(new Color(70, 70, 70, 200));
            int slotSize = 48;
            g2.fillRect(padding, 96 + (slotSize + padding) * i, slotSize, slotSize);
            g2.setColor(Color.WHITE);
            g2.drawRect(padding, 96 + (slotSize + padding) * i, slotSize, slotSize);
            if (i < items.size() && items.get(i) != null) {
                g2.drawImage(items.get(i).image, padding, 96 + (slotSize + padding) * i, slotSize, slotSize, null);
                if(items.get(i) instanceof Wearable || (items.get(i) instanceof Weapon && !(items.get(i) instanceof Shooter))){
                    drawUsageBar(g2, i);
                } else if(items.get(i) instanceof Shooter){
                    drawAmmoBar(g2, i);
                }
            }
        }
    }
}