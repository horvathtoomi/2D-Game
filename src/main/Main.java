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
        panel.setupGame();
        panel.startGameThread();
    }
}

// TO DO
/*
Fejleszteni UI interfacet:
    1. mentes es betoltes menu
Enemyk halandok legyenek
    + friendly enemy oli a tobbit

 */