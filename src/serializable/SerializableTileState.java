package serializable;

import java.io.Serial;
import java.io.Serializable;

public class SerializableTileState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    int[][] mapTileNum;

    SerializableTileState(int[][] mapTileNum) {
        this.mapTileNum = mapTileNum;
    }
}