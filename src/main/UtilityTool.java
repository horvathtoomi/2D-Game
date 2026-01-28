package main;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A UtilityTool osztály segédfunkciókat biztosít a játék számára.
 * Főként képmanipulációs műveleteket tartalmaz.
 */
public class UtilityTool {
    private static final String LOG_CONTEXT = "[UTILITY TOOL]";

    /**
     * Átméretez egy képet a megadott méretekre.
     * 
     * @param img       az eredeti kép
     * @param newWidth  az új szélesség
     * @param newHeight az új magasság
     * @return az átméretezett kép
     */
    public BufferedImage scaleImage(BufferedImage img, int newWidth, int newHeight) {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, 2);
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(img, 0, 0, newWidth, newHeight, null);
        g2.dispose();
        return scaledImage;
    }

}