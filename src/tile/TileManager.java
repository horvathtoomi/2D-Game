package tile;

import main.Engine;
import main.GameState;
import main.UtilityTool;
import main.logger.GameLogger;
import map.MapGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.Objects;
import java.util.Random;

/**
 * A TileManager osztály felelős a játékpálya tileok kezeléséért.
 * Kezeli a tileok betöltését, megjelenítését és a pálya struktúráját.
 */
public class TileManager {
    static Engine eng;
    static Random rand = new Random();
    public static Tile[] tile;
    private int mapNumber = 3;
    public static int[][] mapTileNum;
    private static final String LOG_CONTEXT = "[TILE MANAGER]";

    /**
     * Visszaadja a tilemátrixot.
     * @return a pálya tile mátrixa
     */
    public int[][] getMapTileNum() {return mapTileNum;}

    /**
     * Visszaad egy tile az indexe alapján.
     * @param idx a tile indexe
     * @return a kért tile, vagy az alapértelmezett (0. index) ha az index érvénytelen
     */
    public Tile getTile(int idx){
        if(idx>tile.length-1 || idx<0){
            GameLogger.warn(LOG_CONTEXT, "Bad indexing");
            return tile[0];
        }
        return tile[idx];
    }

    /**
     * Létrehoz egy új tilekezelő példányt.
     * @param engine a játékmotor példánya
     */
    public TileManager(Engine engine) {
        eng = engine;
        tile = new Tile[12];
        getTileImage();
        mapTileNum = new int[eng.getMaxWorldCol()][eng.getMaxWorldRow()];
    }

    /**
     * Betölti az összes tile képét és beállítja tulajdonságaikat.
     */
    public void getTileImage(){
        setup(0,"wall",true);
        setup(1,"grass",false);
        setup(2,"earth",false);
        setup(3,"sand",false);
        setup(4,"water",true);
        setup(5,"blackborder",true);
        setup(6,"blacksand",false);
        setup(7,"deadbush",false);
        setup(8,"cactus",false);
        setup(9,"tree",true);
        setup(10,"gravel",false);
        setup(11, "lava", true);
    }

    /**
     * Beállít egy csempetípust a megadott paraméterekkel.
     * @param idx a csempe indexe
     * @param imagePath a csempe képének elérési útja
     * @param collision van-e ütközés a csempével
     */
    public void setup(int idx, String imagePath, boolean collision){
        UtilityTool uTool = new UtilityTool();
        try{
            tile[idx]=new Tile();
            tile[idx].image=ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tiles/" + imagePath + ".png")));
            tile[idx].image = uTool.scaleImage(tile[idx].image,eng.getTileSize(),eng.getTileSize());
            tile[idx].collision=collision;
        }catch(IOException e){
            GameLogger.error(LOG_CONTEXT, "Failed to setup Map", e);
        }
    }

