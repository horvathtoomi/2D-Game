package main;

import javax.swing.*;
import java.awt.*;
import main.logger.GameLogger;
import serializable.FileManager;

public class SAMPLE extends JFrame {

    Engine gp;

    public SAMPLE() {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(50, 50, 150), 0, getHeight(), new Color(0, 0, 50));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        JLabel startLabel = new JLabel("2D Java Game");
        JLabel modeLabel = new JLabel("Choose Game Mode");
        JLabel difficultyLabel = new JLabel("Choose Difficulty");
        JLabel pauseLabel = new JLabel("Game Paused");
        JLabel lostLabel = new JLabel("You Died!");
        JLabel wontLabel = new JLabel("You won!");

        startLabel.setFont(new Font("Arial", Font.BOLD, 32));
        startLabel.setForeground(Color.WHITE);
        modeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        modeLabel.setForeground(Color.WHITE);
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 32));
        difficultyLabel.setForeground(Color.WHITE);
        pauseLabel.setFont(new Font("Arial", Font.BOLD, 32));
        pauseLabel.setForeground(Color.WHITE);
        lostLabel.setFont(new Font("Arial", Font.BOLD, 32));
        lostLabel.setForeground(Color.WHITE);
        wontLabel.setFont(new Font("Arial", Font.BOLD, 32));
        wontLabel.setForeground(Color.WHITE);

        JButton playButton = createStyledButton("Play");
        JButton loadButton = createStyledButton("Load");
        JButton saveButton = createStyledButton("Save");
        JButton consoleButton = createStyledButton("Console Input");
        JButton exitButton = createStyledButton("Exit");

        playButton.addActionListener(e -> gp.setGameState(Engine.GameState.GAME_MODE_SCREEN));
        saveButton.addActionListener(e -> FileManager.saveGame(gp));
        loadButton.addActionListener(e -> {
            FileManager.loadGame(gp);
            gp.setGameState(Engine.GameState.RUNNING);
        });
        consoleButton.addActionListener(e -> {
            gp.setGameState(Engine.GameState.CONSOLE_INPUT);
            try {
                gp.console.startConsoleInput();
            } catch (Exception err) {
                GameLogger.error("[SAMPLE]", "Console input error: {0}", err.getCause());
            }
        });
        exitButton.addActionListener(e -> System.exit(0));



        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        backgroundPanel.add(startLabel, gbc);

        gbc.gridy++;
        backgroundPanel.add(playButton, gbc);

        gbc.gridy++;
        backgroundPanel.add(loadButton, gbc);

        gbc.gridy++;
        backgroundPanel.add(exitButton, gbc);

        add(backgroundPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 50));
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180)); // Light Steel Blue
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SAMPLE menu = new SAMPLE();
            menu.setVisible(true);
        });
    }
}
