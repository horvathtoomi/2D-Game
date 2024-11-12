package map;

import main.logger.GameLogger;
import tile.TileManager;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class GUIMapGenerator extends JFrame {
    private static final String LOG_CONTEXT = "[GUI MAP GENERATOR]";
    private final int WINDOW_WIDTH = 300;
    private final int WINDOW_HEIGHT = 150;

    public GUIMapGenerator() {
        setTitle("Custom Map Options");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton chooseMapButton = new JButton("Choose Existing Map");
        JButton generateMapButton = new JButton("Generate New Map");

        // Style buttons
        chooseMapButton.setPreferredSize(new Dimension(200, 40));
        generateMapButton.setPreferredSize(new Dimension(200, 40));

        // Add choose map button
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(chooseMapButton, gbc);

        // Add generate map button
        gbc.gridy = 1;
        add(generateMapButton, gbc);

        // Choose Map Button Action
        chooseMapButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Map File");
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
                }

                @Override
                public String getDescription() {
                    return "Text Files (*.txt)";
                }
            });

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    TileManager.loadCustomMap(selectedFile.getAbsolutePath());
                    GameLogger.info(LOG_CONTEXT, "Custom map loaded successfully: " + selectedFile.getName());
                    dispose();
                } catch (Exception ex) {
                    GameLogger.error(LOG_CONTEXT, "Failed to load custom map", ex);
                    JOptionPane.showMessageDialog(this,
                            "Error loading map: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Generate Map Button Action
        generateMapButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Image File");
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
                }

                @Override
                public String getDescription() {
                    return "PNG Images (*.png)";
                }
            });

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    MapGenerator.processImage(selectedFile.getAbsolutePath());
                    GameLogger.info(LOG_CONTEXT, "Map generated successfully from: " + selectedFile.getName());
                    TileManager.loadCustomMap(null);
                    dispose();
                } catch (Exception ex) {
                    GameLogger.error(LOG_CONTEXT, "Failed to generate map", ex);
                    JOptionPane.showMessageDialog(this,
                            "Error generating map: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}