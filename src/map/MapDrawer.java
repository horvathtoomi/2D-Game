package map;

import main.UtilityTool;
import main.logger.GameLogger;
import tile.TileManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class MapDrawer extends JFrame {

    private final PaintPanel paintPanel;
    private ResourcePanel resourcePanel;

    private Color currentColor = Color.BLACK;
    private int brushSize = 16;
    private Tool currentTool = Tool.BRUSH;

    private static final String LOG_CONTEXT = "[MAP DRAWER]";

    public enum Tool {
        BRUSH, BUCKET, ERASER
    }

    public MapDrawer() {
        super("Map Drawer");
        setTitle("Map Drawer - Create Custom Map");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Menu Panel (Save/Load/Open + Tools)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(createMenuPanel(), BorderLayout.NORTH);
        topPanel.add(createToolPanel(), BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Center: Paint Area (Scrollable)
        paintPanel = new PaintPanel();
        JScrollPane paintScrollPane = new JScrollPane(paintPanel);
        paintScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        paintScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        paintScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        paintScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        // Right: Resource Selector (Scrollable)
        resourcePanel = new ResourcePanel(this);
        JScrollPane resourceScrollPane = new JScrollPane(resourcePanel);
        resourceScrollPane.setPreferredSize(new Dimension(400, 0));
        resourceScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resourceScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resourceScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paintScrollPane, resourceScrollPane);
        splitPane.setResizeWeight(0.75);
        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEtchedBorder());

        JButton openButton = new JButton("Open (Play)");
        JButton saveButton = new JButton("Save Map");
        JButton loadButton = new JButton("Load Map");
        JButton exitButton = new JButton("Exit");

        // Gombok eseménykezelői
        openButton.addActionListener(_ -> openMapAction());
        saveButton.addActionListener(_ -> saveMapAction());
        loadButton.addActionListener(_ -> loadMapAction());
        exitButton.addActionListener(_ -> dispose());

        // Stílus kiemelés az Open gombnak
        openButton.setFont(new Font("Arial", Font.BOLD, 12));
        openButton.setBackground(new Color(200, 255, 200));

        panel.add(openButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(exitButton);

        return panel;
    }

    private JPanel createToolPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(230, 230, 230));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // Eszköz választó gombok
        JToggleButton brushBtn = new JToggleButton("Ecset", true);
        JToggleButton bucketBtn = new JToggleButton("Vödör");
        JToggleButton eraserBtn = new JToggleButton("Radír");

        ButtonGroup group = new ButtonGroup();
        group.add(brushBtn);
        group.add(bucketBtn);
        group.add(eraserBtn);

        brushBtn.addActionListener(_ -> currentTool = Tool.BRUSH);
        bucketBtn.addActionListener(_ -> currentTool = Tool.BUCKET);
        eraserBtn.addActionListener(_ -> currentTool = Tool.ERASER);

        JLabel sizeLabel = new JLabel("Méret: 16");
        JSlider sizeSlider = new JSlider(1, 64, 16);
        sizeSlider.setPreferredSize(new Dimension(150, 20));
        sizeSlider.addChangeListener(_ -> {
            brushSize = sizeSlider.getValue();
            sizeLabel.setText("Méret: " + brushSize);
        });

        panel.add(new JLabel("Eszközök:"));
        panel.add(brushBtn);
        panel.add(bucketBtn);
        panel.add(eraserBtn);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(sizeLabel);
        panel.add(sizeSlider);

        return panel;
    }

    /**
     * Ez a metódus végzi az automatikus mentést, konvertálást és betöltést.
     */
    private void openMapAction() {
        try {
            // 1. Lépés: Az aktuális rajz mentése egy ideiglenes fájlba
            File tempDir = new File("res/maps/temp");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            File tempImageFile = new File(tempDir, "temp_drawn_map.png");
            ImageIO.write(paintPanel.getCanvas(), "png", tempImageFile);

            GameLogger.info(LOG_CONTEXT, "Temporary map image saved: " + tempImageFile.getAbsolutePath());

            // 2. Lépés: Kiszámítjuk mi lesz a következő ID, amit a MapGenerator generálni fog
            // Erre azért van szükség, hogy tudjuk melyik TXT fájlt kell betölteni
            int nextMapId = MapGenerator.getNextMapNumber();

            // 3. Lépés: A MapGenerator feldolgozza a képet és létrehozza a .txt mátrixot
            MapGenerator.processImage(tempImageFile.getAbsolutePath());

            // 4. Lépés: Összeállítjuk a generált TXT fájl útvonalát
            // A MapGenerator logikája alapján: res/maps/map_matrices/map{SZAM}.txt
            String generatedMapPath = "res/maps/map_matrices/map" + nextMapId + ".txt";

            GameLogger.info(LOG_CONTEXT, "Loading generated map matrix: " + generatedMapPath);

            // 5. Lépés: Betöltjük a pályát a játékba
            TileManager.loadCustomMap(generatedMapPath);

            // 6. Lépés: Bezárjuk a szerkesztőt
            dispose();

        } catch (Exception ex) {
            GameLogger.error(LOG_CONTEXT, "Failed to generate and open map", ex);
            JOptionPane.showMessageDialog(this, "Critical error while processing map: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMapAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Map as PNG");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }
            try {
                ImageIO.write(paintPanel.getCanvas(), "png", fileToSave);
                JOptionPane.showMessageDialog(this, "Map saved successfully!");
                GameLogger.info(LOG_CONTEXT, "Map saved: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                GameLogger.error(LOG_CONTEXT, "Error saving image", ex);
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
            }
        }
    }

    private void loadMapAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Map PNG");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage loadedImage = ImageIO.read(selectedFile);
                if (loadedImage != null) {
                    paintPanel.setCanvas(loadedImage);
                }
            } catch (IOException ex) {
                GameLogger.error(LOG_CONTEXT, "Error loading image", ex);
                JOptionPane.showMessageDialog(this, "Error loading: " + ex.getMessage());
            }
        }
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public Tool getCurrentTool() {
        return currentTool;
    }
}

