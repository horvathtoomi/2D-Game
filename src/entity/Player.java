package entity;

import entity.attack.Bullet;
import main.Engine;
import main.InputHandler;
import main.UtilityTool;
import object.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A játékos karaktert reprezentáló osztály.
 * Kezeli a játékos mozgását, támadását és interakcióit.
 */
public class Player extends Entity {
    public static boolean isAttacking = false;
    private boolean hasReducedDurability = false;
    private long lastAttackTime = 0;
    private final InputHandler kezelo;
    private final Inventory inventory;
    private static final long ATTACK_COOLDOWN = 500;
    private static final int INTERACTION_COOLDOWN = 30; // frames
    private int interactionTimer = 0;
    public int defeatedEnemies = 0;
    public static boolean shot = false;
    private BufferedImage up_key, up_boots, up_sword,
            down_key, down_boots, down_sword,
            left_key, left_boots, left_sword,
            right_key, right_boots, right_sword,
            attack_up, attack_down, attack_left, attack_rigth;

    public void setPlayerHealth(int health) {
        if(health > maxHealth)
            setHealth(maxHealth);
        else
            setHealth(Math.max(health, 0));
    }

    public Inventory getInventory() {return inventory;}

    /**
     * Létrehoz egy új játékos karaktert.
     * @param panel a játékmotor példánya
     * @param kezelo a bevitel kezelő példánya
     */
    public Player(Engine panel, InputHandler kezelo) {
        super(panel);
        this.kezelo = kezelo;
        setMaxHealth(100);
        setHealth(100);
        setScreenX(eng.getScreenWidth() / 2 - (eng.getTileSize() / 2));
        setScreenY(eng.getScreenHeight() / 2 - (eng.getTileSize() / 2));
        solidArea = new Rectangle(8, 16, 32, 32);
        inventory = new Inventory(eng);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        getPlayerImage();
        setSpeed(3);
        direction = "down";
    }

    public void setDefaultValues() {
        if(eng.getGameMode().equals(Engine.GameMode.STORY)) {
            setWorldX(eng.getTileSize() * 5);
            setWorldY(eng.getTileSize() * 5);
        }
        else{
            int[][] custom_map = eng.tileman.getMapTileNum();
            if(eng.tileman.getTile(custom_map[eng.getMaxWorldCol()/2][eng.getMaxWorldRow()/2]).collision) {
                int[] coordinates = getNotSolidTile(custom_map);
                assert coordinates != null;
                setWorldX(coordinates[0]);
                setWorldY(coordinates[1]);
            }
            else{
                setWorldX(eng.getMaxWorldCol()/2);
                setWorldY(eng.getMaxWorldRow()/2);
            }
        }
    }

    private int[] getNotSolidTile(int[][] custom_map) {
        int[] coordinates = new int[2];
        for(int x = 1; x < 100; x++){
            for(int y = 1; y < 100; y++){
                if(!(eng.tileman.getTile(custom_map[eng.getMaxWorldCol()/2][eng.getMaxWorldRow()/2]).collision)){
                    coordinates[0] = x;
                    coordinates[1] = y;
                    return coordinates;
                }
            }
        }
        return null;
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
        attack_up = uTool.scaleImage(attack_up, eng.getTileSize(), eng.getTileSize() * 2);
        attack_down = uTool.scaleImage(attack_down, eng.getTileSize(), eng.getTileSize() * 2);
        attack_left = uTool.scaleImage(attack_left, eng.getTileSize() * 2, eng.getTileSize());
        attack_rigth = uTool.scaleImage(attack_rigth, eng.getTileSize() * 2, eng.getTileSize());
    }

    /**
     * Frissíti a játékos állapotát.
     * Kezeli a mozgást, támadást és interakciókat.
     */
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
            eng.cChecker.checkTile(this);

