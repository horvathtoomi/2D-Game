package main;

import entity.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;
import main.console.ConsoleHandler;
import main.logger.GameLogger;
import map.MapGenerator;
import object.*;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    private static final int OriginalTileSize = 16;    //16x16-os
    private static final int scale = 3;
    private static final int tileSize = OriginalTileSize * scale;  //48x48-as
    private static final int maxScreenCol = 24;    //16
    private final int maxScreenRow = 18;    //12
    private int maxWorldCol = 100;
    private int maxWorldRow = 100;
    private final int FPS = 60;

    private final int currentStory = 1;
    private static final int MAX_STORY_LEVEL = 6;

    public final transient CollisionChecker cChecker=new CollisionChecker(this);
    public Player player;
    public AssetSetter aSetter;
    private CopyOnWriteArrayList<Entity> entities;
    public TileManager tileman = new TileManager(this);
    public final transient InputHandler inpkez = new InputHandler(this);
    public final transient MouseHandler mouseHandler;
    public UserInterface userInterface;
    public transient Thread gameThread;
    public final transient ConsoleHandler console;
    private static final String LOG_CONTEXT = "[GAME PANEL]";

    public enum GameState {START, DIFFICULTY_SCREEN, GAME_MODE_SCREEN, RUNNING,PAUSED, FINISHED, SAVE, LOAD, CONSOLE_INPUT} //Game State
    public enum GameDifficulty {EASY, MEDIUM, HARD, IMPOSSIBLE}
    public enum GameMode {NONE, STORY, CUSTOM}
    private GameState gameState;
    private GameDifficulty difficulty;
    private GameMode gameMode = GameMode.NONE;


    public GameState getGameState(){return gameState;}
    public GameDifficulty getGameDifficulty(){return difficulty;}
    public GameMode getGameMode(){return gameMode;}
    public int getFPS() {return FPS;}
    public int getTileSize() {return tileSize;}
    public int getScreenWidth() {return maxScreenCol*tileSize;} //768 pixel
    public int getScreenHeight() {return maxScreenRow*tileSize;} //576 pixel
    public int getMaxWorldCol() {return maxWorldCol;}
    public int getMaxWorldRow() {return maxWorldRow;}
    public int getWorldWidth() {return maxWorldCol * tileSize;}
    public int getWorldHeight() {return maxWorldRow * tileSize;}
    public CopyOnWriteArrayList<Entity> getEntity() {return entities;}

    public void setGameState(GameState state){gameState = state;}
    public void setGameMode(GameMode mode){gameMode = mode;}
    public void setGameDifficulty(GameDifficulty diff){difficulty = diff;}
    public void setEntities(CopyOnWriteArrayList<Entity> entities){this.entities = entities;}
    public void setMaxWorldCol(int a) {maxWorldCol = a;}
    public void setMaxWorldRow(int a) {maxWorldRow = a;}

    public void addEntity(Entity ent){
        entities.add(ent);
    }
    public void addObject(SuperObject obj) {aSetter.list.add(obj);}
    public void removeEnemy(Entity ent) {entities.remove(ent);}

    public GamePanel() {
        GameLogger.info(LOG_CONTEXT, "|INITIALIZING GAMEPANEL|");
        player = new Player(this,inpkez);
        entities = new CopyOnWriteArrayList<>();
        aSetter = new AssetSetter(this);
        userInterface = new UserInterface(this);
        mouseHandler=new MouseHandler(this);
        console=new ConsoleHandler(this);
        setGamePanel();
    }

    private void setGamePanel(){
        gameState = GameState.START;
        difficulty = GameDifficulty.EASY;
        this.setPreferredSize(new Dimension(getScreenWidth(),getScreenHeight()));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(inpkez);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.setFocusable(true);
    }

    public void setupStoryMode(){
        tileman.loadStoryMap();
        try{
            aSetter.setObject();
        }catch(IOException e){
            GameLogger.error(LOG_CONTEXT, "|FAILED TO INITIALIZE THE GAME|", e);
        }
        aSetter.setNPC();
        /*
         * Adding entities here
         *
         */
    }

    public void setupCustomMode(){
        MapGenerator.GUIMapGenerator();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        GameLogger.info(LOG_CONTEXT, "|STARTING GAME LOOP|");
        double drawInterval = 1_000_000_000.0 / FPS; //Setting the game's FPS
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (gameThread != null) {
            if(player.getHealth()<=0 && gameState == GameState.RUNNING)
                gameState=GameState.FINISHED;
            else if(gameState == GameState.RUNNING)
                update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = Math.max(0, remainingTime / 1_000_000);
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                GameLogger.error(LOG_CONTEXT, "Unexpected error occured.",e);
            }
        }
    }

    public void update() {
        if(gameState == GameState.RUNNING) {
            player.update();
            entities.removeIf(Objects::isNull);
            aSetter.list.removeIf(Objects::isNull);
            entities.forEach(Entity::update);
            aSetter.list.forEach(SuperObject::update);
            player.getInventory().update();
        }
    }

    @Override
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
        userInterface.draw(g2);
        g2.dispose();
    }

    public void resetGame() {
        entities.clear();
        aSetter.list.clear();
        player = new Player(this, inpkez);
        setupStoryMode();
    }



    public void checkLevelCompletion(){
        if(gameMode == GameMode.STORY){
            if(currentStory < MAX_STORY_LEVEL)
                tileman.loadStoryMap();
            else
                setGameState(GameState.FINISHED);
        }
    }

}