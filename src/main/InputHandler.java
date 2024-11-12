package main;

import main.console.ConsoleHandler;
import main.logger.GameLogger;
import serializable.FileManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed,rightPressed, attackPressed;
    private final ConsoleHandler consoleHandler;
    private static final String LOG_CONTEXT = "[INPUT HANDLER]";

    public InputHandler(GamePanel gp) {
        this.gp =gp;
        consoleHandler = new ConsoleHandler(gp);
    }

    @Override
    public void keyTyped(KeyEvent e) {}


    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_E -> attackPressed = true;
            case KeyEvent.VK_F -> gp.player.getInventory().rotate();
            case KeyEvent.VK_Q -> handleQ();
            case KeyEvent.VK_ESCAPE -> {
                if (gp.getGameState() == GamePanel.GameState.SAVE || gp.getGameState() == GamePanel.GameState.LOAD)
                    gp.setGameState(GamePanel.GameState.PAUSED);
                else
                    togglePauseState();
            }
            case KeyEvent.VK_ENTER -> toggleMenuState();
            case KeyEvent.VK_L -> {
                if(gp.getGameState() == GamePanel.GameState.START || gp.getGameState() == GamePanel.GameState.FINISHED || gp.getGameState() == GamePanel.GameState.PAUSED)
                    FileManager.loadGame(gp);
                else
                    GameLogger.warn(LOG_CONTEXT, "CAN NOT LOAD GAME WHILE RUNNING");
            }
            case KeyEvent.VK_O -> {
                if(gp.getGameState() == GamePanel.GameState.RUNNING || gp.getGameState() == GamePanel.GameState.PAUSED)
                    FileManager.saveGame(gp);
                else
                    GameLogger.warn(LOG_CONTEXT, "You are not running the game yet!");
            }
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
                gp.player.isAttacking = false;
            }
        }
    }

    private void modeFinder(int mode){
        if(gp.getGameState() == GamePanel.GameState.DIFFICULTY_SCREEN) {
            String[] gameModes = {"EASY", "NORMAL", "HARD", "IMPOSSIBLE"};
            switch (mode) {
                case 0 -> startByKey(GamePanel.GameDifficulty.EASY);
                case 1 -> startByKey(GamePanel.GameDifficulty.MEDIUM);
                case 2 -> startByKey(GamePanel.GameDifficulty.HARD);
                case 3 -> startByKey(GamePanel.GameDifficulty.IMPOSSIBLE);
                default ->
                        GameLogger.error(LOG_CONTEXT, "SOMETHING UNEXPECTED HAPPENED", new IllegalArgumentException("Unexpected parameter"));
            }
            GameLogger.info(LOG_CONTEXT, "|GAME RESTARTED AS " + gameModes[mode] + "|");
        }
    }

    private void handleQ(){
        if (gp.getGameState() == GamePanel.GameState.PAUSED) {
            gp.setGameState(GamePanel.GameState.CONSOLE_INPUT);
            consoleHandler.startConsoleInput();
        }
        else if(gp.getGameState() == GamePanel.GameState.RUNNING) {
            gp.player.getInventory().drop();
        }
    }

    private void startByKey(GamePanel.GameDifficulty diff){
        gp.setGameDifficulty(diff);
        gp.startGame();
        gp.setGameState(GamePanel.GameState.RUNNING);
    }

    private void togglePauseState() {
        if (gp.getGameState() == GamePanel.GameState.RUNNING) {
            gp.setGameState(GamePanel.GameState.PAUSED);
        }
        else if (gp.getGameState() == GamePanel.GameState.PAUSED) {
            gp.setGameState(GamePanel.GameState.RUNNING);
        }
        else if(gp.getGameState()==GamePanel.GameState.DIFFICULTY_SCREEN){
            gp.setGameState(GamePanel.GameState.START);
        }
    }

    private void toggleMenuState() {
        if (gp.getGameState() == GamePanel.GameState.PAUSED){
            gp.setGameState(GamePanel.GameState.RUNNING);
        }
        else if(gp.getGameState()==GamePanel.GameState.FINISHED || gp.getGameState()==GamePanel.GameState.START) {
            gp.setGameState(GamePanel.GameState.DIFFICULTY_SCREEN);
        }
    }

}
