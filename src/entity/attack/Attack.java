package entity.attack;

import entity.Entity;
import entity.enemy.Enemy;
import main.Engine;
import tile.TileManager;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Alap Attack osztály, amely a játékban előforduló összes attack ősosztálya.
 * Kezeli a támadások alapvető tulajdonságait, mozgását és ütközésdetektálását.
 */
public class Attack extends Entity {
    int imageChange = 0;
    public BufferedImage image, image1, image2;
    public int damage;
    public double dx, dy;
    private final int[] diffAttackSpeed = {4, 6, 8, 10};

    public Attack(Engine eng,String name,int damage, int startX, int startY, int targetX, int targetY) {
        super(eng);
        setWorldX(startX);
        setWorldY(startY);
        this.name = name;
        double angle = Math.atan2(targetY - startY, targetX - startX);
        initializeDamage(damage);
        initializeSpeed();
        dx = Math.cos(angle) * getSpeed();
        dy = Math.sin(angle) * getSpeed();
        solidArea = new Rectangle(getWorldX() + 3, getWorldY() + 4, 30, 30);
    }

    /**
     * Inicializálja a támadás sebzését a játék nehézségi szintje alapján.
     *
     * @param extra további sebzés érték, ami hozzáadódik az alap sebzéshez
     */
    private void initializeDamage(int extra){
        switch(eng.getGameDifficulty()){
            case EASY -> this.damage = 30 + extra;
            case MEDIUM -> this.damage = 50 + extra;
            case HARD -> this.damage = 99 + extra;
            case IMPOSSIBLE -> this.damage = 200 + extra;
        }
    }

    /**
     * Beállítja a támadás sebességét a játék nehézségi szintje alapján.
     */
    private void initializeSpeed(){
        switch(eng.getGameDifficulty()){
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
            eng.removeEnemy(this);
            return;
        }
        Rectangle playerHitbox = new Rectangle(eng.player.getWorldX() + eng.player.solidArea.x, eng.player.getWorldY() + eng.player.solidArea.y, eng.player.solidArea.width, eng.player.solidArea.height); //Width=32, Height=32
        if (solidArea.intersects(playerHitbox.getBounds())) {
            eng.player.dealDamage(damage);
            eng.removeEnemy(this);
            return;
        }
        for (Entity entity : eng.getEntity()) {
            if (entity instanceof Enemy) {
                Rectangle entityHitbox = new Rectangle(entity.getWorldX() + entity.solidArea.x,
                        entity.getWorldY() + entity.solidArea.y,
                        entity.solidArea.width,
                        entity.solidArea.height);
                if (solidArea.intersects(entityHitbox)) {
                    entity.dealDamage(damage);
                    eng.removeEnemy(this);
                }
            }
        }
        imageChange++;
        if (imageChange > 5) {
            if (image == image1) image = image2;
            else image = image1;
            imageChange = 0;
        }
    }

    protected boolean checkTileCollision() {
        int x = getWorldX()/ eng.getTileSize();
        int y = getWorldY()/ eng.getTileSize();
        if(TileManager.mapTileNum[x][y]==4)
            return false;
        else
            return TileManager.tile[TileManager.mapTileNum[x][y]].collision;
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = getWorldX() - eng.camera.getX();
        int screenY = getWorldY() - eng.camera.getY();
        if (isOnScreen(screenX, screenY)) {
            g2.drawImage(image, screenX, screenY, getWidth(), getHeight(), null);
        }
    }
}