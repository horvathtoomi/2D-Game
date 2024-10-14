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

    public EnemyTest et1,et2,et3;
    public SmallEnemy se1,se2,se3,se4;

    //Game State
    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;

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
            e.printStackTrace();
        }

        aSetter.setNPC();
        et1 = new EnemyTest(this, 25*tileSize,21*tileSize);
        et2 = new EnemyTest(this, 14*tileSize,20*tileSize);
        et3 = new EnemyTest(this, 22*tileSize,45*tileSize);
        se1 = new SmallEnemy(this, 25*tileSize, 25*tileSize);
        se2 = new SmallEnemy(this, 35*tileSize, 34*tileSize);
        se3 = new SmallEnemy(this, 10*tileSize, 10*tileSize);
        se4 = new SmallEnemy(this, 20*tileSize, 40*tileSize);
        aSetter.list = new CopyOnWriteArrayList<>(aSetter.list);
        gameState=playState;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void run(){
        double drawInterval = (double) 1000000000 /FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while(gameThread!=null) {
            if(player.health>0) {
                update();
                repaint();
                try {
                    double remainingTime = nextDrawTime - System.nanoTime();
                    remainingTime /= 1000000;
                    if (remainingTime < 0)
                        remainingTime = 0;
                    Thread.sleep((long) remainingTime);
                    nextDrawTime += drawInterval;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update() {
        if(gameState == playState) {
            player.update();
            et1.update();
            et2.update();
            et3.update();
            se1.update();
            se2.update();
            se3.update();
            se4.update();
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
        //if(gameState == pauseState) {
        //}
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
        if(et1 != null) {
            et1.draw(g2);
        }
        if(et2 != null) {
            et2.draw(g2);
        }
        if(et3 != null) {
            et3.draw(g2);
        }
        if(se1 != null) {
            se1.draw(g2);
        }
        if(se2 != null) {
            se2.draw(g2);
        }
        if(se3 != null) {
            se3.draw(g2);
        }
        if(se4 != null) {
            se4.draw(g2);
        }
        ui.draw(g2);
        g2.dispose();
    }


}