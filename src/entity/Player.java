package entity;

import entity.attack.Bullet;
import main.Engine;
import main.GameMode;
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
    public String name;
    public static boolean isAttacking = false;
    private boolean hasReducedDurability = false;
    private long lastAttackTime = 0;
    private final InputHandler inputHandler;
    private final Inventory inventory;
    private static final long ATTACK_COOLDOWN = 500;
    private static final int INTERACTION_COOLDOWN = 30;
    private int interactionTimer = 0;
    public int defeatedEnemies = 0;
    public static boolean shot = false;
    private BufferedImage up_key, up_boots, up_sword,
            down_key, down_boots, down_sword,
            left_key, left_boots, left_sword,
            right_key, right_boots, right_sword,
            up_rifle, down_rifle, right_rifle, left_rifle,
            attack_up, attack_down, attack_left, attack_right,
            up_pistol, down_pistol, left_pistol, right_pistol,
            attack_up_pistol, attack_down_pistol, attack_left_pistol, attack_right_pistol,
            attack_up_rifle, attack_down_rifle, attack_left_rifle, attack_right_rifle;

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
        this.inputHandler = kezelo;
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
        direction = Direction.DOWN;
    }

    public void setDefaultValues() {
        if(eng.getGameMode().equals(GameMode.STORY)) {
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
        int maxWorldCol = eng.getMaxWorldCol();
        int maxWorldRow = eng.getMaxWorldRow();
        for(int x = 1; x < maxWorldRow; x++){
            for(int y = 1; y < maxWorldCol; y++){
                if(!(eng.tileman.getTile(custom_map[x][y]).collision)){
                    coordinates[0] = x;
                    coordinates[1] = y;
                    return coordinates;
                }
            }
        }
        return null;
    }

    public void getPlayerImage() {
        right = scale("player", "right");
        right_key = scale("player", "right_key");
        right_boots = scale("player", "right_boots");
        right_sword = scale("player", "right_sword");
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
        attack_right = scale("player", "attack_right");

        up_rifle = scale("player", "up_rifle");
        down_rifle = scale("player", "down_rifle");
        left_rifle = scale("player", "left_rifle");
        right_rifle = scale("player", "right_rifle");

        up_pistol = scale("player", "up_pistol");
        down_pistol = scale("player", "down_pistol");
        left_pistol = scale("player", "left_pistol");
        right_pistol = scale("player", "right_pistol");
        attack_up_pistol = scale("player", "attack_up_pistol");
        attack_down_pistol = scale("player", "attack_down_pistol");
        attack_left_pistol = scale("player", "attack_left_pistol");
        attack_right_pistol = scale("player", "attack_right_pistol");

        attack_up_rifle = scale("player", "attack_up_rifle");
        attack_down_rifle = scale("player", "attack_down_rifle");
        attack_left_rifle = scale("player", "attack_left_rifle");
        attack_right_rifle = scale("player", "attack_right_rifle");


        UtilityTool uTool = new UtilityTool();
        attack_up = uTool.scaleImage(attack_up, eng.getTileSize(), eng.getTileSize() * 2);
        attack_down = uTool.scaleImage(attack_down, eng.getTileSize(), eng.getTileSize() * 2);
        attack_left = uTool.scaleImage(attack_left, eng.getTileSize() * 2, eng.getTileSize());
        attack_right = uTool.scaleImage(attack_right, eng.getTileSize() * 2, eng.getTileSize());
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
        if(inputHandler.attackPressed){
            attack();
        }

        if (!inputHandler.attackPressed && isAttacking) {
            isAttacking = false;
            hasReducedDurability = false;
            if (inventory.getCurrent() instanceof Weapon) {
                ((Weapon) inventory.getCurrent()).isActive = false;
            }
        }

        if (inputHandler.upPressed || inputHandler.downPressed || inputHandler.leftPressed || inputHandler.rightPressed) {
            if (inputHandler.upPressed) direction = Direction.UP;
            if (inputHandler.downPressed) direction = Direction.DOWN;
            if (inputHandler.leftPressed) direction = Direction.LEFT;
            if (inputHandler.rightPressed) direction = Direction.RIGHT;

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
                    case UP -> setWorldY(getWorldY() - getSpeed());
                    case DOWN -> setWorldY(getWorldY() + getSpeed());
                    case LEFT -> setWorldX(getWorldX() - getSpeed());
                    case RIGHT -> setWorldX(getWorldX() + getSpeed());
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
                case "key", "sword", "boots", "pistol", "rifle" -> {
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
                image = Direction.valueMapper(new BufferedImage[]{up_key, down_key, left_key, right_key}, direction);
            }
            else if(inventory.getCurrent() instanceof OBJ_Boots) {
                image = Direction.valueMapper(new BufferedImage[]{up_boots, down_boots, left_boots, right_boots}, direction);
            }
            else if(inventory.getCurrent() instanceof Rifle){
                image = !isAttacking ?
                        Direction.valueMapper(new BufferedImage[]{up_rifle, down_rifle, left_rifle, right_rifle}, direction)
                        : Direction.valueMapper(new BufferedImage[]{attack_up_rifle, attack_down_rifle, attack_left_rifle, attack_right_rifle}, direction);
            }
            else if(inventory.getCurrent() instanceof OBJ_Sword) {
                image = !isAttacking ?
                        Direction.valueMapper(new BufferedImage[]{up_sword, down_sword, left_sword, right_sword}, direction)
                        : Direction.valueMapper(new BufferedImage[]{attack_up, attack_down, attack_left, attack_right}, direction);
            }
            else if (inventory.getCurrent() instanceof Pistol) {
                image = !isAttacking ?
                        Direction.valueMapper(new BufferedImage[]{up_pistol, down_pistol, left_pistol, right_pistol}, direction)
                        : Direction.valueMapper(new BufferedImage[]{attack_up_pistol, attack_down_pistol, attack_left_pistol, attack_right_pistol}, direction);
            }
        } else {
            image = Direction.valueMapper(new BufferedImage[]{up, down, left, right}, direction);
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
        if(direction == Direction.UP){
            return new int[]{-10000,getWorldY()};
        }
        else if(direction == Direction.DOWN){
            return new int[]{10000,getWorldY()};
        }
        else if(direction == Direction.LEFT){
            return new int[]{getWorldX(),-10000};
        }
        else {
            return new int[]{getWorldX(), 10000};
        }
    }

    private void attackByShooterWeapon(){
        if(inventory.getCurrent() instanceof Pistol) {
            if(!shot) {
                eng.addEntity(new Bullet(eng, "bullet", 40, getWorldX(), getWorldY(), determineDirection()[0], determineDirection()[1]));
                inventory.getCurrent().use();
                isAttacking = true;
                shot = true;
            }
        } else {
            Rifle rifle = (Rifle)inventory.getCurrent();
            if(rifle.getCoolDown() == 0) {
                eng.addEntity(new Bullet(eng, "bullet", 40, getWorldX(), getWorldY(), determineDirection()[0], determineDirection()[1]));
                inventory.getCurrent().use();
                isAttacking = true;
                ((Shooter)inventory.getCurrent()).setCoolDown(rifle.getFireRate());
            } else {
                ((Shooter)inventory.getCurrent()).setCoolDown(rifle.getCoolDown() - 1);
            }
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
        weapon.updateHitbox(getWorldX(), getWorldY(), direction.toString());
        weapon.checkHit(eng.getEntity());
    }

    private void imageDraw(Graphics2D g2, BufferedImage image, int x, int y){
        if(image == attack_up)
            g2.drawImage(image,x,y - eng.getTileSize(),null);
        else if(image == attack_down)
            g2.drawImage(image,x,y,null);
        else if(image == attack_left)
            g2.drawImage(image,x - eng.getTileSize(),y,null);
        else if(image == attack_right)
            g2.drawImage(image,x,y,null);
        else
            g2.drawImage(image, x, y, null);
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = getStateImage();
        int screenX = getWorldX() - eng.camera.getX();
        int screenY = getWorldY() - eng.camera.getY();
        imageDraw(g2, image, screenX, screenY);
        inventory.draw(g2);
    }


}
