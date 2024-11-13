package main;

import javax.swing.*;
import java.awt.*;

public class Button extends JButton {
    private final int x, y, width, height;
    private final String text;
    private Color backgroundColor;
    private final Color textColor;
    private final Font font;

    public Button(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.backgroundColor = new Color(70, 70, 70);
        this.textColor = Color.WHITE;
        this.font = new Font("Arial", Font.BOLD, 20);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(backgroundColor);
        g2.fillRect(x, y, width, height);
        g2.setColor(textColor);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);
    }

    public boolean contains(Point p) {
        return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 50));
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180)); // Light Steel Blue
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

}