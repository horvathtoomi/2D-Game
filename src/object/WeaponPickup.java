package object;

import main.Engine;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class WeaponPickup extends GameObject implements Pickupable {
    protected WeaponRarity rarity;
    protected float glowOpacity = 0.6f;
    private boolean increasing = true;

    protected WeaponPickup(Engine eng, int x, int y, String imageName) {
        super(eng, x, y, imageName);
        // Determine rarity on spawn
        this.rarity = eng.aSetter.determineWeaponRarity();
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - eng.camera.getX();
        int screenY = worldY - eng.camera.getY();

        // Draw Glow
        drawGlow(g2, screenX, screenY, 2.4f, glowOpacity / 2);
        drawGlow(g2, screenX, screenY, 1.6f, glowOpacity);

        // Draw Item
        super.draw(g2);

        updateGlowAnimation();
    }

    private void drawGlow(Graphics2D g2, int screenX, int screenY, float scale, float opacity) {
        int size = (int) (eng.getTileSize() * scale);
        int offset = (size - eng.getTileSize()) / 2;

        // Safety check for null rarity
        Color c = (rarity != null) ? rarity.color : Color.WHITE;

        Composite originalComposite = g2.getComposite();
        RadialGradientPaint gradient = new RadialGradientPaint(
                new Point2D.Float(screenX + eng.getTileSize() / 2f, screenY + eng.getTileSize() / 2f),
                size / 2f,
                new float[] { 0.0f, 1.0f },
                new Color[] { new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, opacity),
                        new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 0.0f) });

        g2.setPaint(gradient);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2.fillOval(screenX - offset, screenY - offset, size, size);
        g2.setComposite(originalComposite);
    }

    private void updateGlowAnimation() {
        float GLOW_MAX = 0.6f;
        float GLOW_MIN = 0.2f;
        float GLOW_STEP = 0.015f;
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