package entity;

import object.SuperObject;
import java.awt.*;
import java.util.ArrayList;

public class Inventory {
    private final ArrayList<SuperObject> items;
    private final int maxSize = 3;

    public Inventory() {
        items = new ArrayList<>(maxSize);
    }

    public boolean addItem(SuperObject item) {
        if (items.size() < maxSize) {
            items.add(item);
            return true;
        }
        return false;
    }

    public boolean removeItem(SuperObject item) {return items.remove(item);}
    public boolean removeItem(String itemName) {return items.removeIf(item -> item.name.equals(itemName));}
    public boolean hasItem(String itemName) {return items.stream().anyMatch(item -> item.name.equals(itemName));}

    public void draw(Graphics2D g2) {
        int padding = 10; // Padding between slots
        for (int i = 0; i < maxSize; i++) { // Draw inventory slots
            g2.setColor(new Color(70, 70, 70, 200)); // Draw slot background
            int slotSize = 48; // Size of each inventory slot
            g2.fillRect(padding, 96 + (slotSize + padding) * i, slotSize, slotSize);
            g2.setColor(Color.WHITE); // Draw slot border
            g2.drawRect(padding, 96 + (slotSize + padding) * i, slotSize, slotSize);
            if (i < items.size() && items.get(i) != null) { // Draw item if slot is filled
                g2.drawImage(items.get(i).image,
                        padding,
                        96 + (slotSize + padding) * i,
                        slotSize,
                        slotSize,
                        null);
            }
        }
    }

    public boolean isFull() {
        return items.size() >= maxSize;
    }
}