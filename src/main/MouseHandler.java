package main;

import java.awt.event.*;

public class MouseHandler implements MouseListener, MouseMotionListener {
    private final GamePanel gp;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (gp.gameState) {
            case START -> gp.ui.handleStartScreenClick(e.getPoint());
            case FINISHED -> gp.ui.handleGameOverClick(e.getPoint());
            case PAUSED -> gp.ui.handlePauseScreenClick(e.getPoint());
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