/**
 * A panel ahol a felhasználó rajzolhat.
 */
class PaintPanel extends JPanel implements MouseMotionListener, MouseListener {
    private BufferedImage canvas;
    private static final int CANVAS_WIDTH = 100 * 16;
    private static final int CANVAS_HEIGHT = 100 * 16;

    public PaintPanel() {
        init();
    }

    private void init() {
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        setBackground(Color.WHITE);
        // Canvas inicializálása
        canvas = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = canvas.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        g2.dispose();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public BufferedImage getCanvas() {
        return canvas;
    }

    public void setCanvas(BufferedImage newImage) {
        Graphics2D g2 = canvas.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        g2.drawImage(newImage, 0, 0, null);
        g2.dispose();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }

    private void useTool(MouseEvent e, boolean isDrag) {
        MapDrawer frame = (MapDrawer) SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;

        MapDrawer.Tool tool = frame.getCurrentTool();

        if (tool == MapDrawer.Tool.BUCKET) {
            if (!isDrag) {
                floodFill(e.getX(), e.getY(), frame.getCurrentColor());
            }
            return;
        }

        Graphics2D g2 = canvas.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        if (tool == MapDrawer.Tool.ERASER) {
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(frame.getCurrentColor());
        }

        int size = frame.getBrushSize();
        g2.fillOval(e.getX() - size / 2, e.getY() - size / 2, size, size);
        g2.dispose();

        repaint(e.getX() - size, e.getY() - size, size * 2, size * 2);
    }

    private void floodFill(int x, int y, Color fillColor) {
        if (x < 0 || x >= CANVAS_WIDTH || y < 0 || y >= CANVAS_HEIGHT) return;

        int targetRGB = canvas.getRGB(x, y);
        int fillRGB = fillColor.getRGB();

        if (targetRGB == fillRGB) return;

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.poll();

            if (p.x < 0 || p.x >= CANVAS_WIDTH || p.y < 0 || p.y >= CANVAS_HEIGHT) continue;
            if (canvas.getRGB(p.x, p.y) != targetRGB) continue;

            canvas.setRGB(p.x, p.y, fillRGB);

            queue.add(new Point(p.x + 1, p.y));
            queue.add(new Point(p.x - 1, p.y));
            queue.add(new Point(p.x, p.y + 1));
            queue.add(new Point(p.x, p.y - 1));
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        useTool(e, false);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        useTool(e, true);
    }

    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

/**
 * A jobb oldali panel, ami listázza az elérhető Tile-okat.
 */
class ResourcePanel extends JPanel {

    private final MapDrawer mapDrawer;
    private final String LOG_CONTEXT = "[RESOURCE PANEL]";

    public ResourcePanel(MapDrawer mapDrawer) {
        this.mapDrawer = mapDrawer;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.DARK_GRAY);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        loadAndDisplayResources();
    }

    private void loadAndDisplayResources() {
        String[] tileNames = {
                "Wall", "Grass", "Earth", "Sand", "Water",
                "Black Border", "Black Sand", "Dead Bush", "Cactus", "Tree",
                "Gravel", "Lava"
        };

        String[] imageNames = {
                "wall", "grass", "earth", "sand", "water",
                "blackborder", "blacksand", "deadbush", "cactus", "tree",
                "gravel", "lava"
        };

        java.util.List<TileColor> colors = ColorAnalyzer.getTileColors();
        UtilityTool uTool = new UtilityTool();

        for (TileColor tc : colors) {
            int id = tc.tileNumber;
            if (id >= tileNames.length) continue;

            String name = tileNames[id];
            String imgName = imageNames[id];
            Color representativeColor = new Color(tc.r, tc.g, tc.b);

            try {
                BufferedImage rawImg = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tiles/" + imgName + ".png")));
                BufferedImage scaledImg = uTool.scaleImage(rawImg, 48, 48);
                ImageIcon icon = new ImageIcon(scaledImg);

                add(createTileRow(name, icon, representativeColor));
                add(Box.createRigidArea(new Dimension(0, 10)));

            } catch (IOException | NullPointerException e) {
                GameLogger.error(LOG_CONTEXT, "Failed to load resource: " + imgName, e);
            }
        }
    }

    private JPanel createTileRow(String name, ImageIcon icon, Color color) {
        JPanel row = new JPanel();
        row.setLayout(new GridBagLayout());
        row.setBackground(Color.GRAY);
        row.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        row.setMaximumSize(new Dimension(350, 90));
        row.setPreferredSize(new Dimension(350, 90));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        row.add(nameLabel, gbc);

        // Image Button
        JButton imgBtn = new JButton(icon);
        imgBtn.setBackground(Color.GRAY);
        imgBtn.setFocusPainted(false);
        imgBtn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        imgBtn.addActionListener(e -> selectColor(color, row));

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        row.add(imgBtn, gbc);

        // Arrow
        JLabel arrow = new JLabel("➜");
        arrow.setFont(new Font("Arial", Font.BOLD, 20));
        arrow.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        row.add(arrow, gbc);

        // Color Panel
        JPanel colorDisplay = new JPanel();
        colorDisplay.setPreferredSize(new Dimension(48, 48));
        colorDisplay.setBackground(color);
        colorDisplay.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        colorDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        colorDisplay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectColor(color, row);
            }
        });

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        row.add(colorDisplay, gbc);

        return row;
    }

    private void selectColor(Color c, JPanel rowPanel) {
        mapDrawer.setCurrentColor(c);
        if (mapDrawer.getCurrentTool() == MapDrawer.Tool.ERASER) {
            // Opcionális logika
        }
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }
        }
        rowPanel.setBorder(new LineBorder(Color.YELLOW, 3));
    }
}