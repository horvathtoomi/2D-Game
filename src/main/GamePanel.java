package main;

import entity.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;
import main.console.ConsoleHandler;
import main.logger.GameLogger;
import map.MapGenerator;
import object.*;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    private static final int ORIGINAL_TILE_SIZE = 16;
    private static final int SCALE = 3;
    private static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
    private static final int MAX_SCREEN_COL = 24;
    private static final int MAX_SCREEN_ROW = 18;
    private static int MAX_WORLD_COL = 100;
    private static int MAX_WORLD_ROW = 100;
    private static final int FPS = 60;

    private int currentStoryLevel = 0;
    private static final int MAX_STORY_LEVEL = 3;

    private final transient int[][] endPoints = {
            {100 * TILE_SIZE, 94 * TILE_SIZE},
            {TILE_SIZE, 85 * TILE_SIZE},
            {3,3},
            {5,5}
    };

    private final transient int[][] spawnPoints = {
            {6 * TILE_SIZE,4 * TILE_SIZE},
            {5 * TILE_SIZE, 10 * TILE_SIZE},
            {14 * TILE_SIZE, 54 * TILE_SIZE},
            {TILE_SIZE, TILE_SIZE}
    };

    public Player player;
    public AssetSetter aSetter;
    public transient Thread gameThread;
    public transient TileManager tileman;
    private CopyOnWriteArrayList<Entity> entities;
    public final transient InputHandler inpkez;
    public final transient ConsoleHandler console;
    public final transient CollisionChecker cChecker;
    public final transient MouseHandler mouseHandler;
    public final transient UserInterface userInterface;
    private static final String LOG_CONTEXT = "[GAME PANEL]";

    public enum GameState {
        START, DIFFICULTY_SCREEN, GAME_MODE_SCREEN,
        RUNNING, PAUSED, FINISHED_LOST, FINISHED_WON,
        SAVE, LOAD, CONSOLE_INPUT
    }
    public enum GameDifficulty {EASY, MEDIUM, HARD, IMPOSSIBLE}
    public enum GameMode {NONE, STORY, CUSTOM}

    private GameState gameState;
    private GameDifficulty difficulty;
    private GameMode gameMode = GameMode.NONE;


    public GameState getGameState(){return gameState;}
    public GameDifficulty getGameDifficulty(){return difficulty;}
    public GameMode getGameMode(){return gameMode;}
    public int getFPS() {return FPS;}
    public int getTileSize() {return TILE_SIZE;}
    public int getScreenWidth() {return MAX_SCREEN_COL * TILE_SIZE;} //768 pixel
    public int getScreenHeight() {return MAX_SCREEN_ROW * TILE_SIZE;} //576 pixel
    public int getMaxWorldCol() {return MAX_WORLD_COL;}
    public int getMaxWorldRow() {return MAX_WORLD_ROW;}
    public int getWorldWidth() {return MAX_WORLD_COL * TILE_SIZE;}
    public int getWorldHeight() {return MAX_WORLD_ROW * TILE_SIZE;}
    public CopyOnWriteArrayList<Entity> getEntity() {return entities;}

    public void setGameState(GameState state){gameState = state;}
    public void setGameMode(GameMode mode){gameMode = mode;}
    public void setGameDifficulty(GameDifficulty diff){difficulty = diff;}
    public void setEntities(CopyOnWriteArrayList<Entity> entities){this.entities = entities;}

    public void addEntity(Entity ent){
        entities.add(ent);
    }
    public void addObject(SuperObject obj) {aSetter.list.add(obj);}
    public void removeEnemy(Entity ent) {entities.remove(ent);}

    public GamePanel() {
        GameLogger.info(LOG_CONTEXT, "|INITIALIZING GAMEPANEL|");
        inpkez = new InputHandler(this);
        player = new Player(this,inpkez);
        entities = new CopyOnWriteArrayList<>();
        aSetter = new AssetSetter(this);
        cChecker=new CollisionChecker(this);
        userInterface = new UserInterface(this);
        mouseHandler=new MouseHandler(this);
        tileman = new TileManager(this);
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
        tileman.loadStoryMap(true);
        aSetter.loadLevelAssets(true);
        aSetter.setNPC();
    }

    public static void setupCustomMode(){
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
                gameState=GameState.FINISHED_LOST;
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
            checkLevelCompletion();
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

    public void startGame() {
        entities.clear();
        aSetter.list.clear();
        player = new Player(this, inpkez);
        if(gameMode.equals(GameMode.STORY)) {
            setupStoryMode();
            gameMode = GameMode.STORY;
        }
        else if(gameMode.equals(GameMode.CUSTOM)) {
            setupCustomMode();
            gameMode = GameMode.CUSTOM;
        }
    }

    public boolean isPlayerWithinRadius() {
        double distance = Math.sqrt(Math.pow(player.getWorldX() - endPoints[currentStoryLevel][0], 2) + Math.pow(player.getWorldY() - endPoints[currentStoryLevel][1], 2));
        return distance < (3 * TILE_SIZE);
    }

    public void checkLevelCompletion() {
        if(!isPlayerWithinRadius()) {
            return;
        }
        if(currentStoryLevel < MAX_STORY_LEVEL) {
            currentStoryLevel++;
            tileman.loadStoryMap(false);
            aSetter.loadLevelAssets(false);
            player.setWorldX(spawnPoints[currentStoryLevel][0]);
            player.setWorldY(spawnPoints[currentStoryLevel][1]);
        } else{
            gameState = GameState.FINISHED_WON;
        }
    }

}