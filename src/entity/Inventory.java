package entity;

import main.Engine;
import object.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * A játékos leltárát kezelő osztály.
 * Kezeli a tárgyak felvételét, eldobását és használatát.
 */
public class Inventory {
    private final Engine eng;
    private final ArrayList<Item> items;
    private static final int maxSize = 3;

    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Létrehoz egy új leltárat.
     * 
     * @param eng a játékmotor példánya
     */
    public Inventory(Engine eng) {
        this.eng = eng;
        items = new ArrayList<>(maxSize);
    }

    /**
     * Visszaadja az aktuálisan kiválasztott tárgyat.
     * 
     * @return az aktuális tárgy vagy null ha a leltár üres
     */
    public Item getCurrent() {
        if (!items.isEmpty())
            return items.getFirst();
        else
            return null;
    }

    public boolean isFull() {
        return items.size() >= maxSize;
    }

    public void addItem(Item item) {
        if (items.size() < maxSize) {
            items.add(item);
        }
    }

    private void destroy(Item item) {
        items.remove(item);
    }

    public void removeItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(itemName)) {
                items.remove(i);
                return;
            }
        }
    }

    public boolean equalsKey() {
        return getCurrent() instanceof KeyItem;
    }

    /**
     * Elforgatja a leltár tartalmát.
     * Az első elem a lista végére kerül.
     */
    public void rotate() {
        if (items.size() > 1) {
            Item temp = items.getFirst();
            items.removeFirst();
            items.add(temp);
        }
    }

    /**
     * Eldobja az aktuálisan kiválasztott tárgyat.
     * A tárgy a játékos előtt jelenik meg a pályán.
     * NOTE: Drop functionality is NYI - items cannot be dropped back into the world
     * yet
     */
    public void drop() {
        // TODO: Implement item dropping - need to create corresponding OBJ_* from *Item
        // For now, items cannot be dropped
    }

    public void objectExpired(Item obj) {
        destroy(obj);
    }

    /**
     * Frissíti a leltár állapotát.
     * Kezeli a tárgyak használatát és elhasználódását.
     */
    public void update() {
        // Currently items are only used when the player explicitly uses them
        // No passive usage needed
    }

    private void drawUsageBar(Graphics2D g2, int index) {
        Item item = items.get(index);
        Components.Durability durability = null;

        // Check if item has durability
        if (item instanceof BootsItem) {
            durability = ((BootsItem) item).getDurability();
        } else if (item instanceof SwordItem) {
            durability = ((SwordItem) item).getDurability();
        }

        if (durability == null)
            return;

        int screenX = 10 + 4;
        int screenY = 3 * eng.getTileSize() + (eng.getTileSize() + 10) * index;

        g2.setColor(Color.BLACK);
        g2.fillRect(screenX, screenY, eng.getTileSize() - 7, 7);
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY, eng.getTileSize() - 7, 5);
        g2.setColor(Color.BLUE);
        int blueWidth = (int) ((double) durability.getCurrent() / durability.getMax() * eng.getTileSize());
        g2.fillRect(screenX, screenY, blueWidth - 7, 5);
    }

    private void drawAmmoBar(Graphics2D g2, int index) {
        int screenX = 16;
        int screenY = 3 * eng.getTileSize() + (eng.getTileSize() + 10) * index - 2;
        Item item = items.get(index);

        if (item instanceof GunItem) {
            GunItem gun = (GunItem) item;
            Components.Magazine mag = gun.getMagazine();
            g2.drawString(mag.getCurrentMag() + "/" + mag.getReserve(), screenX, screenY);
        }
    }

    /**
     * Kirajzolja a leltár felületét.
     * 
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
                // Items don't have images directly - we'd need to load item icons separately
                // For now, just draw the item name
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                String itemName = items.get(i).getName();
                g2.drawString(itemName.substring(0, Math.min(6, itemName.length())), padding + 2,
                        96 + (slotSize + padding) * i + 12);

                // Draw usage/ammo bars
                if (items.get(i) instanceof BootsItem || items.get(i) instanceof SwordItem) {
                    drawUsageBar(g2, i);
                } else if (items.get(i) instanceof GunItem) {
                    drawAmmoBar(g2, i);
                }
            }
        }
    }
}