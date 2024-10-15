package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle("2D game");
        GamePanel panel = new GamePanel();
        window.add(panel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        int option = JOptionPane.showConfirmDialog(window,
                "Would you like to load a saved game?",
                "Load Game",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (!panel.loadGame()) {
                // If load fails, start a new game
                panel.setupGame();
            }
        } else {
            panel.setupGame();
        }
        panel.startGameThread();
    }
}