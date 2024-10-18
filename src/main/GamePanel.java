package main;

import entity.*;
import object.*;
import serializable.FileManager;
import tile.TileManager;
import javax.swing.JPanel;
import java.awt.*;
import java.io.*;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;


public class GamePanel extends JPanel implements Runnable {

    //Game Screen settings
    private final int OriginalTileSize = 16;    //16x16-os
    private final int scale = 3;    //sad
    private final int tileSize = OriginalTileSize * scale;  //48x48-as
    private final int maxScreenCol = 24;    //16
    private final int maxScreenRow = 18;    //12
    private final int maxWorldCol = 50;
    private final int maxWorldRow = 50;

    public CollisionChecker cChecker=new CollisionChecker(this);
    public Player player;
    public AssetSetter aSetter;
    public CopyOnWriteArrayList<Entity> entities;
    public TileManager tileman=new TileManager(this);
    public InputHandler inpkez = new InputHandler(this);
    public MouseHandler mouseHandler;
    public UserInterface ui;
    public Thread gameThread;

    public enum GameState{START,RUNNING,PAUSED,FINISHED} //Game State
    public GameState gameState;

    public int getTileSize() {return tileSize;}
    public int getScreenWidth() {return maxScreenCol*tileSize;} //768 pixel
    public int getScreenHeight() {return maxScreenRow*tileSize;} //576 pixel
    public int getMaxWorldCol() {return maxWorldCol;}
    public int getMaxWorldRow() {return maxWorldRow;}
    public int getWorldWidth() {return maxWorldCol * tileSize;}
    public int getWorldHeight() {return maxWorldRow * tileSize;}


    public GamePanel() {
        player = new Player(this,inpkez);
        entities = new CopyOnWriteArrayList<>();
        aSetter = new AssetSetter(this);
        ui=new UserInterface(this);
        gameState=GameState.START;
        this.setPreferredSize(new Dimension(getScreenWidth(),getScreenHeight()));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(inpkez);
        //
        mouseHandler=new MouseHandler(this);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        //
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
        addEnemy(new DragonEnemy(this, 25 * tileSize, 21 * tileSize));
        addEnemy(new SmallEnemy(this, 25 * tileSize, 25 * tileSize));
        addEnemy(new GiantEnemy(this,15 * tileSize, 20 * tileSize));
        addEnemy(new FriendlyEnemy(this,30 * tileSize,20 * tileSize));
    }

    private void addEnemy(Entity enemy){
        entities.add(enemy);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        double drawInterval = 1_000_000_000.0 / 60; //Setting the game's FPS
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (gameThread != null) {
            if(player.getHealth()<=0 && gameState == GameState.RUNNING)
                gameState=GameState.FINISHED;
            switch (gameState) {
                case START, FINISHED -> {}
                case RUNNING -> update();
            }
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
            entities.removeIf(Objects::isNull);
            aSetter.list.removeIf(Objects::isNull);
            for(Entity e : entities)
                e.update();
            for (SuperObject obj : aSetter.list)
                obj.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tileman.draw(g2);
        entities.removeIf(Objects::isNull);
        aSetter.list.removeIf(Objects::isNull);
        for(SuperObject object : aSetter.list)
            object.draw(g2, this);
        for(Entity entity : entities)
            entity.draw(g2);
        player.draw(g2);
        ui.draw(g2);
        g2.dispose();
    }

    public void saveGame() {
        String fpath = null;
        try {
            System.out.print("Filename: ");
            fpath = FileManager.getFileName();
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("Wrong filepath");
        }
        try {
            FileManager.saveGameState(this, "res/save/" + fpath);
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean loadGame() {
        String fpath = null;
        try {
            System.out.print("Filepath: ");
            fpath = FileManager.getFileName();
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("Wrong filepath");
        }
        if(fpath != null && !fpath.isEmpty()) {
            try {
                FileManager.loadGameState(this, "res/save/" + fpath);
                System.out.println("Game loaded successfully.");
                return true;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load game: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        else
            return false;
    }

    public void resetGame() {
        entities.clear();
        aSetter.list.clear();
        player = new Player(this, inpkez);
        setupGame();
    }

}