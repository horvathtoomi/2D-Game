package object;

import entity.Entity;
import entity.Player;
import entity.npc.NPC_Wayfarer;
import main.Engine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A fegyver absztrakt ősosztálya.
 */
public abstract class Weapon extends SuperObject {
    protected int damage;
    protected int range;
    protected int attackSpeed;
    protected int attackDuration = 30;
    protected int attackCooldown = 3000;
    protected boolean isAttacking = false;
    protected Rectangle hitbox;
    public boolean isActive = false;
    protected final WeaponRarity rarity;
    private float glowOpacity = 0.6f;
    private boolean increasing = true;

    private static final float GLOW_MIN = 0.2f;
    private static final float GLOW_MAX = 0.6f;
    private static final float GLOW_STEP = 0.015f;
    private static final float INNER_GLOW_SCALE = 1.6f;
    private static final float OUTER_GLOW_SCALE = 2.4f;
    public int getDamage() {return damage;}
    public void setDamage(int damage) {this.damage = damage;}

    /**
     * Létrehoz egy új fegyvert.
     * @param eng játékmotor példány
     * @param x kezdő X pozíció
     * @param y kezdő Y pozíció
     * @param name fegyver neve
     * @param imageName a fegyver képének neve
     * @param damage alap sebzés
     * @param range hatótáv
     * @param attackSpeed támadási sebesség
     */
    public Weapon(Engine eng, int x, int y, String name, String imageName, int damage, int range, int attackSpeed) {
        super(eng, x, y, name, imageName);
        this.rarity = eng.aSetter.determineWeaponRarity();
        this.damage = damage;
        this.range = range;
        this.attackSpeed = attackSpeed;
    }

    public void updateHitbox(int playerWorldX, int playerWorldY, String direction) {
        switch(direction) {
            case "up" -> hitbox.setBounds(playerWorldX + 8, playerWorldY - eng.getTileSize(), 32, eng.getTileSize());
            case "down" -> hitbox.setBounds(playerWorldX + 8, playerWorldY + eng.getTileSize(), 32, eng.getTileSize());
            case "left" -> hitbox.setBounds(playerWorldX - eng.getTileSize(), playerWorldY + 8, eng.getTileSize(), 32);
            case "right" -> hitbox.setBounds(playerWorldX + eng.getTileSize(), playerWorldY + 8, eng.getTileSize(), 32);
        }
    }

    public void checkHit(CopyOnWriteArrayList<Entity> entities) {
        if (!isActive) return;
        for (Entity entity : entities) {
            if (entity instanceof NPC_Wayfarer) continue;
            Rectangle entityHitbox = new Rectangle(
                    entity.getWorldX() + entity.solidArea.x,
                    entity.getWorldY() + entity.solidArea.y,
                    entity.solidArea.width,
                    entity.solidArea.height
            );
            if (hitbox.intersects(entityHitbox)) {
                entity.setHealth(entity.getHealth() - getDamage());
                if(entity.getHealth() <= 0)
                    eng.player.defeatedEnemies++;
            }
        }
    }

    @Override
    public void update() {
        if(isAttacking) {
            attackCooldown--;
        }
        if(attackCooldown == 0 && isAttacking) {
            attackCooldown = 3000;
            attackDuration--;
        }
    }

    @Override
    public void use(){
        if(Player.isAttacking) {
            setDurability(Math.max(getDurability()-getUsageDamage(), 0));
        }
    }

    @Override
    public void draw(Graphics2D g2, Engine eng) {
        int screenX = worldX - eng.player.getWorldX() + eng.player.getScreenX();
        int screenY = worldY - eng.player.getWorldY() + eng.player.getScreenY();

        if (eng.player.getScreenX() > eng.player.getWorldX()) {
            screenX = worldX;
        }
        if (eng.player.getScreenY() > eng.player.getWorldY()) {
            screenY = worldY;
        }
        int rightOffset = eng.getScreenWidth() - eng.player.getScreenX();
        if (rightOffset > eng.getWorldWidth() - eng.player.getWorldX()) {
            screenX = eng.getScreenWidth() - (eng.getWorldWidth() - worldX);
        }
        int bottomOffset = eng.getScreenHeight() - eng.player.getScreenY();
        if (bottomOffset > eng.getWorldHeight() - eng.player.getWorldY()) {
            screenY = eng.getScreenHeight() - (eng.getWorldHeight() - worldY);
        }

        if (worldX + eng.getTileSize() > eng.player.getWorldX() - eng.player.getScreenX() &&
                worldX - eng.getTileSize() < eng.player.getWorldX() + eng.player.getScreenX() &&
                worldY + eng.getTileSize() > eng.player.getWorldY() - eng.player.getScreenY() &&
                worldY - eng.getTileSize() < eng.player.getWorldY() + eng.player.getScreenY()) {

            drawGlow(g2, screenX, screenY, OUTER_GLOW_SCALE, glowOpacity / 2);
            drawGlow(g2, screenX, screenY, INNER_GLOW_SCALE, glowOpacity);
            g2.drawImage(image, screenX, screenY, eng.getTileSize(), eng.getTileSize(), null);

            updateGlowAnimation();
        }
    }

    private void drawGlow(Graphics2D g2, int screenX, int screenY, float scale, float opacity) {
        int size = (int)(eng.getTileSize() * scale);
        int offset = (size - eng.getTileSize()) / 2;

        Composite originalComposite = g2.getComposite();

        RadialGradientPaint gradient = new RadialGradientPaint(
                new Point2D.Float(screenX + eng.getTileSize()/2f, screenY + eng.getTileSize()/2f),
                size/2f,
                new float[]{0.0f, 1.0f},
                new Color[]{
                        new Color(rarity.color.getRed()/255f, rarity.color.getGreen()/255f, rarity.color.getBlue()/255f, opacity),
                        new Color(rarity.color.getRed()/255f, rarity.color.getGreen()/255f, rarity.color.getBlue()/255f, 0.0f)
                }
        );

        g2.setPaint(gradient);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        g2.fillOval(screenX - offset, screenY - offset, size, size);
        g2.setComposite(originalComposite);
    }

    private void updateGlowAnimation() {
        if (increasing) {
            glowOpacity += GLOW_STEP;
            if (glowOpacity >= GLOW_MAX) {
                glowOpacity = GLOW_MAX;
                increasing = false;
            }
        } else {
            glowOpacity -= GLOW_STEP;
            if (glowOpacity <= GLOW_MIN) {
                glowOpacity = GLOW_MIN;
                increasing = true;
            }
        }
    }
}