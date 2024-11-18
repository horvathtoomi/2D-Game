package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle("2D game");
        Engine engine = new Engine();
        window.add(engine);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        engine.startGameThread();
    }
}