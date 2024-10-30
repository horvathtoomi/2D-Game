package main;

import main.logger.GameLogger;

import java.awt.event.*;

public class MouseHandler implements MouseListener, MouseMotionListener {
    private final GamePanel gp;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (gp.getGameState()) {
            case START -> gp.ui.handleStartScreenClick(e.getPoint());
            case DIFFICULTY_SCREEN -> gp.ui.handleDifficultyScreenClick(e.getPoint());
            case FINISHED -> gp.ui.handleGameOverClick(e.getPoint());
            case PAUSED -> gp.ui.handlePauseScreenClick(e.getPoint());
            default -> GameLogger.error("[MOUSE HANDLER]","Unexpected Error", new IllegalArgumentException("State not found"));
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
        gp.ui.handleHover(e.getPoint());
    }

}