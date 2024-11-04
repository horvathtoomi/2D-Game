package entity.attack;

import entity.Entity;
import entity.enemy.Enemy;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Attack extends Entity {
    int imageChange = 0;
    public BufferedImage image, image1, image2;
    public int damage;
    public double dx, dy;
    private final int[] diffAttackSpeed = {4, 6, 8, 10};

    public Attack(GamePanel gp,String name,int damage, int startX, int startY, int targetX, int targetY) {
        super(gp);
        setWorldX(startX);
        setWorldY(startY);
        this.name = name;
        double angle = Math.atan2(targetY - startY, targetX - startX);
        initializeDamage(damage);
        initializeSpeed();
        dx = Math.cos(angle) * getSpeed();
        dy = Math.sin(angle) * getSpeed();
        image1 = scale("objects",name+"1");
        image2 = scale("objects",name+"2");
        image = image1;
        solidArea = new Rectangle(getWorldX() + 3, getWorldY() + 4, 30, 30);
    }

    private void initializeDamage(int extra){
        switch(gp.getGameDifficulty()){
            case EASY -> this.damage = 30 + extra;
            case MEDIUM -> this.damage = 50 + extra;
            case HARD -> this.damage = 99 + extra;
            case IMPOSSIBLE -> this.damage = 200 + extra;
        }
    }

    private void initializeSpeed(){
        switch(gp.getGameDifficulty()){
            case EASY -> setSpeed(diffAttackSpeed[0]);
            case MEDIUM -> setSpeed(diffAttackSpeed[1]);
            case HARD -> setSpeed(diffAttackSpeed[2]);
            case IMPOSSIBLE -> setSpeed(diffAttackSpeed[3]);
        }
    }

    @Override
    public void update() {
        setWorldX(getWorldX() + (int) dx);
        setWorldY(getWorldY() + (int) dy);
        solidArea.setLocation(getWorldX() + 3, getWorldY() + 4);

        if (checkTileCollision()) {
            gp.removeEnemy(this);
            return;
        }

        Rectangle playerHitbox = new Rectangle(gp.player.getWorldX() + gp.player.solidArea.x, gp.player.getWorldY() + gp.player.solidArea.y, gp.player.solidArea.width, gp.player.solidArea.height); //Width=32, Height=32
        if (solidArea.intersects(playerHitbox.getBounds())) {
            gp.player.setHealth(Math.max(0,gp.player.getHealth()-damage));
            gp.removeEnemy(this);
            return;
        }

        for (Entity entity : gp.getEntity()) {
            if (entity instanceof Enemy) {
                Rectangle entityHitbox = new Rectangle(entity.getWorldX() + entity.solidArea.x,
                        entity.getWorldY() + entity.solidArea.y,
                        entity.solidArea.width,
                        entity.solidArea.height);
                if (solidArea.intersects(entityHitbox)) {
                    entity.setHealth(Math.max(0,entity.getHealth()-damage));
                    gp.removeEnemy(this);
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
        setScreenX(getWorldX() - gp.player.getWorldX() + gp.player.getScreenX());
        setScreenY(getWorldY() - gp.player.getWorldY() + gp.player.getScreenY());
        setScreenX(adjustScreenX(getScreenX()));
        setScreenY(adjustScreenY(getScreenY()));
        if (isValidScreenXY(getScreenX(), getScreenY())) {
            g2.drawImage(image, getScreenX(), getScreenY(), getWidth(), getHeight(), null);
        }
    }

}
