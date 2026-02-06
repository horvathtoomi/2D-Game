package main;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * A Button osztály a játék felhasználói felületének gomb elemeit reprezentálja.
 * Modern megjelenítéssel, animációkkal és interakció kezeléssel rendelkezik.
 */
public class Button {

    private final int x, y, width, height;
    private final String text;

    private final Color baseColor;
    private final Color hoverColor;
    private Color currentColor;

    private boolean hovered = false;
    private float hoverOffset = 0f;
    private float scale = 1.0f;
    private float glowIntensity = 0f;

    private final Font font = new Font("SansSerif", Font.BOLD, 20);
    private final ArrayList<ActionListener> listeners = new ArrayList<>();

    /**
     * Létrehoz egy új gombot a megadott paraméterekkel.
     * 
     * @param x          a gomb X koordinátája
     * @param y          a gomb Y koordinátája
     * @param width      a gomb szélessége
     * @param height     a gomb magassága
     * @param text       a gomb szövege
     * @param baseColor  alap szín
     * @param hoverColor hover szín
     */
    public Button(int x, int y, int width, int height, String text, Color baseColor, Color hoverColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.baseColor = baseColor;
        this.hoverColor = hoverColor;
        this.currentColor = baseColor;
    }

    public void update(Point mouse) {
        hovered = contains(mouse);

        // Smooth color transition
        currentColor = lerp(currentColor, hovered ? hoverColor : baseColor, 0.12f);

        // Smooth hover offset (lift effect)
        hoverOffset = lerp(hoverOffset, hovered ? -4f : 0f, 0.15f);

        // Smooth scale animation
        scale = lerp(scale, hovered ? 1.03f : 1.0f, 0.12f);

        // Glow intensity
        glowIntensity = lerp(glowIntensity, hovered ? 1f : 0f, 0.1f);
    }

    /**
     * Kirajzolja a gombot a megadott grafikus kontextusra.
     * 
     * @param g2 a grafikus kontextus
     */
    public void draw(Graphics2D g2) {
        // Calculate scaled dimensions
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        int drawX = x - (scaledWidth - width) / 2;
        int drawY = (int) (y + hoverOffset) - (scaledHeight - height) / 2;

        // Enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw glow effect (when hovered)
        if (glowIntensity > 0.01f) {
            drawGlow(g2, drawX, drawY, scaledWidth, scaledHeight);
        }

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRoundRect(drawX + 3, drawY + 5, scaledWidth, scaledHeight, 16, 16);

        // Draw button with gradient
        drawGradientButton(g2, drawX, drawY, scaledWidth, scaledHeight);

        // Draw subtle border
        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(drawX, drawY, scaledWidth, scaledHeight, 16, 16);

        // Draw text with shadow
        drawText(g2, drawX, drawY, scaledWidth, scaledHeight);
    }

    private void drawGlow(Graphics2D g2, int drawX, int drawY, int w, int h) {
        int glowSize = 8;
        int alpha = (int) (40 * glowIntensity);

        // Create glow layers
        for (int i = glowSize; i > 0; i -= 2) {
            int layerAlpha = (int) (alpha * (1 - (float) i / glowSize));
            g2.setColor(new Color(
                    currentColor.getRed(),
                    currentColor.getGreen(),
                    currentColor.getBlue(),
                    Math.max(0, Math.min(255, layerAlpha))));
            g2.fillRoundRect(drawX - i, drawY - i, w + i * 2, h + i * 2, 16 + i, 16 + i);
        }
    }

    private void drawGradientButton(Graphics2D g2, int drawX, int drawY, int w, int h) {
        // Create gradient from lighter at top to darker at bottom
        Color topColor = brighten(currentColor, 0.15f);
        Color bottomColor = darken(currentColor, 0.1f);

        GradientPaint gradient = new GradientPaint(
                drawX, drawY, topColor,
                drawX, drawY + h, bottomColor);
        g2.setPaint(gradient);
        g2.fillRoundRect(drawX, drawY, w, h, 16, 16);

        // Add subtle highlight at top
        GradientPaint highlight = new GradientPaint(
                drawX, drawY, new Color(255, 255, 255, 50),
                drawX, drawY + h / 3, new Color(255, 255, 255, 0));
        g2.setPaint(highlight);
        g2.fillRoundRect(drawX, drawY, w, h / 2, 16, 16);
    }

    private void drawText(Graphics2D g2, int drawX, int drawY, int w, int h) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int textX = drawX + (w - fm.stringWidth(text)) / 2;
        int textY = drawY + (h - fm.getHeight()) / 2 + fm.getAscent();

        // Text shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawString(text, textX + 1, textY + 1);

        // Main text
        g2.setColor(Color.WHITE);
        g2.drawString(text, textX, textY);
    }

    /**
     * Ellenőrzi, hogy a megadott pont a gombon belül van-e.
     * 
     * @param p az ellenőrizendő pont
     * @return true ha a pont a gombon belül van
     */
    public boolean contains(Point p) {
        return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
    }

    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    public void doClick() {
        listeners.forEach(l -> l.actionPerformed(null));
    }

    // === Utility Methods ===

    private Color lerp(Color a, Color b, float t) {
        return new Color(
                clamp((int) (a.getRed() + (b.getRed() - a.getRed()) * t), 0, 255),
                clamp((int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t), 0, 255),
                clamp((int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t), 0, 255));
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private Color brighten(Color c, float factor) {
        int r = clamp((int) (c.getRed() + 255 * factor), 0, 255);
        int g = clamp((int) (c.getGreen() + 255 * factor), 0, 255);
        int b = clamp((int) (c.getBlue() + 255 * factor), 0, 255);
        return new Color(r, g, b);
    }

    private Color darken(Color c, float factor) {
        int r = clamp((int) (c.getRed() * (1 - factor)), 0, 255);
        int g = clamp((int) (c.getGreen() * (1 - factor)), 0, 255);
        int b = clamp((int) (c.getBlue() * (1 - factor)), 0, 255);
        return new Color(r, g, b);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}