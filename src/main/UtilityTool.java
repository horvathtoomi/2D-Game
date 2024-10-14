package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class UtilityTool {

    public BufferedImage scaleImage(BufferedImage img, int newWidth, int newHeight) {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, 2);
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(img,0,0,newWidth,newHeight,null);
        g2.dispose();
        return scaledImage;
    }

}
