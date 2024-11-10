package map;

import main.logger.GameLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TileColor {
    public final int r;
    public final int g;
    public final int b;
    public final int tileNumber;

    public TileColor(int r, int g, int b, int tileNumber) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.tileNumber = tileNumber;
    }
}

class ColorAnalyzer {

    private ColorAnalyzer() {}

    private static final List<TileColor> TILE_COLORS = Arrays.asList(
            new TileColor(122, 122, 122, 0),  // Light gray, stone wall
            new TileColor(37, 166, 22, 1),    // Light green, grass
            new TileColor(98, 76, 60, 2),     // Brown, earth
            new TileColor(214, 199, 160, 3),  // Sandy beige, sand
            new TileColor(80, 119, 219, 4),   // Blue, water
            new TileColor(16, 11, 35, 5),     // Black, dark wall
            new TileColor(64, 64, 64, 6),     // Dark gray, gravel
            new TileColor(119, 132, 87, 7),   // Brown with sand bg, deadbush
            new TileColor(13, 160, 132, 8),   // Green with sand bg, cactus
            new TileColor(62, 113, 2, 9)      // Green-brown with grass bg, tree
    );

    public static double calculateColorDistance(Color color1, TileColor color2) {
        double dr = (color1.getRed() - color2.r) / 255.0;
        double dg = (color1.getGreen() - color2.g) / 255.0;
        double db = (color1.getBlue() - color2.b) / 255.0;
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    public static int getClosestTile(Color color) {
        return TILE_COLORS.stream()
                .min(Comparator.comparingDouble(tile ->
                        calculateColorDistance(color, tile)))
                .map(tile -> tile.tileNumber)
                .orElse(0);
    }

    public static Color getDominantColor(BufferedImage block) {
        Map<Integer, Integer> colorCount = new HashMap<>();

        for (int y = 0; y < block.getHeight(); y++) {
            for (int x = 0; x < block.getWidth(); x++) {
                int rgb = block.getRGB(x, y);
                colorCount.merge(rgb, 1, Integer::sum);
            }
        }

        int dominantRGB = colorCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);

        return new Color(dominantRGB);
    }
}

class ResultWriter {

    private ResultWriter(){}

    public static void writeTileMap(int[][] tileMap, Path outputPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            for (int[] row : tileMap) {
                StringBuilder line = new StringBuilder();
                for (int tile : row) {
                    if (!line.isEmpty()) {
                        line.append(" ");
                    }
                    line.append(tile);
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }
}

public class MapGenerator {
    private static final int BLOCK_SIZE = 16;
    private static final int MAX_TILES = 1000;
    private static final String MATRICES_PATH = "res/maps/map_matrices";
    private static final String ANALYSIS_PATH = "res/maps/map_analysis";
    private static final String LOG_CONTEXT = "[MAP GENERATOR]";

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

        GameLogger.info(LOG_CONTEXT,
                "New map dimensions set to: " + tilesWidth + "x" + tilesHeight + " tiles");
    }

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

    public static void processImage(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));

        validateImageDimensions(image.getWidth(), image.getHeight());

        int mapNumber = getNextMapNumber();

        Files.createDirectories(Paths.get(MATRICES_PATH));
        Files.createDirectories(Paths.get(ANALYSIS_PATH));

        int numBlocksH = image.getHeight() / BLOCK_SIZE;
        int numBlocksW = image.getWidth() / BLOCK_SIZE;
        int[][] tileMap = new int[numBlocksH][numBlocksW];

        BufferedImage blockAnalysis = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameLogger.info(LOG_CONTEXT,"Enter the path to your PNG image: ");
        String imagePath = scanner.nextLine();

        try {
            processImage(imagePath);
        } catch (Exception e) {
            GameLogger.error(LOG_CONTEXT, "Error occurred while processing image: " + e.getMessage(), e);
        }
    }
}