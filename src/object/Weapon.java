package object;

import entity.Entity;
import entity.npc.NPC_Wayfarer;
import main.GamePanel;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Weapon extends SuperObject {
    protected int damage;
    protected int range;  // Range in tiles
    protected int attackSpeed; // Frames between attacks
    protected int attackDuration = 30; // Duration of attack animation in frames
    protected int attackCooldown = 30;
    private final int ATTACK_COOLDOWN_TIME = 30;
    protected boolean isAttacking = false;
    protected Rectangle hitbox;
    public boolean isActive = false;

    public int getDamage() {return damage;}

    public Weapon(GamePanel gp, int x, int y, String name, String imageName, int damage, int range, int attackSpeed) {
        super(gp, x, y, name, imageName);
        this.damage = damage;
        this.range = range;
        this.attackSpeed = attackSpeed;
    }

    public void updateHitbox(int playerWorldX, int playerWorldY, String direction) {
        switch(direction) {
            case "up" -> hitbox.setBounds(
                    playerWorldX + 8,  // Align with player's hitbox x
                    playerWorldY - gp.getTileSize(),  // One tile above player
                    32,  // Same width as player's hitbox
                    gp.getTileSize()  // One tile height
            );
            case "down" -> hitbox.setBounds(
                    playerWorldX + 8,
                    playerWorldY + gp.getTileSize(),
                    32,
                    gp.getTileSize()
            );
            case "left" -> hitbox.setBounds(
                    playerWorldX - gp.getTileSize(),
                    playerWorldY + 8,
                    gp.getTileSize(),
                    32
            );
            case "right" -> hitbox.setBounds(
                    playerWorldX + gp.getTileSize(),
                    playerWorldY + 8,
                    gp.getTileSize(),
                    32
            );
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
            }
        }
    }

    public void update() {
        if(isAttacking) {
            attackCooldown--;
        }
        if(attackCooldown==0 && isAttacking) {
            attackCooldown = ATTACK_COOLDOWN_TIME;
            attackDuration--;
        }
    }

    @Override
    public void use(){
        if(gp.player.isAttacking)
            setDurability(getDurability() - getUsageDamage());
    }
}