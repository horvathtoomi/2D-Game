package main;

import main.logger.GameLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * A UtilityTool osztály segédfunkciókat biztosít a játék számára.
 * Főként képmanipulációs műveleteket tartalmaz.
 */
public class UtilityTool {
    private static final String LOG_CONTEXT = "[UTILITY TOOL]";

    /**
     * Átméretez egy képet a megadott méretekre.
     * @param img az eredeti kép
     * @param newWidth az új szélesség
     * @param newHeight az új magasság
     * @return az átméretezett kép
     */
    public BufferedImage scaleImage(BufferedImage img, int newWidth, int newHeight) {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, 2);
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(img,0,0,newWidth,newHeight,null);
        g2.dispose();
        return scaledImage;
    }

    public BufferedImage getImage(String foldername, String filename) {
        BufferedImage bufim = null;
        try {
            var inputStream = getClass().getClassLoader().getResourceAsStream(foldername + "/" + filename + ".png");
            if (inputStream == null) {
                GameLogger.error(LOG_CONTEXT, "resource inputStream is null", new IOException());
                return null;
            }
            bufim = ImageIO.read(inputStream);
        } catch (IOException e) {
            GameLogger.error(LOG_CONTEXT, "Error occurred while loading image from " + foldername + "/" + filename + ".png", e);
            return null;
        }
        return bufim;
    }

}