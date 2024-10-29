package entity;

import entity.enemy.Enemy;
import main.InputHandler;
import main.GamePanel;
import object.SuperObject;
import object.Weapon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity {

    private final InputHandler kezelo;
    private final Inventory inventory;
    private static final int INTERACTION_COOLDOWN = 20; // frames
    private int interactionTimer = 0;
    ///////////////////////////
    private Weapon equippedWeapon;
    private java.util.ArrayList<Weapon> weapons;
    private int selectedWeaponIndex = 0;
    BufferedImage up_attack, down_attack, left_attack, right_attack;
    ///////////////////////////

    public Inventory getInventory() {return inventory;}

    public Player(GamePanel panel, InputHandler kezelo) {
        super(panel);
        this.kezelo = kezelo;
        setMaxHealth(100);
        setHealth(100);
        setScreenX(gp.getScreenWidth()/2 - (gp.getTileSize()/2));
        setScreenY(gp.getScreenHeight()/2 - (gp.getTileSize()/2));
        solidArea = new Rectangle(8,16,32,32);
        inventory = new Inventory();
        ////////////////////////
        weapons = new ArrayList<>();
        ////////////////////////
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){
        setWorldX(gp.getTileSize() * 23);
        setWorldY(gp.getTileSize() * 21);
        setSpeed(3);
        direction = "down";
    }

    public void getPlayerImage(){
        right = scale("player","right");
        left = scale("player","left");
        down = scale("player","down");
        up = scale("player","up");
    }

   // @Override
    public void update(){
        if(interactionTimer > 0){
            interactionTimer--;
        }
        if(kezelo.upPressed||kezelo.downPressed||kezelo.leftPressed||kezelo.rightPressed) {
            if (kezelo.upPressed) direction = "up";
            if (kezelo.downPressed) direction = "down";
            if (kezelo.leftPressed) direction = "left";
            if (kezelo.rightPressed) direction = "right";

            //Check Tile Collision
            collisionOn = false;
            gp.cChecker.checkTile(this);

            //Check Object Colllision
            int objIndex = gp.cChecker.checkObject(this, true);
            if (objIndex != 999 && interactionTimer == 0) {
                interactWithObject(objIndex);
                interactionTimer = INTERACTION_COOLDOWN;
            }

            //Check npc collision
            int npcIndex = gp.cChecker.checkEntity(this,gp.entities);
            interractNPC(npcIndex);

            if (!collisionOn) {
                switch (direction) {
                    case "up" -> setWorldY(getWorldY()-getSpeed());
                    case "down"-> setWorldY(getWorldY()+getSpeed());
                    case "left" -> setWorldX(getWorldX()-getSpeed());
                    case "right" -> setWorldX(getWorldX()+getSpeed());
                }
            }
        }
    }


    private void interactWithObject(int index) {
        if (index < gp.aSetter.list.size()) {
            SuperObject obj = gp.aSetter.list.get(index);

            switch (obj.name) {
                case "key", "sword" -> {
                    if (!inventory.isFull()) {
                        inventory.addItem(obj);
                        gp.aSetter.list.remove(obj);
                    }
                }
                case "door" -> {
                    if(gp.aSetter.list.get(index).collision) {
                        if (inventory.hasItem("key")) {
                            inventory.removeItem("key");
                            gp.aSetter.list.get(index).collision = false;
                            gp.aSetter.list.get(index).image = gp.aSetter.list.get(index).image2;
                        }
                    }
                }
                case "chest" -> {
                    if (!obj.opened) {
                        gp.aSetter.list.get(index).image=gp.aSetter.list.get(index).image2;
                        gp.aSetter.list.get(index).opened = true;
                        int offsetX = obj.worldX + gp.getTileSize()/2;
                        int offsetY = obj.worldY + gp.getTileSize()/2;
                        gp.aSetter.spawnItemFromChest(offsetX, offsetY);
                    }
                }
                case "boots" -> {
                    if (!inventory.isFull()) {
                        inventory.addItem(obj);
                        gp.aSetter.list.remove(obj);
                        if(!inventory.hasItem("boots"))
                            setSpeed(getSpeed() + 1); // Boots increase speed
                    }
                }
            }
        }
    }
//////////////////////////////////////////////////////////////////////////////////
    public void equipWeapon(Weapon weapon) {
        if (weapons.size() < inventory.getMaxSize()) {
            weapons.add(weapon);
            if (equippedWeapon == null) {
                equippedWeapon = weapon;
            }
        }
    }

    public void switchWeapon() {
        if (weapons.size() > 0) {
            selectedWeaponIndex = (selectedWeaponIndex + 1) % weapons.size();
            equippedWeapon = weapons.get(selectedWeaponIndex);
        }
    }

    public void attack() {
        if (equippedWeapon != null && equippedWeapon.canAttack()) {
            equippedWeapon.startAttack();

            // Get attack area based on player direction and weapon range
            Rectangle attackArea = getAttackArea();

            // Check for enemies in range
            for (Entity entity : gp.entities) {
                if (entity instanceof Enemy) {
                    Rectangle enemyHitbox = new Rectangle(
                            entity.getWorldX() + entity.solidArea.x,
                            entity.getWorldY() + entity.solidArea.y,
                            entity.solidArea.width,
                            entity.solidArea.height
                    );

                    if (attackArea.intersects(enemyHitbox)) {
                        // Deal damage
                        int damage = equippedWeapon.getDamage();
                        entity.setHealth(entity.getHealth() - damage);

                        // Create damage number effect
                        gp.addDamageNumber(entity.getWorldX(), entity.getWorldY(), damage);
                    }
                }
            }
        }
    }

    private Rectangle getAttackArea() {
        int areaX = getWorldX();
        int areaY = getWorldY();
        int tileSize = gp.getTileSize();
        int range = equippedWeapon.getRange() * tileSize;

        switch (direction) {
            case "up":
                areaY -= range;
                return new Rectangle(areaX, areaY, tileSize, range);
            case "down":
                return new Rectangle(areaX, areaY + tileSize, tileSize, range);
            case "left":
                areaX -= range;
                return new Rectangle(areaX, areaY, range, tileSize);
            case "right":
                return new Rectangle(areaX + tileSize, areaY, range, tileSize);
            default:
                return new Rectangle(areaX, areaY, tileSize, tileSize);
        }
    }
//////////////////////////////////////////////////////////////////////////////////

    public void interractNPC(int idx){
        //if(idx!=999){
           //System.out.println("interaction w an NPC!");
        //}
    }

    //@Override
    public void draw(Graphics2D g2){
        BufferedImage image;
        if (equippedWeapon != null) {
            // Use attack animation frame
            image = switch (direction) {
                case "up" -> up_attack;
                case "down" -> down_attack;
                case "left" -> left_attack;
                case "right" -> right_attack;
                default -> null;
            };
        } else {
            // Use normal movement frame
            image = switch (direction) {
                case "up" -> up;
                case "down" -> down;
                case "left" -> left;
                case "right" -> right;
                default -> null;
            };
        }
        int x = getScreenX();
        int y = getScreenY();
        if(getScreenX() > getWorldX()){
            x = getWorldX();
        }
        if(getScreenY() > getWorldY()){
            y = getWorldY();
        }
        int rightOffset = gp.getScreenWidth() - getScreenX();
        if (rightOffset > gp.getWorldWidth() - getWorldX()) {
            x = gp.getScreenWidth() - (gp.getWorldWidth() - getWorldX());
        }
        int bottomOffset = gp.getScreenHeight() - getScreenY();
        if (bottomOffset > gp.getWorldHeight() - getWorldY()) {
            y = gp.getScreenHeight() - (gp.getWorldHeight() - getWorldY());
        }
        g2.drawImage(image,x,y,null);
        inventory.draw(g2);
    }

}
