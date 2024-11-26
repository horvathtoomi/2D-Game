package main;

import javax.swing.*;
import java.awt.*;

/**
 * A Button osztály a játék felhasználói felületének gomb elemeit reprezentálja.
 * Egyéni megjelenítéssel és interakció kezeléssel rendelkezik.
 */
public class Button extends JButton {
    private final int x, y, width, height;
    private final String text;
    private Color backgroundColor;
    private final Color textColor;
    private final Font font;

    /**
     * Létrehoz egy új gombot a megadott paraméterekkel.
     * @param x a gomb X koordinátája
     * @param y a gomb Y koordinátája
     * @param width a gomb szélessége
     * @param height a gomb magassága
     * @param text a gomb szövege
     */
    public Button(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.backgroundColor = new Color(70, 130, 180); // Light Steel Blue
        this.textColor = Color.WHITE;
        this.font = new Font("Arial", Font.BOLD, 20);
    }

    /**
     * Kirajzolja a gombot a megadott grafikus kontextusra.
     * @param g2 a grafikus kontextus
     */
    public void draw(Graphics2D g2) {
        g2.setColor(backgroundColor);
        g2.fillRect(x, y, width, height);

        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, width, height);

        g2.setColor(textColor);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);
    }

    /**
     * Ellenőrzi, hogy a megadott pont a gombon belül van-e.
     * @param p az ellenőrizendő pont
     * @return true ha a pont a gombon belül van
     */
    public boolean contains(Point p) {
        return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
    }

    /**
     * Beállítja a gomb háttérszínét.
     * @param color az új háttérszín
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
}