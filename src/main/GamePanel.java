package main;

import entity.EnemyTest;
import entity.Player;
import object.*;
import tile.TileManager;
import javax.swing.JPanel;
import java.awt.*;
import java.io.IOException;
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
    InputHandler inpkez = new InputHandler();
    public UserInterface ui=new UserInterface(this);
    Thread gameThread;
    public CollisionChecker cChecker=new CollisionChecker(this);
    public Player player = new Player(this,inpkez);
    public AssetSetter aSetter= new AssetSetter(this);
    public EnemyTest et = null;

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

        et = new EnemyTest(this, 23*tileSize,21*tileSize);
        aSetter.lista = new CopyOnWriteArrayList<>(aSetter.lista);
    }

    public void addObject(SuperObject obj){
        aSetter.lista.add(obj);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void run(){
        double drawInterval =1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while(gameThread!=null) {
            update();
            repaint();
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1000000;
                if(remainingTime<0)
                    remainingTime=0;
                Thread.sleep((long)remainingTime);
                nextDrawTime += drawInterval;
            }catch(InterruptedException e){e.printStackTrace();}
        }
    }

    public void killEntitys(){
        et.left = et.right = et.shoot = null;
    }

    public void update() {
        player.update();
        et.update();
        for(SuperObject obj : aSetter.lista) {
            if (obj != null) {
                obj.update();
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //Tile
        tileman.draw(g2);
        //Object
        for(SuperObject object : aSetter.lista) {
            if (object != null)
                object.draw(g2, this);
        }
        player.draw(g2);
        if(et!=null) {
            et.draw(g2);
        }
        ui.draw(g2);
        g2.dispose();
    }

}