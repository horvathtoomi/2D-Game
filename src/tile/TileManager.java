package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap("maps/map1.txt");
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
            tile[idx].image = uTool.scaleImage(tile[idx].image,gp.tileSize,gp.tileSize);
            tile[idx].collision=collision;
        }catch(IOException e){e.getCause();}
    }

    public void loadMap(String address){
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream(address);
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col=0, row=0;
            while(col<gp.maxWorldCol&&row<gp.maxWorldRow){
                String line=br.readLine();
                while(col<gp.maxWorldCol){
                    String[] numbers =line.split(" ");
                    int num=Integer.parseInt(numbers[col]);
                    mapTileNum[col][row]=num;
                    col++;
                }
                if(col==gp.maxWorldCol){
                    col=0;
                    row++;
                }
            }
            br.close();
        }catch(Exception e){ e.printStackTrace(); }
    }

    public void draw(Graphics2D g2){
        int worldCol=0, worldRow=0;
        while(worldCol<gp.maxWorldCol && worldRow<gp.maxWorldRow){
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            //Stop moving the camera at the edge of the map
            if(gp.player.screenX>gp.player.worldX)
                screenX = worldX;
            if(gp.player.screenY>gp.player.worldY)
                screenY = worldY;
            int rightOffset = gp.screenWidth - gp.player.screenX;

            if(rightOffset > gp.worldWidth - gp.player.worldX)
                screenX = gp.screenWidth - gp.worldWidth - worldX;

            int bottomOffset = gp.screenHeight - gp.player.screenY;

            if(bottomOffset > gp.worldHeight - gp.player.worldY)
                screenY = gp.screenHeight - gp.worldHeight - worldY;

            if(worldX+gp.tileSize > gp.player.worldX - gp.player.screenX && worldX-gp.tileSize < gp.player.worldX + gp.player.screenX && worldY+gp.tileSize > gp.player.worldY - gp.player.screenY && worldY-gp.tileSize < gp.player.worldY + gp.player.screenY)
                g2.drawImage(tile[tileNum].image,screenX,screenY,null);

            else if(gp.player.screenX > gp.player.worldX || gp.player.screenY > gp.player.worldY || rightOffset > gp.worldWidth - gp.player.worldX || bottomOffset > gp.worldHeight - gp.player.worldY)
                g2.drawImage(tile[tileNum].image,screenX,screenY,null);

            worldCol++;
            if(worldCol==gp.maxWorldCol){
                worldCol=0;
                worldRow++;
            }
        }
    }

}
