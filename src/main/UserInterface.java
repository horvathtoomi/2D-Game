package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import javax.swing.JFrame;

import leaderboard.LeaderboardDialog;
import main.logger.GameLogger;
import serializable.FileManager;

/**
 * A UserInterface osztály felelős a játék felhasználói felületének
 * megjelenítéséért.
 * Kizárólag rajzolásért és UI input delegálásért felel.
 */
public class UserInterface extends JFrame {

    private final Engine eng;
    private Graphics2D g2;

    // Blurred background for pause/game over screens
    private BufferedImage blurredBackground;
    private boolean needsBlurCapture = false;

    // Title animation
    private float titleGlow = 0f;
    private boolean titleGlowIncreasing = true;

    private final Font titleFont = new Font("Arial", Font.BOLD, 72);
    private final Font subtitleFont = new Font("Arial", Font.PLAIN, 18);
    private final Font defaultFont = new Font("Arial", Font.PLAIN, 40);

    private static final String LOG_CONTEXT = "[USER INTERFACE]";

    // Button lists
    static final ArrayList<Button> startScreenButtons = new ArrayList<>();
    static final ArrayList<Button> modeScreenButtons = new ArrayList<>();
    static final ArrayList<Button> difficultyScreenButtons = new ArrayList<>();
    static final ArrayList<Button> pauseScreenButtons = new ArrayList<>();
    static final ArrayList<Button> endScreenButtons = new ArrayList<>();

    // ===== COLORS =====

    // Start - Deep blue theme
    private static final Color START_TOP = new Color(25, 35, 80);
    private static final Color START_BOTTOM = new Color(5, 10, 30);
    private static final Color START_BTN = new Color(60, 100, 160);
    private static final Color START_BTN_H = new Color(80, 130, 200);

    // Mode - Purple theme
    private static final Color MODE_TOP = new Color(50, 30, 100);
    private static final Color MODE_BOTTOM = new Color(15, 10, 40);
    private static final Color MODE_BTN = new Color(100, 70, 180);
    private static final Color MODE_BTN_H = new Color(130, 100, 220);

    // Difficulty - Teal theme
    private static final Color DIFF_TOP = new Color(20, 60, 80);
    private static final Color DIFF_BOTTOM = new Color(5, 20, 30);
    private static final Color DIFF_BTN = new Color(40, 120, 140);
    private static final Color DIFF_BTN_H = new Color(60, 160, 180);

    // Pause - Green theme
    private static final Color PAUSE_BTN = new Color(50, 130, 70);
    private static final Color PAUSE_BTN_H = new Color(70, 170, 100);

    // Game Over - Red theme
    private static final Color GAMEOVER_BTN = new Color(150, 50, 50);
    private static final Color GAMEOVER_BTN_H = new Color(190, 70, 70);

    // ===================

    public UserInterface(Engine eng) {
        this.eng = eng;
        initializeButtons();
        initializeButtonActions();
    }

    /* ===================== BLUR CAPTURE ===================== */

    /**
     * Call this when transitioning to pause or game over to capture the current
     * frame
     */
    public void requestBlurCapture() {
        needsBlurCapture = true;
    }

