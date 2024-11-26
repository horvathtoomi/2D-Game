package map;

import main.logger.GameLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A MapGenerator osztály felelős a pályák generálásáért képfájlok alapján.
 */
public class MapGenerator {
    private static final int BLOCK_SIZE = 16;
    private static final int MAX_TILES = 1000;
    private static final String MATRICES_PATH = "res/maps/map_matrices";
    private static final String ANALYSIS_PATH = "res/maps/map_analysis";
    private static final String LOG_CONTEXT = "[MAP GENERATOR]";

    /**
     * Ellenőrzi a bemeneti kép méreteit.
     * @param width szélesség
     * @param height magasság
     * @throws IllegalArgumentException ha a méretek nem megfelelőek
     */
    private static void validateImageDimensions(int width, int height) {
        if (width % BLOCK_SIZE != 0 || height % BLOCK_SIZE != 0) {
            GameLogger.error(LOG_CONTEXT,
                    "Image dimensions must be divisible by 16. Current dimensions: " + width + "x" + height,
                    new IllegalArgumentException());
            throw new IllegalArgumentException("Invalid image dimensions");
        }
        int tilesWidth = width / BLOCK_SIZE;
        int tilesHeight = height / BLOCK_SIZE;
        if (tilesWidth > MAX_TILES || tilesHeight > MAX_TILES) {
            GameLogger.error(LOG_CONTEXT,
                    "Map size exceeds maximum limit of " + MAX_TILES + "x" + MAX_TILES + " tiles",
                    new IllegalArgumentException());
            throw new IllegalArgumentException("Map size too large");
        }
        GameLogger.info(LOG_CONTEXT, "New map dimensions set to: " + tilesWidth + "x" + tilesHeight + " tiles");
    }

    /**
     * Visszaadja a következő pályaszámot.
     * @return a következő pályaszám
     * @throws IOException ha a fájlrendszer nem elérhető
     */
    public static int getNextMapNumber() throws IOException {
        Path matricesDir = Paths.get(MATRICES_PATH);
        if (!Files.exists(matricesDir)) {
            throw new IOException("Map matrices directory does not exist: " + MATRICES_PATH);
        }
        Pattern pattern = Pattern.compile("map(\\d+)\\.txt");
        int maxNumber = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(matricesDir)) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.matches()) {
                    int number = Integer.parseInt(matcher.group(1));
                    maxNumber = Math.max(maxNumber, number);
                }
            }
        }
        return maxNumber + 1;
    }

    /**
     * Feldolgoz egy képfájlt és pályát generál belőle.
     * @param imagePath a képfájl útvonala
     * @throws IOException ha a fájl nem olvasható
     */
    public static void processImage(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        validateImageDimensions(image.getWidth(), image.getHeight());

        int mapNumber = getNextMapNumber();

        Files.createDirectories(Paths.get(MATRICES_PATH));
        Files.createDirectories(Paths.get(ANALYSIS_PATH));

        int numBlocksH = image.getHeight() / BLOCK_SIZE;
        int numBlocksW = image.getWidth() / BLOCK_SIZE;
        int[][] tileMap = new int[numBlocksH][numBlocksW];

        BufferedImage blockAnalysis = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = blockAnalysis.createGraphics();

        for (int row = 0; row < numBlocksH; row++) {
            for (int col = 0; col < numBlocksW; col++) {
                BufferedImage block = image.getSubimage(
                        col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

                Color dominantColor = ColorAnalyzer.getDominantColor(block);
                int tileNumber = ColorAnalyzer.getClosestTile(dominantColor);
                tileMap[row][col] = tileNumber;

                g2d.setColor(dominantColor);
                g2d.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            }
        }
        g2d.dispose();

        Path analysisPath = Paths.get(ANALYSIS_PATH, "map_analysis_" + mapNumber + ".png");
        Path matrixPath = Paths.get(MATRICES_PATH, "map" + mapNumber + ".txt");

        ImageIO.write(blockAnalysis, "PNG", analysisPath.toFile());
        ResultWriter.writeTileMap(tileMap, matrixPath);

        GameLogger.info(LOG_CONTEXT, "Map generation complete!");
        GameLogger.info(LOG_CONTEXT, "Files generated:");
        GameLogger.info(LOG_CONTEXT, "- " + matrixPath + ": Contains the tile numbers");
        GameLogger.info(LOG_CONTEXT, "- " + analysisPath + ": Visual analysis of the conversion process");
    }

    public static void GUIMapGenerator(){
        GUIMapGenerator mapGenerator = new GUIMapGenerator();
        mapGenerator.setVisible(true);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameLogger.info(LOG_CONTEXT, "Type 'GUI' to open graphical UI");
        GameLogger.info(LOG_CONTEXT, "Type 'exit' to close application");
        GameLogger.info(LOG_CONTEXT, "Enter the path to your PNG image: ");
        String imagePath;
        while((imagePath = scanner.nextLine()) != null) {
            if (imagePath.equalsIgnoreCase("GUI")) {
                GUIMapGenerator();
            } else if (imagePath.equalsIgnoreCase("exit")) {
                System.exit(0);
            } else {
                try {
                    processImage(imagePath);
                } catch (Exception e) {
                    GameLogger.warn(LOG_CONTEXT, "File not found!");
                }
            }
        }
    }

}