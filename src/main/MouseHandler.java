package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * A MouseHandler osztály felelős az egér események kezeléséért.
 * Kezeli a kattintásokat és az egér mozgását a felhasználói felületen.
 */
public class MouseHandler implements MouseListener, MouseMotionListener {
    private final Engine eng;

    /**
     * Létrehoz egy új egér esemény kezelőt.
     * @param eng a játékmotor példánya
     */
    public MouseHandler(Engine eng) {
        this.eng = eng;
    }

    /**
     * Kezeli az egér kattintás eseményeket a játék különböző állapotaiban.
     * @param e az egér esemény
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        switch (eng.getGameState()) {
            case START -> eng.userInterface.handleClick(e.getPoint(), UserInterface.startScreenButtons);
            case GAME_MODE_SCREEN -> eng.userInterface.handleClick(e.getPoint(), UserInterface.modeScreenButtons);
            case DIFFICULTY_SCREEN -> eng.userInterface.handleDifficultyScreenClick(e.getPoint());
            case FINISHED_LOST -> eng.userInterface.handleClick(e.getPoint(), UserInterface.endScreenButtons);
            case PAUSED -> eng.userInterface.handleClick(e.getPoint(), UserInterface.pauseScreenButtons);
            case RUNNING -> eng.player.attack();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        eng.userInterface.handleHover(e.getPoint());
    }
}