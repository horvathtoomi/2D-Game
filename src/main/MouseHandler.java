package main;

import java.awt.event.*;

public class MouseHandler implements MouseListener, MouseMotionListener {
    private final Engine gp;

    public MouseHandler(Engine gp) {
        this.gp = gp;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (gp.getGameState()) {
            case START -> gp.userInterface.handleStartScreenClick(e.getPoint());
            case GAME_MODE_SCREEN -> gp.userInterface.handleGameModeScreenClick(e.getPoint());
            case DIFFICULTY_SCREEN -> gp.userInterface.handleDifficultyScreenClick(e.getPoint());
            case FINISHED_LOST -> gp.userInterface.handleGameOverClick(e.getPoint());
            case PAUSED -> gp.userInterface.handlePauseScreenClick(e.getPoint());
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
        gp.userInterface.handleHover(e.getPoint());
    }

}