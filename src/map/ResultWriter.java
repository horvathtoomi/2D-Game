package map;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A ResultWriter osztály felelős a generált pályák fájlba írásáért.
 */
public class ResultWriter {

    private ResultWriter(){}

    /**
     * Kiírja a csempetérképet egy fájlba.
     * @param tileMap a csempetérkép
     * @param outputPath a kimeneti fájl útvonala
     * @throws IOException ha a fájl írása sikertelen
     */
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