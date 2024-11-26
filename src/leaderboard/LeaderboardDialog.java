package leaderboard;

import main.Engine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class LeaderboardDialog extends JDialog {
    private final Engine engine;
    private final JTabbedPane tabbedPane;
    private static final String[] COLUMN_NAMES = {
            "Rank", "Player", "Score", "Time", "Enemies Defeated", "Final Health"
    };

    public LeaderboardDialog(Engine engine, JFrame parent) {
        super(parent, "Leaderboard", true);
        this.engine = engine;

        setSize(800, 600);
        setLocationRelativeTo(parent);
        setResizable(false);

        tabbedPane = new JTabbedPane();
        initializeTabs();

        add(tabbedPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (engine.getGameState() == Engine.GameState.PAUSED) {
                    engine.setGameState(Engine.GameState.RUNNING);
                }
                dispose();
            }
        });
    }

    private void initializeTabs() {
        for (Engine.GameDifficulty difficulty : Engine.GameDifficulty.values()) {
            JPanel panel = createDifficultyPanel(difficulty);
            tabbedPane.addTab(difficulty.toString(), panel);
        }
    }

    private JPanel createDifficultyPanel(Engine.GameDifficulty difficulty) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));

        DefaultTableModel model = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(40, 40, 40));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(70, 70, 70));
        table.getTableHeader().setBackground(new Color(60, 60, 60));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(80, 80, 80));
        table.setSelectionForeground(Color.WHITE);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // Rank
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Player
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Score
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Time
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Enemies
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Health

        updateTableData(model, difficulty);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void updateTableData(DefaultTableModel model, Engine.GameDifficulty difficulty) {
        model.setRowCount(0);
        List<LeaderboardEntry> entries = LeaderboardManager.getInstance()
                .getEntriesForDifficulty(difficulty);

        int rank = 1;
        for (LeaderboardEntry entry : entries) {
            model.addRow(new Object[]{
                    rank++,
                    entry.getPlayerName(),
                    entry.getScore(),
                    entry.getFormattedTime(),
                    entry.getEnemiesDefeated(),
                    entry.getFinalHealth()
            });
        }
    }

    public void showDialog() {
        if (engine.getGameState() == Engine.GameState.RUNNING) {
            engine.setGameState(Engine.GameState.PAUSED);
        }
        setVisible(true);
    }
}