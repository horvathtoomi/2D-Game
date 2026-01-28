package map;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

/**
 * A ColorAnalyzer osztály felelős a képek színeinek elemzéséért és a megfelelő csempe típusok
 * meghatározásáért a pályagenerálás során.
 */
public class ColorAnalyzer {

    private ColorAnalyzer() {}

    private static final List<TileColor> TILE_COLORS = Arrays.asList(
            new TileColor(122, 122, 122, 0),  // Light gray, stone wall
            new TileColor(37, 166, 22, 1),    // Light green, grass
            new TileColor(98, 76, 60, 2),     // Brown, earth
            new TileColor(214, 199, 160, 3),  // Sandy beige, sand
            new TileColor(80, 119, 219, 4),   // Blue, water
            new TileColor(16, 11, 35, 5),     // Black, dark wall
            new TileColor(64, 64, 64, 6),     // Dark gray, gravel
            new TileColor(119, 132, 87, 7),   // Brown with sand bg, dead bush
            new TileColor(13, 160, 132, 8),   // Green with sand bg, cactus
            new TileColor(62, 113, 2, 9),     // Green-brown with grass bg, tree
            new TileColor(255, 216, 93, 10),  // Light-gray, gravel
            new TileColor(254,16,2,11)        // Red, lava
    );

    /**
     * Visszaadja a definiált csempeszínek listáját.
     * @return A TileColor objektumok listája.
     */
    public static List<TileColor> getTileColors() {
        return TILE_COLORS;
    }

    /**
     * Kiszámítja két szín közötti távolságot.
     * @param color1 az első szín
     * @param color2 a második szín
     * @return a színek közötti távolság
     */
    public static double calculateColorDistance(Color color1, TileColor color2) {
        double dr = (color1.getRed() - color2.r) / 255.0;
        double dg = (color1.getGreen() - color2.g) / 255.0;
        double db = (color1.getBlue() - color2.b) / 255.0;
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /**
     * Meghatározza a legközelebbi csempe típust egy adott színhez.
     * @param color a vizsgált szín
     * @return a megfelelő csempe típus azonosítója
     */
    public static int getClosestTile(Color color) {
        return TILE_COLORS.stream().min(Comparator.comparingDouble(tile -> calculateColorDistance(color, tile))).map(tile -> tile.tileNumber).orElse(0);
    }

    /**
     * Meghatározza egy képblokk domináns színét.
     * @param block a vizsgált képblokk
     * @return a domináns szín
     */
    public static Color getDominantColor(BufferedImage block) {
        Map<Integer, Integer> colorCount = new HashMap<>();
        for (int y = 0; y < block.getHeight(); y++) {
            for (int x = 0; x < block.getWidth(); x++) {
                int rgb = block.getRGB(x, y);
                colorCount.merge(rgb, 1, Integer::sum);
            }
        }
        int dominantRGB = colorCount.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(0);
        return new Color(dominantRGB);
    }
}