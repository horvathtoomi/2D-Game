package entity.attack;

import entity.Entity;
import entity.enemy.Enemy;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Attack extends Entity {
    int imageChange = 0;
    public BufferedImage image, image1, image2;
    public final int damage;
    public double dx, dy;

    public Attack(GamePanel gp,String name,int damage, int startX, int startY, int targetX, int targetY) {
        super(gp);
        setWorldX(startX);
        setWorldY(startY);
        this.name = name;
        this.damage = damage;
        // Calculate direction
        double angle = Math.atan2(targetY - startY, targetX - startX);
        double speed = 6;
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;
        image1 = scale("objects",name+"1");
        image2 = scale("objects",name+"2");
        image = image1;
        solidArea = new Rectangle(getWorldX() + 3, getWorldY() + 4, 30, 30);
    }

    @Override
    public void update() {
        setWorldX(getWorldX() + (int) dx);
        setWorldY(getWorldY() + (int) dy);
        solidArea.setLocation(getWorldX() + 3, getWorldY() + 4);

        if (checkTileCollision()) {
            gp.entities.remove(this);
            //gp.entities.set(gp.entities.indexOf(this), null);
            return;
        }

        Rectangle playerHitbox = new Rectangle(gp.player.getWorldX() + gp.player.solidArea.x, gp.player.getWorldY() + gp.player.solidArea.y, gp.player.solidArea.width, gp.player.solidArea.height); //Width=32, Height=32
        if (solidArea.intersects(playerHitbox.getBounds())) {
            gp.player.setHealth(gp.player.getHealth() - damage);
            gp.entities.remove(this);
            return;
        }

        for (Entity entity : gp.entities) {
            if (entity instanceof Enemy) {
                Rectangle entityHitbox = new Rectangle(entity.getWorldX() + entity.solidArea.x,
                        entity.getWorldY() + entity.solidArea.y,
                        entity.solidArea.width,
                        entity.solidArea.height);
                if (solidArea.intersects(entityHitbox)) {
                    entity.setHealth(entity.getHealth() - damage);
                    gp.entities.remove(this);
                }
            }
        }
        imageChange++;
        if (imageChange > 5) {
            if (image == image1)
                image = image2;
            else
                image = image1;
            imageChange = 0;
        }
    }

    private boolean checkTileCollision() {
        int x = getWorldX()/ gp.getTileSize();
        int y = getWorldY()/ gp.getTileSize();
        if(gp.tileman.mapTileNum[x][y]==4)
            return false;
        else
            return gp.tileman.tile[gp.tileman.mapTileNum[x][y]].collision;
    }


    @Override
    public void draw(Graphics2D g2) {
        if (getWorldX() + gp.getTileSize() > gp.player.getWorldX() - gp.player.getScreenX() && getWorldX() - gp.getTileSize() < gp.player.getWorldX() + gp.player.getScreenX() && getWorldY() + gp.getTileSize() > gp.player.getWorldY() - gp.player.getScreenY() && getWorldY() - gp.getTileSize() < gp.player.getWorldY() + gp.player.getScreenY())
            g2.drawImage(image, getWorldX() - gp.player.getWorldX() + gp.player.getScreenX(), getWorldY() - gp.player.getWorldY() + gp.player.getScreenY(), gp.getTileSize(), gp.getTileSize(), null);
    }

}
