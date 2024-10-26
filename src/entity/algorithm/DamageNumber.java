package entity.algorithm;

import main.GamePanel;

import java.awt.*;

public class DamageNumber{
    private final int x, y;
    private final int damage;
    private int lifetime = 60; // Number stays for 1 second
    private float alpha = 1.0f;
    private final GamePanel gp;

    public DamageNumber(GamePanel gp, int x, int y, int damage) {
        this.gp = gp;
        this.x = x;
        this.y = y;
        this.damage = damage;
    }

    public boolean update() {
        lifetime--;
        alpha = lifetime / 60f;
        return lifetime > 0;
    }

    public void draw(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int screenX = x - gp.player.getWorldX() + gp.player.getScreenX();
        int screenY = y - gp.player.getWorldY() + gp.player.getScreenY();

        // Draw outline
        g2.setColor(Color.BLACK);
        g2.drawString(String.valueOf(damage), screenX - 1, screenY - 1);
        g2.drawString(String.valueOf(damage), screenX + 1, screenY - 1);
        g2.drawString(String.valueOf(damage), screenX - 1, screenY + 1);
        g2.drawString(String.valueOf(damage), screenX + 1, screenY + 1);

        // Draw number
        g2.setColor(Color.RED);
        g2.drawString(String.valueOf(damage), screenX, screenY);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
