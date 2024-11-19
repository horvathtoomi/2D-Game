package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tile.TileManager;
import map.*;
import main.Engine;
import main.logger.GameLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Comparator;

@DisplayName("Map and Tile System Tests")
class MapSystemTest {
    private Engine engine;
    private TileManager tileManager;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        engine = new Engine();
        tileManager = engine.tileman;

        tempDir = Files.createTempDirectory("game-test");
        createTestDirectories();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        GameLogger.error("TEST", "Error deleting test files", e);
                    }
                });
    }

    private void createTestDirectories() throws IOException {
        Files.createDirectories(tempDir.resolve("res/maps/map_matrices"));
        Files.createDirectories(tempDir.resolve("res/maps/map_analysis"));
        Files.createDirectories(tempDir.resolve("res/tiles"));
    }

    @Nested
    @DisplayName("Tile Loading and Management Tests")
    class TileTests {

        @Test
        @DisplayName("Should load all tile types correctly")
        void testTileLoading() {
            assertNotNull(tileManager.tile);
            assertEquals(12, tileManager.tile.length);

            assertNotNull(tileManager.tile[0]); // Wall
            assertNotNull(tileManager.tile[1]); // Grass
            assertNotNull(tileManager.tile[4]); // Water

            assertTrue(tileManager.tile[0].collision); // Wall should have collision
            assertFalse(tileManager.tile[1].collision); // Grass should not have collision
            assertTrue(tileManager.tile[4].collision); // Water should have collision
        }

        @Test
        @DisplayName("Should handle tile boundary cases")
        void testTileBoundaries() {
            assertNotNull(tileManager.getTile(-1));
            assertNotNull(tileManager.getTile(12));

            assertEquals(tileManager.getTile(0), tileManager.getTile(-1));
            assertEquals(tileManager.getTile(0), tileManager.getTile(12));
        }

        @ParameterizedTest
        @CsvSource({
                "0, true",   // Wall
                "1, false",  // Grass
                "2, false",  // Earth
                "4, true",   // Water
                "9, true"    // Tree
        })
        @DisplayName("Should have correct collision properties")
        void testTileCollisions(int tileIndex, boolean expectedCollision) {
            assertEquals(expectedCollision, tileManager.tile[tileIndex].collision);
        }
    }

    @Nested
    @DisplayName("Map Generation Tests")
    class MapGenerationTests {

        @Test
        @DisplayName("Should validate image dimensions correctly")
        void testImageDimensionValidation() {
            BufferedImage validImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            BufferedImage invalidImage = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);

            assertDoesNotThrow(() -> MapGenerator.processImage(createTempImage(validImage, "valid.png")));

            assertThrows(IllegalArgumentException.class,
                    () -> MapGenerator.processImage(createTempImage(invalidImage, "invalid.png")));
        }

        @Test
        @DisplayName("Should process color mapping correctly")
        void testColorMapping() {
            BufferedImage testBlock = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = testBlock.createGraphics();

            g2d.setColor(new Color(37, 166, 22)); // Light green (grass)
            g2d.fillRect(0, 0, 16, 16);
            assertEquals(1, ColorAnalyzer.getClosestTile(ColorAnalyzer.getDominantColor(testBlock)));

            g2d.setColor(new Color(80, 119, 219)); // Blue (water)
            g2d.fillRect(0, 0, 16, 16);
            assertEquals(4, ColorAnalyzer.getClosestTile(ColorAnalyzer.getDominantColor(testBlock)));

            g2d.dispose();
        }
    }

    @Nested
    @DisplayName("Map Loading Tests")
    class MapLoadingTests {

        @Test
        @DisplayName("Should load story map correctly")
        void testStoryMapLoading() throws IOException {
            createTestStoryMapFile();

            tileManager.loadStoryMap(true);
            assertNotNull(TileManager.mapTileNum);

            assertEquals(engine.getMaxWorldCol(), TileManager.mapTileNum.length);
            assertEquals(engine.getMaxWorldRow(), TileManager.mapTileNum[0].length);
        }

        @Test
        @DisplayName("Should handle missing map files")
        void testMissingMapHandling() {
            assertDoesNotThrow(() -> tileManager.loadStoryMap(true));
        }

        @Test
        @DisplayName("Should load custom map correctly")
        void testCustomMapLoading() throws IOException {
            Path customMapPath = createTestCustomMapFile();

            TileManager.loadCustomMap(customMapPath.toString());
            assertNotNull(TileManager.mapTileNum);

            assertTrue(TileManager.mapTileNum[0][0] >= 0);
            assertTrue(TileManager.mapTileNum[0][0] < tileManager.tile.length);
        }
    }

    private String createTempImage(BufferedImage image, String filename) throws IOException {
        Path imagePath = tempDir.resolve(filename);
        ImageIO.write(image, "PNG", imagePath.toFile());
        return imagePath.toString();
    }

    private void createTestStoryMapFile() throws IOException {
        Path mapPath = tempDir.resolve("res/maps/map_matrices/story_mode/story_map_1.txt");
        Files.createDirectories(mapPath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(mapPath)) {
            for (int i = 0; i < 100; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < 100; j++) {
                    line.append(j == 0 ? "1" : " 1");
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }

    private Path createTestCustomMapFile() throws IOException {
        Path mapPath = tempDir.resolve("res/maps/map_matrices/custom_map.txt");
        Files.createDirectories(mapPath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(mapPath)) {
            for (int i = 0; i < 100; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < 100; j++) {
                    line.append(j == 0 ? "1" : " 1");
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }

        return mapPath;
    }
}