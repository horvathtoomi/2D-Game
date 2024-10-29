package main;

import main.console.ConsoleHandler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed,rightPressed, attackPressed;
    private final ConsoleHandler consoleHandler;

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
            case KeyEvent.VK_F -> gp.player.switchWeapon();
            case KeyEvent.VK_Q -> {
                if (gp.gameState == GamePanel.GameState.PAUSED) {
                    gp.gameState = GamePanel.GameState.CONSOLE_INPUT;
                    consoleHandler.startConsoleInput();
                }
            }
            case KeyEvent.VK_ESCAPE -> {
                if (gp.gameState == GamePanel.GameState.SAVE || gp.gameState == GamePanel.GameState.LOAD)
                    gp.gameState = GamePanel.GameState.PAUSED;
                else
                    togglePauseState();
            }
            case KeyEvent.VK_ENTER -> toggleMenuState();
            case KeyEvent.VK_L -> {
                if(gp.gameState==GamePanel.GameState.START||gp.gameState==GamePanel.GameState.FINISHED||gp.gameState==GamePanel.GameState.PAUSED)
                    gp.loadGame();
                else
                    System.out.println("Can not load game while running");
            }
            case KeyEvent.VK_O -> {
                if(gp.gameState==GamePanel.GameState.RUNNING||gp.gameState==GamePanel.GameState.PAUSED)
                    gp.saveGame();
                else
                    System.out.println("You are not running the game yet!");
            }
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
            case KeyEvent.VK_E -> attackPressed = false;
        }
    }

    private void togglePauseState() {
        if (gp.gameState == GamePanel.GameState.RUNNING) {
            gp.gameState = GamePanel.GameState.PAUSED;
        }
        else if (gp.gameState == GamePanel.GameState.PAUSED) {
            gp.gameState = GamePanel.GameState.RUNNING;
        }
        else if(gp.gameState==GamePanel.GameState.DIFFICULTY_SCREEN){
            gp.gameState=GamePanel.GameState.START;
        }
    }

    private void toggleMenuState() {
        if (gp.gameState == GamePanel.GameState.PAUSED){
            gp.gameState = GamePanel.GameState.RUNNING;
        }
        else if(gp.gameState==GamePanel.GameState.FINISHED || gp.gameState==GamePanel.GameState.START) {
            gp.gameState = GamePanel.GameState.DIFFICULTY_SCREEN;
        }
    }

}
