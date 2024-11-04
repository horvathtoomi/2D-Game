package entity;

import main.GamePanel;
import object.*;

import java.awt.*;
import java.util.ArrayList;

public class Inventory {
    private final GamePanel gp;
    private final ArrayList<SuperObject> items;
    private static final int maxSize = 3;

    public Inventory(GamePanel gp) {
        this.gp = gp;
        items = new ArrayList<>(maxSize);
    }

    public SuperObject getCurrent() {
        if (!items.isEmpty())
            return items.getFirst();
        else
            return null;
    }

    public int getMaxSize(){
        return maxSize;
    }

    public boolean hasItem(String itemName) {
        return items.stream().anyMatch(item -> item.name.equals(itemName));
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

    public boolean equalsBoots(){
        return getCurrent() instanceof OBJ_Boots;
    }

    public boolean equalsWeapon(){
        return getCurrent() instanceof Weapon;
    }



    public void rotate() {
        if (items.size() > 1) {
            SuperObject temp = items.getFirst();
            items.removeFirst();
            items.add(temp);
        }
    }

    public SuperObject createDroppable(Entity ent){
        int offSetX, offSetY;
        switch (ent.direction) {
            case "up" -> {
                offSetX = 0;
                offSetY = gp.getTileSize();
            }
            case "down" -> {
                offSetX = 0;
                offSetY = -(gp.getTileSize());
            }
            case "left" -> {
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
        return switch(items.getFirst().name){
            case "key" -> new OBJ_Key(gp, x, y);
            case "boots" -> new OBJ_Boots(gp, x, y);
            case "sword" -> new OBJ_Sword(gp, x, y,100);
            default -> null;
        };
    }

    public void drop(){
        if(!items.isEmpty()){
            SuperObject obj = createDroppable(gp.player);
            if(obj != null) {
                gp.aSetter.list.add(createDroppable(gp.player));
                items.removeFirst();
            }
        }
    }

    private void drawUsageBar(Graphics2D g2, int index) {
        int screenX = 10 + 4;
        int screenY = 3 * gp.getTileSize() + (gp.getTileSize() + 10) * index;

        OBJ_Boots boots = (OBJ_Boots)items.get(index);
        if(boots.getDurability()<1)
            destroy(items.get(index));
        if(getCurrent() instanceof OBJ_Boots) {
            items.get(index).use();
        }

        g2.setColor(Color.BLACK);
        g2.fillRect(screenX, screenY, gp.getTileSize() - 7, 7);
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY, gp.getTileSize() - 7, 5);
        g2.setColor(Color.BLUE);
        int blueWidth = (int) ((double) boots.getDurability() / boots.getMaxDurability() * gp.getTileSize());
        g2.fillRect(screenX, screenY, blueWidth - 7, 5);
    }

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
                if(items.get(i) instanceof OBJ_Boots){
                    drawUsageBar(g2, i);
                }
            }
        }
    }
}