    /**
     * Captures the current game state and creates a blurred version
     */
    public void captureAndBlurBackground() {
        if (!needsBlurCapture)
            return;
        needsBlurCapture = false;

        // Create image of current game state
        BufferedImage capture = new BufferedImage(
                eng.getScreenWidth(), eng.getScreenHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D captureG2 = capture.createGraphics();

        // Draw game elements to the capture
        eng.tileman.draw(captureG2);
        for (object.GameObject obj : eng.aSetter.list) {
            obj.draw(captureG2);
        }
        for (entity.Entity entity : eng.getEntity()) {
            entity.draw(captureG2);
        }
        eng.player.draw(captureG2);
        captureG2.dispose();

        // Apply blur
        blurredBackground = applyGaussianBlur(capture, 3);
    }

    private BufferedImage applyGaussianBlur(BufferedImage src, int radius) {
        int size = radius * 2 + 1;
        float weight = 1.0f / (size * size);
        float[] data = new float[size * size];
        for (int i = 0; i < data.length; i++) {
            data[i] = weight;
        }

        Kernel kernel = new Kernel(size, size, data);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        // Apply blur multiple times for stronger effect
        BufferedImage result = src;
        for (int i = 0; i < 3; i++) {
            BufferedImage temp = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
            op.filter(result, temp);
            result = temp;
        }
        return result;
    }

    public void clearBlurredBackground() {
        blurredBackground = null;
    }

    /* ===================== UPDATE ===================== */

    public void update(Point mouse) {
        getActiveButtons().forEach(b -> b.update(mouse));

        // Animate title glow
        if (titleGlowIncreasing) {
            titleGlow += 0.02f;
            if (titleGlow >= 1f)
                titleGlowIncreasing = false;
        } else {
            titleGlow -= 0.02f;
            if (titleGlow <= 0f)
                titleGlowIncreasing = true;
        }
    }

    /* ===================== DRAW ===================== */

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(defaultFont);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (eng.getGameState()) {
            case START -> drawScreen("2D GAME", "Press Start to Begin", START_TOP, START_BOTTOM, startScreenButtons);
            case GAME_MODE_SCREEN ->
                drawScreen("GAME MODE", "Choose Your Adventure", MODE_TOP, MODE_BOTTOM, modeScreenButtons);
            case DIFFICULTY_SCREEN ->
                drawScreen("DIFFICULTY", "Select Challenge Level", DIFF_TOP, DIFF_BOTTOM, difficultyScreenButtons);
            case PAUSED, CONSOLE_INPUT -> drawPauseScreen();
            case FINISHED_LOST, FINISHED_WON -> drawGameOverScreen();
            default -> drawPlayerHealthBar();
        }
    }

    private void drawScreen(String title, String subtitle, Color top, Color bottom, ArrayList<Button> buttons) {
        drawGradientBackground(top, bottom);
        drawDecorations();
        drawTitle(title, subtitle);
        buttons.forEach(b -> b.draw(g2));
    }

    private void drawPauseScreen() {
        // Capture blur if needed
        captureAndBlurBackground();

        // Draw blurred background or fallback to gradient
        if (blurredBackground != null) {
            g2.drawImage(blurredBackground, 0, 0, null);
            // Dark overlay
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, eng.getScreenWidth(), eng.getScreenHeight());
        } else {
            drawGradientBackground(new Color(20, 40, 30), new Color(5, 15, 10));
        }

        // Glass panel effect
        drawGlassPanel();

        // Title
        drawTitle("PAUSED", "Game is paused");

