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
            case START -> eng.userInterface.handleStartScreenClick(e.getPoint());
            case GAME_MODE_SCREEN -> eng.userInterface.handleGameModeScreenClick(e.getPoint());
            case DIFFICULTY_SCREEN -> eng.userInterface.handleDifficultyScreenClick(e.getPoint());
            case FINISHED_LOST -> eng.userInterface.handleGameOverClick(e.getPoint());
            case PAUSED -> eng.userInterface.handlePauseScreenClick(e.getPoint());
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

    /**
     * Kezeli az egér mozgás eseményeket.
     * Frissíti a felhasználói felület hover effektusait.
     * @param e az egér esemény
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        eng.userInterface.handleHover(e.getPoint());
    }

}