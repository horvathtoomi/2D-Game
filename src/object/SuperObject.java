package object;

import main.Engine;
import main.logger.GameLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * A SuperObject osztály az összes játékban szereplő tárgy ősosztálya.
 * Alapvető tulajdonságokat és viselkedéseket definiál.
 */
public abstract class SuperObject{
    public Engine eng;
    public BufferedImage image, image1, image2;
    public String name;
    private static final String LOG_CONTEXT = "[SUPER OBJECT]";

    public int worldX, worldY;
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public Rectangle solidArea = new Rectangle(0,0,48,48);

    public int getWorldX(){return worldX;}
    public int getWorldY(){return worldY;}

    public void setWorldX(int x){worldX = x;}
    public void setWorldY(int y){worldY = y;}

    /**
     * Létrehoz egy új tárgyat.
     * @param eng játékmotor példány
     * @param x kezdő X pozíció
     * @param y kezdő Y pozíció
     * @param name tárgy neve
     * @param imageName a tárgy képének neve
     */
    protected SuperObject(Engine eng, int x, int y, String name, String imageName) {
        this.eng = eng;
        this.worldX = x;
        this.worldY = y;
        this.name = name;
        try {
            image1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/" + imageName +".png")));
        } catch(IOException e) {
            GameLogger.error(LOG_CONTEXT, "Failed to get image: " + e.getMessage(), e);
        }
        image = image1;
    }

    /**
     * A tárgy használatát kezelő metódus.
     * Az alosztályok felülírhatják saját viselkedés megvalósításához.
     */
    public void use(){}

    public void draw(Graphics2D g2, Engine eng) {
        int screenX = worldX - eng.camera.getX();
        int screenY = worldY - eng.camera.getY();
        if (screenX + eng.getTileSize() > 0 && screenX < eng.getScreenWidth() && screenY + eng.getTileSize() > 0 && screenY < eng.getScreenHeight()) {
            g2.drawImage(image, screenX, screenY, eng.getTileSize(), eng.getTileSize(), null);
        }
    }

    protected BufferedImage scale(String filename){
        BufferedImage ima = null;
        try {
            ima = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/" + filename +".png")));
        } catch(IOException e) {
            GameLogger.error(LOG_CONTEXT, "Failed to scale image: " + e.getMessage(), e);
        }
        return ima;
    }

    public void update() {}
    public void interact(){}

}