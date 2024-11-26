package tile;

import java.awt.image.BufferedImage;

/**
 * A Tile osztály egy alapvető játékelemet reprezentál.
 * Ez az osztály tartalmazza a tile megjelenítéséhez és viselkedéséhez szükséges alapadatokat.
 */
public class Tile {
    public BufferedImage image;
    public boolean collision = false;
}
