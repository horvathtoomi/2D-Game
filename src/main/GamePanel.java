
package main;

import entity.*;
import object.*;
import tile.TileManager;
import javax.swing.JPanel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;



public class GamePanel extends JPanel implements Runnable {
    final int OriginalTileSize=16; //16x16-os
    final int scale = 3;
    public final int tileSize = OriginalTileSize*scale; //48x48-as
    public final int maxScreenCol =16;
    public final int maxScreenRow =12;
    public final int screenWidth = maxScreenCol*tileSize; //768 pixel
    public final int screenHeight = maxScreenRow*tileSize; //576 pixel

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = maxWorldCol*tileSize;
    public final int worldHeight = maxWorldRow*tileSize;

    int FPS=60;

    public TileManager tileman=new TileManager(this);

    InputHandler inpkez = new InputHandler(this);

    public UserInterface ui=new UserInterface(this);

    Thread gameThread;

    public CollisionChecker cChecker=new CollisionChecker(this);
    public Player player = new Player(this,inpkez);
    public AssetSetter aSetter= new AssetSetter(this);
    public ArrayList<Entity> entities = new ArrayList<>();

    //Game State
    public enum GameState{RUNNING,PAUSED,FINISHED}
    public GameState gameState;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(inpkez);
        this.setFocusable(true);
    }

    public void setupGame(){
        try{
            aSetter.setObject();
        }catch(IOException e){
            System.out.println("Object was not set.");
            e.getCause();
        }
        aSetter.setNPC();
        addEnemy(new EnemyTest(this, 25 * tileSize, 21 * tileSize));
        addEnemy(new EnemyTest(this, 22 * tileSize, 45 * tileSize));
        addEnemy(new SmallEnemy(this, 25 * tileSize, 25 * tileSize));
        addEnemy(new SmallEnemy(this, 35 * tileSize, 34 * tileSize));
        addEnemy(new GiantEnemy(this,30 * tileSize, 30 * tileSize));
        aSetter.list = new CopyOnWriteArrayList<>(aSetter.list);
        gameState=GameState.RUNNING;
    }

    private void addEnemy(Entity enemy){
        entities.add(enemy);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null && player.health > 0) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = Math.max(0, remainingTime / 1_000_000);
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.getCause();
            }
        }
    }

    public void update() {
        if(gameState == GameState.RUNNING) {
            player.update();
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                if (entity != null) {
                    entity.update();
                }
                else {
                    entities.remove(i);
                    i--;  // Adjust index after removal
                }
            }
            for (SuperObject obj : aSetter.list)
                if (obj != null)
                    obj.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //Tile
        tileman.draw(g2);
        //Object
        for(SuperObject object : aSetter.list)
            if (object != null)
                object.draw(g2, this);
        //NPC and Entities
        for(Entity entity : entities)
            if(entity != null)
                entity.draw(g2);
        player.draw(g2);
        ui.draw(g2);
        g2.dispose();
    }

}