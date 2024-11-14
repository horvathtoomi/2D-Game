package entity;

import main.InputHandler;
import main.Engine;
import main.UtilityTool;
import object.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    public boolean isAttacking = false;
    private boolean hasReducedDurability = false;
    private long lastAttackTime = 0;
    private final InputHandler kezelo;
    private final Inventory inventory;
    private static final long ATTACK_COOLDOWN = 500;
    private static final int INTERACTION_COOLDOWN = 30; // frames
    private int interactionTimer = 0;
    private BufferedImage up_key, up_boots, up_sword,
            down_key, down_boots, down_sword,
            left_key, left_boots, left_sword,
            right_key, right_boots, right_sword,
            attack_up, attack_down, attack_left, attack_rigth;

    public Inventory getInventory() {
        return inventory;
    }

    public Player(Engine panel, InputHandler kezelo) {
        super(panel);
        this.kezelo = kezelo;
        setMaxHealth(100);
        setHealth(100);
        setScreenX(gp.getScreenWidth() / 2 - (gp.getTileSize() / 2));
        setScreenY(gp.getScreenHeight() / 2 - (gp.getTileSize() / 2));
        solidArea = new Rectangle(8, 16, 32, 32);
        inventory = new Inventory(gp);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        setWorldX(gp.getTileSize() * 5);
        setWorldY(gp.getTileSize() * 5);
        setSpeed(3);
        direction = "down";
    }

    public void getPlayerImage() {
        right = scale("player", "rigth");
        right_key = scale("player", "rigth_key");
        right_boots = scale("player", "rigth_boots");
        right_sword = scale("player", "rigth_sword");
        left = scale("player", "left");
        left_key = scale("player", "left_key");
        left_boots = scale("player", "left_boots");
        left_sword = scale("player", "left_sword");
        down = scale("player", "down");
        down_key = scale("player", "down_key");
        down_boots = scale("player", "down_boots");
        down_sword = scale("player", "down_sword");
        up = scale("player", "up");
        up_key = scale("player", "up_key");
        up_boots = scale("player", "up_boots");
        up_sword = scale("player", "up_sword");
        attack_up = scale("player", "attack_up");
        attack_down = scale("player", "attack_down");
        attack_left = scale("player", "attack_left");
        attack_rigth = scale("player", "attack_rigth");
        UtilityTool uTool = new UtilityTool();
        attack_up = uTool.scaleImage(attack_up, gp.getTileSize(), gp.getTileSize() * 2);
        attack_down = uTool.scaleImage(attack_down, gp.getTileSize(), gp.getTileSize() * 2);
        attack_left = uTool.scaleImage(attack_left, gp.getTileSize() * 2, gp.getTileSize());
        attack_rigth = uTool.scaleImage(attack_rigth, gp.getTileSize() * 2, gp.getTileSize());
    }

    @Override
    public void update() {
        if (interactionTimer > 0) {
            interactionTimer--;
        }

        setSpeed(inventory.getCurrent() instanceof OBJ_Boots ? 4 : 3);

        if (!kezelo.attackPressed && isAttacking) {
            isAttacking = false;
            hasReducedDurability = false;
            if (inventory.getCurrent() instanceof Weapon) {
                ((Weapon) inventory.getCurrent()).isActive = false;
            }
        }

        if (kezelo.attackPressed || kezelo.upPressed || kezelo.downPressed || kezelo.leftPressed || kezelo.rightPressed) {
            if (kezelo.upPressed) direction = "up";
            if (kezelo.downPressed) direction = "down";
            if (kezelo.leftPressed) direction = "left";
            if (kezelo.rightPressed) direction = "right";
            if (kezelo.attackPressed) attack();

            //Check Tile Collision
            collisionOn = false;
            gp.cChecker.checkTile(this);

            int objIndex = gp.cChecker.checkObject(this, true);
            if (objIndex != 999 && interactionTimer == 0) {
                interactWithObject(objIndex);
                interactionTimer = INTERACTION_COOLDOWN;
            }

            if (!collisionOn) {
                switch (direction) {
                    case "up" -> setWorldY(getWorldY() - getSpeed());
                    case "down" -> setWorldY(getWorldY() + getSpeed());
                    case "left" -> setWorldX(getWorldX() - getSpeed());
                    case "right" -> setWorldX(getWorldX() + getSpeed());
                }
            }
        }
        if (isAttacking && !hasReducedDurability && inventory.getCurrent() instanceof Weapon) {
            inventory.getCurrent().use();
            hasReducedDurability = true;
        }
    }


    private void interactWithObject(int index) {
        if (index < gp.aSetter.list.size()) {
            SuperObject obj = gp.aSetter.list.get(index);

            switch (obj.name) {
                case "key", "sword", "boots" -> {
                    if (!inventory.isFull()) {
                        inventory.addItem(obj);
                        gp.aSetter.list.remove(obj);
                    }
                }
                case "door" -> {
                    if (gp.aSetter.list.get(index).collision) {
                        if (inventory.equalsKey()) {
                            inventory.removeItem("key");
                            gp.aSetter.list.get(index).collision = false;
                            gp.aSetter.list.get(index).image = gp.aSetter.list.get(index).image2;
                        }
                    }
                }
                case "chest" -> {
                    if (!obj.opened) {
                        gp.aSetter.list.get(index).image = gp.aSetter.list.get(index).image2;
                        gp.aSetter.list.get(index).opened = true;
                        int offsetX = obj.worldX + gp.getTileSize() / 2;
                        int offsetY = obj.worldY + gp.getTileSize() / 2;
                        gp.aSetter.spawnItemFromChest(offsetX, offsetY);
                    }
                }
            }
        }
    }

    private BufferedImage getStateImage() {
        BufferedImage image = up;
        if (inventory.getCurrent() != null) {
            if (inventory.getCurrent() instanceof OBJ_Key) {
                image = switch(direction){
                    case "up" -> up_key;
                    case "down" -> down_key;
                    case "left" -> left_key;
                    case "right" -> right_key;
                    default -> null;
                };
            }
            else if(inventory.getCurrent() instanceof OBJ_Boots) {
                image = switch(direction){
                    case "up" -> up_boots;
                    case "down" -> down_boots;
                    case "left" -> left_boots;
                    case "right" -> right_boots;
                    default -> null;
                };
            }
            else if(inventory.getCurrent() instanceof Weapon) {
                if(!isAttacking) {
                    image = switch (direction) {
                        case "up" -> up_sword;
                        case "down" -> down_sword;
                        case "left" -> left_sword;
                        case "right" -> right_sword;
                        default -> null;
                    };
                }
                else{
                    image = switch (direction) {
                        case "up" -> attack_up;
                        case "down" -> attack_down;
                        case "left" -> attack_left;
                        case "right" -> attack_rigth;
                        default -> null;
                    };
                }
            }
        } else {
            image = switch (direction) {
                case "up" -> up;
                case "down" -> down;
                case "left" -> left;
                case "right" -> right;
                default -> null;
            };
        }
        return image;
    }

    public void attack(){
        if(!(getInventory().getCurrent() instanceof Weapon weapon)) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastAttackTime < ATTACK_COOLDOWN){
            return;
        }
        isAttacking = true;
        hasReducedDurability = false;
        weapon.isActive = true;
        lastAttackTime = currentTime;

        weapon.updateHitbox(getWorldX(), getWorldY(), direction);
        weapon.checkHit(gp.getEntity());
    }

    private int setAdjustedX(){
        int x = getScreenX();
        if(getScreenX() > getWorldX())
            x = getWorldX();
        int rightOffset = gp.getScreenWidth() - getScreenX();
        if (rightOffset > gp.getWorldWidth() - getWorldX())
            x = gp.getScreenWidth() - (gp.getWorldWidth() - getWorldX());
        return x;
    }

    private int setAdjustedY(){
        int y = getScreenY();
        if(getScreenY() > getWorldY())
            y = getWorldY();
        int bottomOffset = gp.getScreenHeight() - getScreenY();
        if (bottomOffset > gp.getWorldHeight() - getWorldY())
            y = gp.getScreenHeight() - (gp.getWorldHeight() - getWorldY());
        return y;
    }

    @Override
    public void draw(Graphics2D g2){
        BufferedImage image = getStateImage();
        int x = setAdjustedX();
        int y = setAdjustedY();

        if(image == attack_up)
            g2.drawImage(image,x,y - gp.getTileSize(),null);
        else if(image == attack_down)
            g2.drawImage(image,x,y,null);
        else if(image == attack_left)
            g2.drawImage(image,x - gp.getTileSize(),y,null);
        else if(image == attack_rigth)
            g2.drawImage(image,x,y,null);
        else
            g2.drawImage(image, x, y, null);
        inventory.draw(g2);
    }

}
