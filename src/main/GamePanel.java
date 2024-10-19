package main;

import entity.*;
import object.*;
import serializable.FileManager;
import tile.TileManager;

import javax.swing.*;
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

    public enum GameState{START,RUNNING,PAUSED,FINISHED,SAVE_DIALOG, LOAD_DIALOG, SAVE, LOAD} //Game State
    public GameState gameState;
    public String currentInputText = "";
    public boolean saveLoadSuccess = false;
    public String saveLoadMessage = "";

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
        System.out.println("---------------");
        System.out.println("|Saving pending|");
        System.out.println("---------------");

        gameState = GameState.SAVE;

        // GETTING FILENAME
        String filename = JOptionPane.showInputDialog(this, "Enter a name for your save file:");

        if (filename != null && !filename.trim().isEmpty()) {
            try {
                File saveDir = new File("res/save");
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                File saveFile = new File(saveDir, filename + ".dat");

                if (saveFile.exists()) {
                    int choice = JOptionPane.showConfirmDialog(this,
                            "A save file with this name already exists. Do you want to overwrite it?",
                            "Overwrite Save",
                            JOptionPane.YES_NO_OPTION);
                    if (choice != JOptionPane.YES_OPTION) {
                        System.out.println("Save cancelled.");
                        gameState = GameState.RUNNING;
                        return;
                    }
                }

                FileManager.saveGameState(this, saveFile.getPath());
                System.out.println("Game saved successfully.");
                JOptionPane.showMessageDialog(this, "Game saved successfully.");
            } catch (IOException e) {
                System.err.println("Error saving game: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Save cancelled.");
        }

        gameState = GameState.RUNNING;
    }

    public void loadGame() {
        System.out.println("-------------");
        System.out.println("|Load pending|");
        System.out.println("-------------");
        gameState = GameState.LOAD;

        // GETTING FILENAME
        File saveDir = new File("res/save");
        if (!saveDir.exists() || saveDir.list() == null || Objects.requireNonNull(saveDir.list()).length == 0) {
            JOptionPane.showMessageDialog(this, "No save files found.", "Load Game", JOptionPane.INFORMATION_MESSAGE);
            gameState = GameState.RUNNING;
        }

        String[] saveFiles = saveDir.list((dir, name) -> name.endsWith(".dat"));
        assert saveFiles != null;
        String selectedFile = (String) JOptionPane.showInputDialog(
                this,
                "Choose a save file to load:",
                "Load Game",
                JOptionPane.QUESTION_MESSAGE,
                null,
                saveFiles,
                saveFiles[0]);

        if (selectedFile != null) {
            try {
                FileManager.loadGameState(this, new File(saveDir, selectedFile).getPath());
                System.out.println("Game loaded successfully.");
                JOptionPane.showMessageDialog(this, "Game loaded successfully.");
                gameState = GameState.RUNNING;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading game: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        else
            System.out.println("Load cancelled.");
        gameState = GameState.RUNNING;
    }


    public void resetGame() {
        entities.clear();
        aSetter.list.clear();
        player = new Player(this, inpkez);
        setupGame();
    }

    public void processInput(char c) {
        if (c == '\b') {  // Backspace
            if (!currentInputText.isEmpty()) {
                currentInputText = currentInputText.substring(0, currentInputText.length() - 1);
            }
        } else {
            currentInputText += c;
        }
    }

}