    /**
     * Betölti a történet mód pályáját.
     * @param reset jelzi, hogy újra kell-e kezdeni a pályák betöltését
     */
    public void loadStoryMap(boolean reset){
        if(reset) {
            mapNumber = 1;
        }
        String address = "res/maps/map_matrices/story_mode/story_map_" + mapNumber + ".txt";
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(address)))){
            int col=0;
            int row=0;
            while(col < eng.getMaxWorldCol() && row < eng.getMaxWorldRow()){
                String line=br.readLine();
                while(col < eng.getMaxWorldCol()){
                    String[] numbers =line.split(" ");
                    int num=Integer.parseInt(numbers[col]);
                    mapTileNum[col][row]=num;
                    col++;
                }
                if(col == eng.getMaxWorldCol()){
                    col=0;
                    row++;
                }
            }
            mapNumber++;
        }catch(Exception e){
            GameLogger.error(LOG_CONTEXT, "Failed to load map: " + address, e);
            GameLogger.warn(LOG_CONTEXT, "Initializing a clean map");
            createCleanMap();
        }
    }

    /**
     * Meghatározza a megfelelő pálya fájl elérési útját.
     * @param path a megadott útvonal
     * @return a végleges útvonal
     * @throws IOException ha a fájl nem elérhető
     */
    private static String getRightPath(String path) throws IOException {
        return path == null ? "res/maps/map_matrices/map" + (MapGenerator.getNextMapNumber() - 1) + ".txt" : path;
    }

    /**
     * Betölt egy egyéni pályát.
     * @param mapPath a pályafájl elérési útja
     */
    public static void loadCustomMap(String mapPath) {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getRightPath(mapPath))))){
            int col=0;
            int row=0;
            while(col < eng.getMaxWorldCol() && row < eng.getMaxWorldRow()){
                String line=br.readLine();
                while(col < eng.getMaxWorldCol()){
                    String[] numbers =line.split(" ");
                    int num=Integer.parseInt(numbers[col]);
                    mapTileNum[col][row]=num;
                    col++;
                }
                if(col == eng.getMaxWorldCol()){
                    col=0;
                    row++;
                }
            }
            eng.startGame();
            eng.setGameState(GameState.RUNNING);
        }catch(Exception e){
            GameLogger.error(LOG_CONTEXT, "Failed to load map", e);
            GameLogger.warn(LOG_CONTEXT, "Initializing a clean map");
            createCleanMap();
        }
        eng.player.setDefaultValues();
    }

    /**
     * Létrehoz egy alapértelmezett pályát véletlenszerű elemekkel.
     */
    private static void createCleanMap() {
        int[] tomb = new int[4];
        tomb[0] = 1;
        tomb[1] = 2;
        tomb[2] = 3;
        tomb[3] = 6;
        String filePath = "res/maps/map_matrices/default.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < 100; i++) {
                writer.write("0 ");
            }
            writer.newLine();
            for (int j = 0; j < 98; j++) {
                writer.write("0 ");
                for (int i = 0; i < 49; i++) {
                    int rand_int1 = rand.nextInt(5);
                    int rand_int2 = rand.nextInt(5);
                    if (rand_int1 == 0) rand_int1++;
                    if (rand_int2 == 0) rand_int2++;
                    writer.write(tomb[i % rand_int1] + " ");
                    writer.write(tomb[i % rand_int2] + " ");
                }
                writer.write("0 ");
                writer.newLine();
            }
            for (int i = 0; i < 100; i++) {
                writer.write("0 ");
            }
        } catch (IOException e) {
            GameLogger.error(LOG_CONTEXT, "Some unexpected error occurred: "+ e.getMessage() + "\nClosing application.", e);
            System.exit(1);
        }
        loadCustomMap("res/maps/map_matrices/default.txt");
    }

    /**
     * Kirajzolja a pályát a képernyőre.
     * Csak azokat a tileokat rajzolja ki, amelyek láthatóak a képernyőn.
     * @param g2 a grafikus kontextus
     */
    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;
        while (worldCol < eng.getMaxWorldCol() && worldRow < eng.getMaxWorldRow()) {
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * eng.getTileSize();
            int worldY = worldRow * eng.getTileSize();
            int screenX = worldX - eng.player.getWorldX() + eng.player.getScreenX();
            int screenY = worldY - eng.player.getWorldY() + eng.player.getScreenY();

            if (eng.player.getScreenX() > eng.player.getWorldX()) {
                screenX = worldX;
            }
            if (eng.player.getScreenY() > eng.player.getWorldY()) {
                screenY = worldY;
            }
            int rightOffset = eng.getScreenWidth() - eng.player.getScreenX();
            if (rightOffset > eng.getWorldWidth() - eng.player.getWorldX()) {
                screenX = eng.getScreenWidth() - (eng.getWorldWidth() - worldX);
            }
            int bottomOffset = eng.getScreenHeight() - eng.player.getScreenY();
            if (bottomOffset > eng.getWorldHeight() - eng.player.getWorldY()) {
                screenY = eng.getScreenHeight() - (eng.getWorldHeight() - worldY);
            }

            if (screenX > -eng.getTileSize() && screenX < eng.getScreenWidth() &&
                    screenY > -eng.getTileSize() && screenY < eng.getScreenHeight()) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, null);
            }

            worldCol++;
            if (worldCol == eng.getMaxWorldCol()) {
                worldCol = 0;
                worldRow++;
            }
        }
    }

}
