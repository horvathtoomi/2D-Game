package main.console;

import main.Engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsoleGUI extends JFrame {
    private final JTextPane outputPane;
    private final JTextField inputField;
    private final ConsoleHandler consoleHandler;
    private final ScriptModeDocument document;
    private final BlockingQueue<String> inputQueue;
    private final CommandHistory commandHistory;
    private volatile boolean isScriptMode;
    private static final String LOG_CONTEXT = "[CONSOLE GUI]";

    public ConsoleGUI(Engine gp, ConsoleHandler consoleHandler) {
        super("Game Console");
        this.consoleHandler = consoleHandler;
        this.commandHistory = new CommandHistory();
        this.inputQueue = new LinkedBlockingQueue<>();
        this.isScriptMode = false;
        this.document = new ScriptModeDocument();

        // Set up the main window
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create the output pane with the custom document
        outputPane = new JTextPane(document);
        outputPane.setEditable(false);
        outputPane.setBackground(new Color(30, 30, 30));
        outputPane.setMargin(new Insets(5, 5, 5, 5));

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(outputPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Create input field
        inputField = new JTextField();
        inputField.setBackground(new Color(40, 40, 40));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputField.setMargin(new Insets(5, 5, 5, 5));

        // Layout setup
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(40, 40, 40));
        JLabel promptLabel = new JLabel("> ");
        promptLabel.setForeground(Color.GREEN);
        promptLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        promptLabel.setBackground(new Color(40, 40, 40));
        promptLabel.setOpaque(true);
        inputPanel.add(promptLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Input handling
        inputField.addActionListener(e -> handleInput());
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> inputField.setText(commandHistory.getPrevious());
                    case KeyEvent.VK_DOWN -> inputField.setText(commandHistory.getNext());
                    case KeyEvent.VK_ESCAPE -> dispose();
                }
            }
        });

        // Window closing handling
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gp.setGameState(Engine.GameState.PAUSED);
                if (isScriptMode) {
                    inputQueue.offer("end");
                }
                dispose();
            }
        });

        printWelcomeMessage();
    }

    private void handleInput() {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            commandHistory.add(input);

            if (isScriptMode) {
                // Script módban a felhasználói input kezelése
                document.appendUserInput(input, true);  // Ez fogja csak növelni a sorszámot
                inputQueue.offer(input);

                if (input.equalsIgnoreCase("end")) {
                    isScriptMode = false;
                    document.resetLineNumber();
                    document.appendPrompt();
                }
            } else {
                // Normál módban a parancs kezelése
                document.appendPrompt();
                document.appendUserInput(input, false);

                if (input.startsWith("make")) {
                    isScriptMode = true;
                    document.resetLineNumber();
                    // Most csak simán rendszerüzenetként jelenítjük meg
                    document.appendSystemMessage("Enter commands for the script (type 'end' to finish):");
                }
                consoleHandler.executeCommand(input);
            }

            inputField.setText("");
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        outputPane.setCaretPosition(document.getLength());
    }

    public void appendToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            // Rendszerüzenetek mindig sorszám nélkül jelennek meg
            document.appendSystemMessage(text);
            if (isScriptMode) {
                // Script módban nem adunk új promptot rendszerüzenet után
                scrollToBottom();
            } else {
                document.appendPrompt();
                scrollToBottom();
            }
        });
    }

    public String getInput() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "end";
        }
    }

    private void printWelcomeMessage() {
        document.appendSystemMessage("Welcome to the Game Console");
        document.appendSystemMessage("Type 'help' for a list of available commands");
        document.appendSystemMessage("Press ESC or click X to close the console");
        document.appendSystemMessage("");
        document.appendPrompt();
    }

    private static class CommandHistory {
        private final java.util.List<String> history = new java.util.ArrayList<>();
        private int currentIndex = -1;

        public void add(String command) {
            history.add(command);
            currentIndex = history.size();
        }

        public String getPrevious() {
            if (currentIndex > 0) {
                currentIndex--;
                return history.get(currentIndex);
            }
            return currentIndex > -1 ? history.get(currentIndex) : "";
        }

        public String getNext() {
            if (currentIndex < history.size() - 1) {
                currentIndex++;
                return history.get(currentIndex);
            }
            if (currentIndex == history.size() - 1) {
                currentIndex++;
                return "";
            }
            return "";
        }
    }

    public void showConsole() {
        setVisible(true);
        inputField.requestFocus();
    }
}