            int objIndex = eng.cChecker.checkObject(this, true);
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
        if (index < eng.aSetter.list.size()) {
            SuperObject obj = eng.aSetter.list.get(index);

            switch (obj.name) {
                case "key", "sword", "boots", "pistol" -> {
                    if (!inventory.isFull()) {
                        inventory.addItem(obj);
                        eng.aSetter.list.remove(obj);
                    }
                }
                case "door" -> {
                    if (eng.aSetter.list.get(index).collision) {
                        if (inventory.equalsKey()) {
                            inventory.removeItem("key");
                            eng.aSetter.list.get(index).collision = false;
                            eng.aSetter.list.get(index).image = eng.aSetter.list.get(index).image2;
                        }
                    }
                }
                case "chest" -> {
                    if (!obj.opened) {
                        eng.aSetter.list.get(index).image = eng.aSetter.list.get(index).image2;
                        eng.aSetter.list.get(index).opened = true;
                        int offsetX = obj.worldX + eng.getTileSize() / 2;
                        int offsetY = obj.worldY + eng.getTileSize() / 2;
                        eng.aSetter.spawnItemFromChest(offsetX, offsetY);
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
        if(weapon instanceof Shooter){
            if(((Shooter) weapon).canShoot())
                attackByShooterWeapon();
        }
        else {
            attackByMeeleWeapon(weapon);
        }
    }

    private int[] determineDirection() {
        if(direction.equalsIgnoreCase("up")){
            return new int[]{-10000,getWorldY()};
        }
        else if(direction.equalsIgnoreCase("down")){
            return new int[]{10000,getWorldY()};
        }
        else if(direction.equalsIgnoreCase("left")){
            return new int[]{getWorldX(),-10000};
        }
        else {
            return new int[]{getWorldX(), 10000};
        }
    }

    private void attackByShooterWeapon(){
        if(!shot) {
            eng.addEntity(new Bullet(eng, "bullet", 40, getWorldX(), getWorldY(), determineDirection()[0], determineDirection()[1]));
            inventory.getCurrent().use();
            isAttacking = true;
            shot = true;
        }
    }

    private void attackByMeeleWeapon(Weapon weapon){
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastAttackTime < ATTACK_COOLDOWN){
            return;
        }
        isAttacking = true;
        hasReducedDurability = false;
        weapon.isActive = true;
        lastAttackTime = currentTime;
        weapon.updateHitbox(getWorldX(), getWorldY(), direction);
        weapon.checkHit(eng.getEntity());
    }

    private int setAdjustedX(){
        int x = getScreenX();
        if(getScreenX() > getWorldX())
            x = getWorldX();
        int rightOffset = eng.getScreenWidth() - getScreenX();
        if (rightOffset > eng.getWorldWidth() - getWorldX())
            x = eng.getScreenWidth() - (eng.getWorldWidth() - getWorldX());
        return x;
    }

    private int setAdjustedY(){
        int y = getScreenY();
        if(getScreenY() > getWorldY())
            y = getWorldY();
        int bottomOffset = eng.getScreenHeight() - getScreenY();
        if (bottomOffset > eng.getWorldHeight() - getWorldY())
            y = eng.getScreenHeight() - (eng.getWorldHeight() - getWorldY());
        return y;
    }

    private void imageDraw(Graphics2D g2, BufferedImage image, int x, int y){
        if(image == attack_up)
            g2.drawImage(image,x,y - eng.getTileSize(),null);
        else if(image == attack_down)
            g2.drawImage(image,x,y,null);
        else if(image == attack_left)
            g2.drawImage(image,x - eng.getTileSize(),y,null);
        else if(image == attack_rigth)
            g2.drawImage(image,x,y,null);
        else
            g2.drawImage(image, x, y, null);
    }

    @Override
    public void draw(Graphics2D g2){
        BufferedImage image = getStateImage();
        int x = setAdjustedX();
        int y = setAdjustedY();
        imageDraw(g2, image, x, y);
        inventory.draw(g2);
    }

}
