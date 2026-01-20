package map;

import main.logger.GameLogger;
import tile.TileManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

/**
 * A GUIMapGenerator osztály biztosítja a pályagenerálás grafikus felhasználói felületét.
 */
public class GUIMapGenerator extends JFrame {

    private static final String LOG_CONTEXT = "[GUI MAP GENERATOR]";

    /**
     * Létrehoz egy új pályagenerátor ablakot.
     */
    public GUIMapGenerator() {
        setTitle("Custom Map Options");
        int WINDOW_WIDTH = 300;
        int WINDOW_HEIGHT = 150;
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        initializeComponents();
    }

    /**
     * Inicializálja az ablak komponenseit és eseménykezelőit.
     */
    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton chooseMapButton = new JButton("Choose Existing Map");
        JButton generateMapButton = new JButton("Generate New Map");

        chooseMapButton.setPreferredSize(new Dimension(200, 40));
        generateMapButton.setPreferredSize(new Dimension(200, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(chooseMapButton, gbc);

        gbc.gridy = 1;
        add(generateMapButton, gbc);

        chooseMapButton.addActionListener(e -> {
            JFileChooser fileChooser = getFileChooser();

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

        generateMapButton.addActionListener(e -> {
            MapDrawer drawer = new MapDrawer();
        });
    }

    private static JFileChooser getFileChooser() {
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
        return fileChooser;
    }
}