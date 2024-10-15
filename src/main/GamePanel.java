
package main;

import entity.*;
import object.*;
import serializable.FileManager;
import tile.TileManager;
import javax.swing.JPanel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;



public class GamePanel extends JPanel implements Runnable {
    final int OriginalTileSize=16; //16x16-os
    final int scale = 3;
    public final int tileSize = OriginalTileSize*scale; //48x48-as
    public final int maxScreenCol =24/*16*/;
    public final int maxScreenRow =18/*12*/;
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
    public AssetSetter aSetter;
    public CopyOnWriteArrayList<Entity> entities;

    //Game State
    public enum GameState{RUNNING,PAUSED,FINISHED,START}
    public GameState gameState;

    public GamePanel() {
        this.entities = new CopyOnWriteArrayList<>();
        this.aSetter = new AssetSetter(this);
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
        addEnemy(new DragonEnemy(this, 25 * tileSize, 21 * tileSize));
        addEnemy(new DragonEnemy(this, 22 * tileSize, 45 * tileSize));
        addEnemy(new SmallEnemy(this, 25 * tileSize, 25 * tileSize));
        addEnemy(new SmallEnemy(this, 35 * tileSize, 34 * tileSize));
        addEnemy(new GiantEnemy(this,30 * tileSize, 30 * tileSize));
        gameState=GameState.START;
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
            switch (gameState) {
                case START -> handleStartMenuInput();
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

    public void saveGame() {
        try {
            FileManager.saveGameState(this, "save.dat");
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean loadGame() {
        try {
            FileManager.loadGameState(this, "save.dat");
            System.out.println("Game loaded successfully.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void resetGame() {
        // Reset game state to initial values
        player = new Player(this, inpkez);
        entities.clear();
        aSetter.list.clear();
        setupGame(); // This should reinitialize your game state
        gameState = GameState.RUNNING;
    }

    public void handleStartMenuInput() {
        if (inpkez.enterPressed) {
            gameState = GameState.RUNNING;
            setupGame();
        } else if (inpkez.loadPressed) {
            if (loadGame()) {
                gameState = GameState.RUNNING;
            } else {
                // If load fails, start a new game
                setupGame();
                gameState = GameState.RUNNING;
            }
        }
        // Reset the flags after handling
        inpkez.enterPressed = false;
        inpkez.loadPressed = false;
    }

}