package main;

import entity.Player;
import main.console.ConsoleHandler;
import main.logger.GameLogger;
import object.Shooter;
import serializable.FileManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Az InputHandler osztály felelős a billentyűzet bemenet kezeléséért.
 * Kezeli a játékos irányítását és a játék vezérlőparancsait.
 */
public class InputHandler implements KeyListener {
    Engine eng;
    public boolean upPressed, downPressed, leftPressed,rightPressed, attackPressed;
    private final ConsoleHandler consoleHandler;
    private static final String LOG_CONTEXT = "[INPUT HANDLER]";

    /**
     * Létrehoz egy új bevitel kezelőt.
     * @param eng a játékmotor példánya
     */
    public InputHandler(Engine eng) {
        this.eng = eng;
        consoleHandler = new ConsoleHandler(eng);
    }

    @Override
    public void keyTyped(KeyEvent e) {}


    /**
     * Kezeli a billentyű lenyomás eseményeket.
     * @param e a billentyű esemény
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_E -> attackPressed = true;
            case KeyEvent.VK_F -> eng.player.getInventory().rotate();
            case KeyEvent.VK_R -> {
                if (eng.player.getInventory().getCurrent() instanceof Shooter shooter) {
                    shooter.reload();
                }
            }
            case KeyEvent.VK_Q -> handleQ();
            case KeyEvent.VK_ESCAPE -> togglePauseState();
            case KeyEvent.VK_ENTER -> toggleMenuState();
            case KeyEvent.VK_L -> handleL();
            case KeyEvent.VK_O -> handleO();
            case KeyEvent.VK_1 -> modeFinder(0);
            case KeyEvent.VK_2 -> modeFinder(1);
            case KeyEvent.VK_3 -> modeFinder(2);
            case KeyEvent.VK_4 -> modeFinder(3);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W -> upPressed = false;
            case KeyEvent.VK_S -> downPressed = false;
            case KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_E -> {
                attackPressed = false;
                Player.isAttacking = false;
                Player.shot = false;
            }
        }
    }

    /**
     * Kezeli a játékmód kiválasztását.
     * @param mode a kiválasztott mód indexe
     */
    private void modeFinder(int mode){
        if(eng.getGameState() == GameState.DIFFICULTY_SCREEN) {
            String[] gameModes = {"EASY", "NORMAL", "HARD", "IMPOSSIBLE"};
            switch (mode) {
                case 0 -> startByKey(GameDifficulty.EASY);
                case 1 -> startByKey(GameDifficulty.MEDIUM);
                case 2 -> startByKey(GameDifficulty.HARD);
                case 3 -> startByKey(GameDifficulty.IMPOSSIBLE);
                default ->
                        GameLogger.error(LOG_CONTEXT, "SOMETHING UNEXPECTED HAPPENED", new IllegalArgumentException("Unexpected parameter"));
            }
            GameLogger.info(LOG_CONTEXT, "|GAME STARTED AS " + gameModes[mode] + "|");
        }
    }

    private void handleQ(){
        if (eng.getGameState() == GameState.PAUSED) {
            eng.setGameState(GameState.CONSOLE_INPUT);
            consoleHandler.startConsoleInput();
        }
        else if(eng.getGameState() == GameState.RUNNING) {
            eng.player.getInventory().drop();
        }
    }

    private void handleO() {
        if (eng.getGameState() == GameState.RUNNING || eng.getGameState() == GameState.PAUSED)
            FileManager.saveGame(eng);
        else
            GameLogger.warn(LOG_CONTEXT, "You are not running the game yet!");
    }

    private void handleL(){
        if(eng.getGameState() != GameState.RUNNING)
            FileManager.loadGame(eng);
        else
            GameLogger.warn(LOG_CONTEXT, "CAN NOT LOAD GAME WHILE RUNNING");
    }

    private void startByKey(GameDifficulty diff){
        eng.setGameMode(GameMode.STORY);
        eng.setGameDifficulty(diff);
        eng.startGame();
        eng.setGameState(GameState.RUNNING);
    }

    private void togglePauseState() {
        switch(eng.getGameState()){
            case RUNNING, CONSOLE_INPUT -> eng.setGameState(GameState.PAUSED);
            case PAUSED -> eng.setGameState(GameState.RUNNING);
            case DIFFICULTY_SCREEN -> eng.setGameState(GameState.GAME_MODE_SCREEN);
            default -> eng.setGameState(GameState.START);
        }
    }

    private void toggleMenuState() {
        switch(eng.getGameState()){
            case PAUSED -> eng.setGameState(GameState.RUNNING);
            case START, FINISHED_LOST, FINISHED_WON -> eng.setGameState(GameState.GAME_MODE_SCREEN);
            case GAME_MODE_SCREEN -> eng.setGameState(GameState.DIFFICULTY_SCREEN);
            case DIFFICULTY_SCREEN -> {
                eng.setGameMode(GameMode.STORY);
                eng.setGameState(GameState.RUNNING);
                eng.setupStoryMode();
            }
        }
    }

}
