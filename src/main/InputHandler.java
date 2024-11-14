package main;

import main.console.ConsoleHandler;
import main.logger.GameLogger;
import serializable.FileManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    Engine gp;
    public boolean upPressed, downPressed, leftPressed,rightPressed, attackPressed;
    private final ConsoleHandler consoleHandler;
    private static final String LOG_CONTEXT = "[INPUT HANDLER]";

    public InputHandler(Engine gp) {
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
            case KeyEvent.VK_ESCAPE -> togglePauseState();
            case KeyEvent.VK_ENTER -> toggleMenuState();
            case KeyEvent.VK_L -> {
                if(gp.getGameState() == Engine.GameState.START || gp.getGameState() == Engine.GameState.FINISHED_LOST || gp.getGameState() == Engine.GameState.FINISHED_WON || gp.getGameState() == Engine.GameState.PAUSED)
                    FileManager.loadGame(gp);
                else
                    GameLogger.warn(LOG_CONTEXT, "CAN NOT LOAD GAME WHILE RUNNING");
            }
            case KeyEvent.VK_O -> {
                if(gp.getGameState() == Engine.GameState.RUNNING || gp.getGameState() == Engine.GameState.PAUSED)
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
        if(gp.getGameState() == Engine.GameState.DIFFICULTY_SCREEN) {
            String[] gameModes = {"EASY", "NORMAL", "HARD", "IMPOSSIBLE"};
            switch (mode) {
                case 0 -> startByKey(Engine.GameDifficulty.EASY);
                case 1 -> startByKey(Engine.GameDifficulty.MEDIUM);
                case 2 -> startByKey(Engine.GameDifficulty.HARD);
                case 3 -> startByKey(Engine.GameDifficulty.IMPOSSIBLE);
                default ->
                        GameLogger.error(LOG_CONTEXT, "SOMETHING UNEXPECTED HAPPENED", new IllegalArgumentException("Unexpected parameter"));
            }
            GameLogger.info(LOG_CONTEXT, "|GAME STARTED AS " + gameModes[mode] + "|");
        }
    }

    private void handleQ(){
        if (gp.getGameState() == Engine.GameState.PAUSED) {
            gp.setGameState(Engine.GameState.CONSOLE_INPUT);
            consoleHandler.startConsoleInput();
        }
        else if(gp.getGameState() == Engine.GameState.RUNNING) {
            gp.player.getInventory().drop();
        }
    }

    private void startByKey(Engine.GameDifficulty diff){
        gp.setGameMode(Engine.GameMode.STORY);
        gp.setGameDifficulty(diff);
        gp.startGame();
        gp.setGameState(Engine.GameState.RUNNING);
    }

    private void togglePauseState() {
        switch(gp.getGameState()){
            case RUNNING, CONSOLE_INPUT -> gp.setGameState(Engine.GameState.PAUSED);
            case PAUSED -> gp.setGameState(Engine.GameState.RUNNING);
            case DIFFICULTY_SCREEN -> gp.setGameState(Engine.GameState.GAME_MODE_SCREEN);
            default -> gp.setGameState(Engine.GameState.START);
        }
    }

    private void toggleMenuState() {
        switch(gp.getGameState()){
            case PAUSED -> gp.setGameState(Engine.GameState.RUNNING);
            case START, FINISHED_LOST, FINISHED_WON -> gp.setGameState(Engine.GameState.GAME_MODE_SCREEN);
            case GAME_MODE_SCREEN -> gp.setGameState(Engine.GameState.DIFFICULTY_SCREEN);
            case DIFFICULTY_SCREEN -> {
                gp.setGameMode(Engine.GameMode.STORY);
                gp.setGameState(Engine.GameState.RUNNING);
                gp.setupStoryMode();
            }
        }
    }

}
