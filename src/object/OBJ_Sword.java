package object;

import main.GamePanel;
import main.logger.GameLogger;

import java.awt.*;
import java.io.IOException;

public class OBJ_Sword extends Weapon {

    private final WeaponRarity rarity;
    private static final int swordUsageDamage = 2;

    public OBJ_Sword(GamePanel gp, int x, int y, int baseDamage) {
        super(gp, x, y, "sword", "sword", baseDamage, 1, 30); // 30 frames cooldown (0.5 seconds at 60 FPS)
        setMaxDurability(200);
        setDurability(getMaxDurability());
        setUsageDamage(swordUsageDamage);
        this.rarity = gp.aSetter.determineWeaponRarity();
        this.damage = (int)(baseDamage * rarity.damageMultiplier);
        // Load attack animation frames
        try {
            image1 = scale("sword1");
            image2 = scale("sword2");
            image = image1;
        } catch (Exception e) {
            GameLogger.error("[OBJ_SWORD]", "ERROR OCCURED WHILE GETTING IMAGE", new IOException(e.getMessage()));
        }
        hitbox = new Rectangle(0,0,32,gp.getTileSize());
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        super.draw(g2, gp);
        if (worldX + gp.getTileSize() > gp.player.getWorldX() - gp.player.getScreenX() &&
                worldX - gp.getTileSize() < gp.player.getWorldX() + gp.player.getScreenX() &&
                worldY + gp.getTileSize() > gp.player.getWorldY() - gp.player.getScreenY() &&
                worldY - gp.getTileSize() < gp.player.getWorldY() + gp.player.getScreenY()) {

            int screenX = worldX - gp.player.getWorldX() + gp.player.getScreenX();
            int screenY = worldY - gp.player.getWorldY() + gp.player.getScreenY();

            // Draw rarity glow
            g2.setColor(new Color(rarity.color.getRed(), rarity.color.getGreen(), rarity.color.getBlue(), 100));
            g2.fillOval(screenX - 5, screenY - 5, gp.getTileSize() + 10, gp.getTileSize() + 10);
        }
    }

}