        pauseScreenButtons.forEach(b -> b.draw(g2));
    }

    private void drawGameOverScreen() {
        // Capture blur if needed
        captureAndBlurBackground();

        // Draw blurred background
        if (blurredBackground != null) {
            g2.drawImage(blurredBackground, 0, 0, null);
        }

        // Red/green overlay based on win/loss
        boolean won = eng.getGameState() == GameState.FINISHED_WON;
        Color overlayColor = won ? new Color(0, 80, 40, 180) : new Color(100, 0, 0, 180);
        g2.setColor(overlayColor);
        g2.fillRect(0, 0, eng.getScreenWidth(), eng.getScreenHeight());

        // Glass panel
        drawGlassPanel();

        // Title
        String title = won ? "VICTORY!" : "GAME OVER";
        String subtitle = won ? "Congratulations!" : "Better luck next time";
        drawTitle(title, subtitle);

        endScreenButtons.forEach(b -> b.draw(g2));
    }

    private void drawGlassPanel() {
        int panelWidth = 500;
        int panelHeight = 400;
        int panelX = (eng.getScreenWidth() - panelWidth) / 2;
        int panelY = (eng.getScreenHeight() - panelHeight) / 2 - 40;

        // Panel background
        g2.setColor(new Color(255, 255, 255, 15));
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 30, 30);

        // Panel border
        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 30, 30);
    }

    private void drawDecorations() {
        // Subtle corner decorations
        int sw = eng.getScreenWidth();
        int sh = eng.getScreenHeight();

        g2.setColor(new Color(255, 255, 255, 10));
        g2.setStroke(new BasicStroke(2f));

        // Top left corner
        g2.drawLine(30, 30, 80, 30);
        g2.drawLine(30, 30, 30, 80);

        // Top right corner
        g2.drawLine(sw - 30, 30, sw - 80, 30);
        g2.drawLine(sw - 30, 30, sw - 30, 80);

        // Bottom left corner
        g2.drawLine(30, sh - 30, 80, sh - 30);
        g2.drawLine(30, sh - 30, 30, sh - 80);

        // Bottom right corner
        g2.drawLine(sw - 30, sh - 30, sw - 80, sh - 30);
        g2.drawLine(sw - 30, sh - 30, sw - 30, sh - 80);
    }

    private void drawTitle(String title, String subtitle) {
        int centerX = eng.getScreenWidth() / 2;
        int titleY = eng.getScreenHeight() / 4;

        // Title glow effect
        int glowAlpha = (int) (30 + 20 * titleGlow);
        g2.setFont(titleFont);
        g2.setColor(new Color(255, 255, 255, glowAlpha));
        for (int i = 0; i < 3; i++) {
            int offset = (i + 1) * 2;
            g2.drawString(title, centerX - g2.getFontMetrics().stringWidth(title) / 2 - offset, titleY);
            g2.drawString(title, centerX - g2.getFontMetrics().stringWidth(title) / 2 + offset, titleY);
        }

        // Title shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(title, centerX - g2.getFontMetrics().stringWidth(title) / 2 + 3, titleY + 3);

        // Main title
        g2.setColor(Color.WHITE);
        g2.drawString(title, centerX - g2.getFontMetrics().stringWidth(title) / 2, titleY);

        // Subtitle
        g2.setFont(subtitleFont);
        g2.setColor(new Color(200, 200, 200, 180));
        g2.drawString(subtitle, centerX - g2.getFontMetrics().stringWidth(subtitle) / 2, titleY + 35);

        // Divider line
        int dividerY = titleY + 55;
        int dividerWidth = 200;
        GradientPaint dividerGradient = new GradientPaint(
                centerX - dividerWidth, dividerY, new Color(255, 255, 255, 0),
                centerX, dividerY, new Color(255, 255, 255, 80));
        g2.setPaint(dividerGradient);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(centerX - dividerWidth, dividerY, centerX, dividerY);

        dividerGradient = new GradientPaint(
                centerX, dividerY, new Color(255, 255, 255, 80),
                centerX + dividerWidth, dividerY, new Color(255, 255, 255, 0));
        g2.setPaint(dividerGradient);
        g2.drawLine(centerX, dividerY, centerX + dividerWidth, dividerY);
    }

    private void drawGradientBackground(Color top, Color bottom) {
        g2.setPaint(new GradientPaint(0, 0, top, 0, eng.getScreenHeight(), bottom));
        g2.fillRect(0, 0, eng.getScreenWidth(), eng.getScreenHeight());
    }

    /* ===================== INPUT ===================== */

    protected void handleClick(Point p) {
        for (Button b : getActiveButtons()) {
            if (b.contains(p)) {
                b.doClick();
                break;
            }
        }
    }

    private ArrayList<Button> getActiveButtons() {
        return switch (eng.getGameState()) {
            case START -> startScreenButtons;
            case GAME_MODE_SCREEN -> modeScreenButtons;
            case DIFFICULTY_SCREEN -> difficultyScreenButtons;
            case PAUSED -> pauseScreenButtons;
            case FINISHED_LOST, FINISHED_WON -> endScreenButtons;
            default -> new ArrayList<>();
        };
    }

    /* ===================== HEALTH BAR ===================== */

    // Animated health value
    private float displayedHealth = 100f;
    private float lowHealthGlow = 0f;

    private void drawPlayerHealthBar() {
        int barX = eng.getTileSize();
        int barY = eng.getTileSize();
        int barWidth = 220;
        int barHeight = 24;

        float currentHealth = eng.player.getHealth();
        float maxHealth = eng.player.getMaxHealth();

        // Smooth health animation
        displayedHealth = lerp(displayedHealth, currentHealth, 0.1f);

        // Calculate widths
        int maxW = (int) ((maxHealth / 100.0) * barWidth);
        int curW = (int) ((displayedHealth / 100.0) * barWidth);

        // Low health glow effect
        boolean isLowHealth = currentHealth <= maxHealth * 0.25f;
        lowHealthGlow = lerp(lowHealthGlow, isLowHealth ? 1f : 0f, 0.05f);

        // Draw glow if low health
        if (lowHealthGlow > 0.01f) {
            int glowAlpha = (int) (30 * lowHealthGlow * (0.5f + 0.5f * Math.sin(System.currentTimeMillis() * 0.005)));
            g2.setColor(new Color(255, 50, 50, Math.max(0, glowAlpha)));
            g2.fillRoundRect(barX - 5, barY - 5, maxW + 10, barHeight + 10, 12, 12);
        }

        // Background
        g2.setColor(new Color(30, 30, 30, 200));
        g2.fillRoundRect(barX, barY, maxW, barHeight, 10, 10);

        // Health gradient
        if (curW > 0) {
            GradientPaint healthGradient = new GradientPaint(
                    barX, barY, new Color(220, 60, 60),
                    barX, barY + barHeight, new Color(150, 30, 30));
            g2.setPaint(healthGradient);
            g2.fillRoundRect(barX, barY, curW, barHeight, 10, 10);

            // Highlight
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillRoundRect(barX + 2, barY + 2, curW - 4, barHeight / 3, 8, 8);
        }

        // Border
        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(barX, barY, maxW, barHeight, 10, 10);

        // HP text
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        String hpText = (int) currentHealth + " / " + (int) maxHealth;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(hpText, barX + maxW / 2 - g2.getFontMetrics().stringWidth(hpText) / 2 + 1,
                barY + barHeight / 2 + 5);
        g2.setColor(Color.WHITE);
        g2.drawString(hpText, barX + maxW / 2 - g2.getFontMetrics().stringWidth(hpText) / 2, barY + barHeight / 2 + 4);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    /* ===================== INIT ===================== */

    private void initializeButtons() {
        int w = 200;
        int h = 50;
        int y = eng.getScreenHeight() / 2;
        int cx = eng.getScreenWidth() / 2 - w / 2;

        // START
        addButton(startScreenButtons, cx, y, w, h, "Start Game", START_BTN, START_BTN_H);
        addButton(startScreenButtons, cx, y + 60, w, h, "Load Game", START_BTN, START_BTN_H);
        addButton(startScreenButtons, cx, y + 120, w, h, "Leaderboard", START_BTN, START_BTN_H);
        addButton(startScreenButtons, cx, y + 180, w, h, "Quit", START_BTN, START_BTN_H);

        // MODE
        addButton(modeScreenButtons, cx, y, w, h, "Story Mode", MODE_BTN, MODE_BTN_H);
        addButton(modeScreenButtons, cx, y + 60, w, h, "Custom Map", MODE_BTN, MODE_BTN_H);
        addButton(modeScreenButtons, cx, y + 120, w, h, "Back", MODE_BTN, MODE_BTN_H);

        // DIFFICULTY
        addButton(difficultyScreenButtons, cx - 120, y, w, h, "Easy", DIFF_BTN, DIFF_BTN_H);
        addButton(difficultyScreenButtons, cx - 120, y + 60, w, h, "Medium", DIFF_BTN, DIFF_BTN_H);
        addButton(difficultyScreenButtons, cx + 120, y, w, h, "Hard", DIFF_BTN, DIFF_BTN_H);
        addButton(difficultyScreenButtons, cx + 120, y + 60, w, h, "Impossible", DIFF_BTN, DIFF_BTN_H);

        // PAUSE - Centered layout (moved up)
        addButton(pauseScreenButtons, cx - 120, y - 60, w, h, "Resume", PAUSE_BTN, PAUSE_BTN_H);
        addButton(pauseScreenButtons, cx + 120, y - 60, w, h, "New Game", PAUSE_BTN, PAUSE_BTN_H);
        addButton(pauseScreenButtons, cx - 120, y, w, h, "Console", PAUSE_BTN, PAUSE_BTN_H);
        addButton(pauseScreenButtons, cx + 120, y, w, h, "Save", PAUSE_BTN, PAUSE_BTN_H);
        addButton(pauseScreenButtons, cx - 120, y + 60, w, h, "Leaderboard", PAUSE_BTN, PAUSE_BTN_H);
        addButton(pauseScreenButtons, cx + 120, y + 60, w, h, "Load", PAUSE_BTN, PAUSE_BTN_H);
        addButton(pauseScreenButtons, cx, y + 120, w, h, "Exit", PAUSE_BTN, PAUSE_BTN_H);

        // GAME OVER
        addButton(endScreenButtons, cx, y, w, h, "New Game", GAMEOVER_BTN, GAMEOVER_BTN_H);
        addButton(endScreenButtons, cx, y + 60, w, h, "Load Game", GAMEOVER_BTN, GAMEOVER_BTN_H);
        addButton(endScreenButtons, cx, y + 120, w, h, "Leaderboard", GAMEOVER_BTN, GAMEOVER_BTN_H);
        addButton(endScreenButtons, cx, y + 180, w, h, "Exit", GAMEOVER_BTN, GAMEOVER_BTN_H);
    }

    private void addButton(ArrayList<Button> list, int x, int y, int w, int h,
            String text, Color base, Color hover) {
        list.add(new Button(x, y, w, h, text, base, hover));
    }

    private void initializeButtonActions() {
        // START
        startScreenButtons.get(0).addActionListener(e -> eng.setGameState(GameState.GAME_MODE_SCREEN));
        startScreenButtons.get(1).addActionListener(e -> {
            if (FileManager.loadGame(eng))
                eng.setGameState(GameState.RUNNING);
        });
        startScreenButtons.get(2).addActionListener(e -> new LeaderboardDialog(eng, null).showDialog());
        startScreenButtons.get(3).addActionListener(e -> System.exit(0));

        // MODE
        modeScreenButtons.get(0).addActionListener(e -> {
            eng.setGameMode(GameMode.STORY);
            eng.setGameState(GameState.DIFFICULTY_SCREEN);
        });
        modeScreenButtons.get(1).addActionListener(e -> {
            eng.setGameMode(GameMode.CUSTOM);
            Engine.setupCustomMode();
        });
        modeScreenButtons.get(2).addActionListener(e -> eng.setGameState(GameState.START));

        // DIFFICULTY
        difficultyScreenButtons.get(0).addActionListener(e -> startGame(GameDifficulty.EASY));
        difficultyScreenButtons.get(1).addActionListener(e -> startGame(GameDifficulty.MEDIUM));
        difficultyScreenButtons.get(2).addActionListener(e -> startGame(GameDifficulty.HARD));
        difficultyScreenButtons.get(3).addActionListener(e -> startGame(GameDifficulty.IMPOSSIBLE));

        // PAUSE
        pauseScreenButtons.get(0).addActionListener(e -> {
            clearBlurredBackground();
            eng.setGameState(GameState.RUNNING);
        });
        pauseScreenButtons.get(1).addActionListener(e -> {
            clearBlurredBackground();
            eng.startGame();
        });
        pauseScreenButtons.get(2).addActionListener(e -> {
            eng.setGameState(GameState.CONSOLE_INPUT);
            try {
                eng.console.startConsoleInput();
            } catch (Exception ex) {
                GameLogger.error(LOG_CONTEXT, "Console error", ex);
            }
        });
        pauseScreenButtons.get(3).addActionListener(e -> FileManager.saveGame(eng));
        pauseScreenButtons.get(4).addActionListener(e -> new LeaderboardDialog(eng, null).showDialog());
        pauseScreenButtons.get(5).addActionListener(e -> FileManager.loadGame(eng));
        pauseScreenButtons.get(6).addActionListener(e -> System.exit(0));

        // GAME OVER
        endScreenButtons.get(0).addActionListener(e -> {
            clearBlurredBackground();
            eng.setGameState(GameState.GAME_MODE_SCREEN);
        });
        endScreenButtons.get(1).addActionListener(e -> {
            clearBlurredBackground();
            FileManager.loadGame(eng);
        });
        endScreenButtons.get(2).addActionListener(e -> new LeaderboardDialog(eng, null).showDialog());
        endScreenButtons.get(3).addActionListener(e -> System.exit(0));
    }

    private void startGame(GameDifficulty diff) {
        eng.setGameDifficulty(diff);
        eng.startGame();
        eng.setGameState(GameState.RUNNING);
    }
}
