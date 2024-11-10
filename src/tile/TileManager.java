package tile;

import main.GamePanel;
import main.UtilityTool;
import main.logger.GameLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.Objects;
import java.util.Random;

public class TileManager {
    GamePanel gp;
    Random rand = new Random();
    public Tile[] tile;
    private int mapNumber = 1;
    public int[][] mapTileNum;
    private static final String LOG_CONTEXT = "[TILE MANAGER]";

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        getTileImage();
        mapTileNum = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
    }

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
    }

    public void setup(int idx, String imagePath, boolean collision){
        UtilityTool uTool = new UtilityTool();
        try{
            tile[idx]=new Tile();
            tile[idx].image=ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tiles/" + imagePath + ".png")));
            tile[idx].image = uTool.scaleImage(tile[idx].image,gp.getTileSize(),gp.getTileSize());
            tile[idx].collision=collision;
        }catch(IOException e){
            GameLogger.error(LOG_CONTEXT, "Failed to setup Map", e);
        }
    }

    public void loadStoryMap(){
        String address = "res/maps/map_matrices/story_mode/story_map_" + mapNumber + ".txt";
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream(address);
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col=0, row=0;
            while(col < gp.getMaxWorldCol() && row < gp.getMaxWorldRow()){
                String line=br.readLine();
                while(col < gp.getMaxWorldCol()){
                    String[] numbers =line.split(" ");
                    int num=Integer.parseInt(numbers[col]);
                    mapTileNum[col][row]=num;
                    col++;
                }
                if(col == gp.getMaxWorldCol()){
                    col=0;
                    row++;
                }
            }
            mapNumber++;
            br.close();
        }catch(Exception e){
            GameLogger.error(LOG_CONTEXT, "Failed to load map: " + address, e);
            GameLogger.warn(LOG_CONTEXT, "Initializing a clean map");
            createCleanMap();
        }
    }

    public void loadCustomMap(String address){
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream(address);
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col=0, row=0;
            while(col < gp.getMaxWorldCol() && row < gp.getMaxWorldRow()){
                String line=br.readLine();
                while(col < gp.getMaxWorldCol()){
                    String[] numbers =line.split(" ");
                    int num=Integer.parseInt(numbers[col]);
                    mapTileNum[col][row]=num;
                    col++;
                }
                if(col == gp.getMaxWorldCol()){
                    col=0;
                    row++;
                }
            }
            br.close();
        }catch(Exception e){
            GameLogger.error(LOG_CONTEXT, "Failed to load map: " + address, e);
            GameLogger.warn(LOG_CONTEXT, "Initializing a clean map");
            createCleanMap();
        }
    }

    private void createCleanMap() {
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
            GameLogger.error(LOG_CONTEXT, "Some unexpected error occured: "+ e.getMessage() + "\nClosing application.", e);
            System.exit(1);
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;
        while (worldCol < gp.getMaxWorldCol() && worldRow < gp.getMaxWorldRow()) {
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gp.getTileSize();
            int worldY = worldRow * gp.getTileSize();
            int screenX = worldX - gp.player.getWorldX() + gp.player.getScreenX();
            int screenY = worldY - gp.player.getWorldY() + gp.player.getScreenY();

            // Adjust these calculations to prevent black screen at map edges
            if (gp.player.getScreenX() > gp.player.getWorldX()) {
                screenX = worldX;
            }
            if (gp.player.getScreenY() > gp.player.getWorldY()) {
                screenY = worldY;
            }
            int rightOffset = gp.getScreenWidth() - gp.player.getScreenX();
            if (rightOffset > gp.getWorldWidth() - gp.player.getWorldX()) {
                screenX = gp.getScreenWidth() - (gp.getWorldWidth() - worldX);
            }
            int bottomOffset = gp.getScreenHeight() - gp.player.getScreenY();
            if (bottomOffset > gp.getWorldHeight() - gp.player.getWorldY()) {
                screenY = gp.getScreenHeight() - (gp.getWorldHeight() - worldY);
            }

            // Only draw the tile if it's within the screen bounds
            if (screenX > -gp.getTileSize() && screenX < gp.getScreenWidth() &&
                    screenY > -gp.getTileSize() && screenY < gp.getScreenHeight()) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, null);
            }

            worldCol++;
            if (worldCol == gp.getMaxWorldCol()) {
                worldCol = 0;
                worldRow++;
            }
        }
    }

}
