package map;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResultWriter {

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