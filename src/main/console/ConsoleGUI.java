package main.console;

import main.Engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsoleGUI extends JFrame {
    private final JTextPane outputPane;
    private final JTextField inputField;
    private final ConsoleHandler consoleHandler;
    private final ScriptModeDocument document;
    private final BlockingQueue<String> inputQueue;
    private final CommandHistory commandHistory;
    private final CommandCompleter commandCompleter;
    private volatile boolean isScriptMode;
    private static int numOfMakeEnd = 0;

    public ConsoleGUI(Engine gp, ConsoleHandler consoleHandler) {
        super("Game Console");
        this.consoleHandler = consoleHandler;
        this.commandHistory = new CommandHistory();
        this.inputQueue = new LinkedBlockingQueue<>();
        this.isScriptMode = false;
        this.document = new ScriptModeDocument();
        this.commandCompleter = new CommandCompleter();

        setSize(600, 400);
        setResizable(true);
        setLocationRelativeTo(null);

        outputPane = new JTextPane(document);
        outputPane.setEditable(false);
        outputPane.setBackground(new Color(30, 30, 30));
        outputPane.setMargin(new Insets(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(outputPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        inputField = new JTextField();
        inputField.setBackground(new Color(40, 40, 40));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputField.setMargin(new Insets(5, 5, 5, 5));

        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
        inputField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());


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
        inputField.addActionListener(_ -> handleInput());

        // Extended KeyListener
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> inputField.setText(commandHistory.getPrevious());
                    case KeyEvent.VK_DOWN -> inputField.setText(commandHistory.getNext());
                    case KeyEvent.VK_ESCAPE -> dispose();
                    case KeyEvent.VK_TAB -> {
                        e.consume();
                        handleTabCompletion();
                    }
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

        inputField.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "complete");
        inputField.getActionMap().put("complete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTabCompletion();
            }
        });

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
    }

    private void resetMakeEndNum(){
        numOfMakeEnd = 0;
    }

    private void handleInput() {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            commandHistory.add(input);
            if (isScriptMode) {
                document.appendUserInput(input, true);
                inputQueue.offer(input);
                if(input.startsWith("make")){
                    numOfMakeEnd++;
                }
                if (input.equalsIgnoreCase("end")) {
                    if(numOfMakeEnd <= 0) {
                        isScriptMode = false;
                        document.resetLineNumber();
                        document.appendPrompt();
                    } else{
                        numOfMakeEnd--;
                    }
                }
            } else {
                document.appendPrompt();
                document.appendUserInput(input, false);
                if (input.startsWith("make")) {
                    numOfMakeEnd++;
                    isScriptMode = true;
                    document.resetLineNumber();
                    resetMakeEndNum();
                    document.appendSystemMessage("Enter commands for the script (type 'end' to finish):");
                }
                consoleHandler.executeCommand(input);
            }
            inputField.setText("");
            scrollToBottom();
        }
    }

    private void handleTabCompletion() {
        String currentInput = inputField.getText().trim();
        if (currentInput.isEmpty()) {
            return;
        }

        String lastWord = currentInput.substring(currentInput.lastIndexOf(' ') + 1);
        String completed = commandCompleter.complete(lastWord);

        if (!completed.equals(lastWord)) {
            if (currentInput.contains(" ")) {
                String prefix = currentInput.substring(0, currentInput.lastIndexOf(' ') + 1);
                inputField.setText(prefix + completed);
            } else {
                inputField.setText(completed);
            }
            inputField.setCaretPosition(inputField.getText().length());
        }

        inputField.requestFocusInWindow();
    }

    private void scrollToBottom() {
        outputPane.setCaretPosition(document.getLength());
    }

    public void appendToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            document.appendSystemMessage(text);
            if (isScriptMode) {
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

    private static class CommandHistory {
        private final List<String> history = new ArrayList<>();
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