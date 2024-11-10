package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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


/*
*** TO DO ***
*
* Map valtozatas implementacioja.
* "Story modeot szeretnek" legyen 5 map kb tansitionokkel.
* Legyen free play mode, ahol a user megadja hogy milyen mapot szeretne.
*
 */