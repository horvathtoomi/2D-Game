package tile;

import main.GamePanel;
import main.UtilityTool;
import main.logger.GameLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.Objects;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;
    private static final String LOG_CONTEXT = "[TILE MANAGER]";

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
        getTileImage();
        loadMap("maps/map_matrices/" + getCurrentMap());
    }

    private String getCurrentMap() {
        File dir = new File("res/maps/map_matrices");
        if (!dir.exists() || !dir.isDirectory()) {
            GameLogger.error(LOG_CONTEXT, "Map matrices directory not found", new IOException());
            return "map1.txt";
        }

        int maxIndex = 0;
        File[] files = dir.listFiles((d, name) -> name.matches("map\\d+\\.txt"));

        if (files != null) {
            for (File file : files) {
                try {
                    int currentIndex = Integer.parseInt(file.getName().replaceAll("map(\\d+)\\.txt", "$1"));
                    maxIndex = Math.max(maxIndex, currentIndex);
                } catch (NumberFormatException e) {
                    GameLogger.error(LOG_CONTEXT, "Error parsing map number: " + file.getName(), e);
                }
            }
        }

        if (maxIndex == 0) {
            GameLogger.warn(LOG_CONTEXT,"No valid map files found, using default map");
            return "map1.txt";
        }

        return "map" + maxIndex + ".txt";
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

    public void loadMap(String address){
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream(address);
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col=0, row=0;
            while(col<gp.getMaxWorldCol()&&row<gp.getMaxWorldRow()){
                String line=br.readLine();
                while(col<gp.getMaxWorldCol()){
                    String[] numbers =line.split(" ");
                    int num=Integer.parseInt(numbers[col]);
                    mapTileNum[col][row]=num;
                    col++;
                }
                if(col==gp.getMaxWorldCol()){
                    col=0;
                    row++;
                }
            }
            br.close();
        }catch(Exception e){
            GameLogger.error(LOG_CONTEXT, "Failed to load map: " + address, e);
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
