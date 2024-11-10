package map;

import main.logger.GameLogger;
import tile.TileManager;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GUIMapGenerator extends JFrame {
    private JTextField inputField;
    private JButton generateButton;
    private JButton browserButton;
    private static final String LOG_CONTEXT = "[GUI MAP GENERATOR]";

    public GUIMapGenerator() {
        setTitle("Generate Custom Map");
        setSize(400, 110);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        inputField = new JTextField(20);
        inputField.setToolTipText("Enter image path or use Browse button");

        generateButton = new JButton("Generate");
        generateButton.setEnabled(false);
        browserButton = new JButton("Browse Image");

        JPanel centerPanel = new JPanel(new FlowLayout());
        centerPanel.add(new JLabel("Image Path: "));
        centerPanel.add(inputField);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(generateButton);
        bottomPanel.add(browserButton);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(new GenerateButtonActionListener());
        browserButton.addActionListener(new BrowserButtonActionListener());
        inputField.getDocument().addDocumentListener(new InputFieldDocumentListener());
    }

    private class GenerateButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String imagePath = inputField.getText();
            File imageFile = new File(imagePath);

            if (!imageFile.exists() || !imageFile.isFile()) {
                JOptionPane.showMessageDialog(GUIMapGenerator.this,
                        "Invalid image file path",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                MapGenerator.processImage(imagePath);
                GameLogger.info(LOG_CONTEXT, "Map generated successfully from image: " + imagePath);

                // Load the newly generated map
                int mapNumber = MapGenerator.getNextMapNumber() - 1;  // Get the number of the map we just created
                String mapPath = "maps/map_matrices/map" + mapNumber + ".txt";
                TileManager.loadCustomMap(mapPath);

                dispose();  // Close the window after successful generation
            } catch (Exception ex) {
                GameLogger.error(LOG_CONTEXT, "Failed to generate map from image: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(GUIMapGenerator.this,
                        "Error generating map: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class BrowserButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Image File");
            fileChooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
                }

                public String getDescription() {
                    return "PNG Images (*.png)";
                }
            });

            int result = fileChooser.showOpenDialog(GUIMapGenerator.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                inputField.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    private class InputFieldDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateGenerateButton();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateGenerateButton();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateGenerateButton();
        }

        private void updateGenerateButton() {
            String path = inputField.getText();
            generateButton.setEnabled(!path.isEmpty() && new File(path).exists());
        }
    }
}