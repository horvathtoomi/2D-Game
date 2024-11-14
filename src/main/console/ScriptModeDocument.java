package main.console;

import main.logger.GameLogger;

import javax.swing.text.*;
import java.awt.*;

public class ScriptModeDocument extends DefaultStyledDocument {
    private int lineNumber = 1;
    private final Style promptStyle;
    private final Style numberStyle;
    private final Style textStyle;
    private final Style scriptModeStyle;
    private final Style systemStyle;
    private static final String LOG_CONTEXT = "[SCRIPT MODE DOC]";

    public ScriptModeDocument() {
        // Alap stílusok létrehozása
        promptStyle = addStyle("prompt", null);
        StyleConstants.setForeground(promptStyle, Color.GREEN);
        StyleConstants.setFontFamily(promptStyle, "Consolas");
        StyleConstants.setFontSize(promptStyle, 14);

        numberStyle = addStyle("number", null);
        StyleConstants.setForeground(numberStyle, new Color(150, 150, 150));
        StyleConstants.setFontFamily(numberStyle, "Consolas");
        StyleConstants.setFontSize(numberStyle, 14);

        textStyle = addStyle("text", null);
        StyleConstants.setForeground(textStyle, Color.WHITE);
        StyleConstants.setFontFamily(textStyle, "Consolas");
        StyleConstants.setFontSize(textStyle, 14);

        scriptModeStyle = addStyle("scriptMode", null);
        StyleConstants.setForeground(scriptModeStyle, new Color(135, 206, 235));
        StyleConstants.setFontFamily(scriptModeStyle, "Consolas");
        StyleConstants.setFontSize(scriptModeStyle, 14);

        systemStyle = addStyle("system", null);
        StyleConstants.setForeground(systemStyle, new Color(200, 200, 200));
        StyleConstants.setFontFamily(systemStyle, "Consolas");
        StyleConstants.setFontSize(systemStyle, 14);
    }

    public void appendPrompt() {
        try {
            insertString(getLength(), "> ", promptStyle);
        } catch (BadLocationException e) {
            GameLogger.error(LOG_CONTEXT, "Error occurred during string insertion:" + e.getMessage(), e);
        }
    }

    public void appendUserInput(String text, boolean isScriptMode) {
        try {
            if (isScriptMode) {
                // Csak a tényleges felhasználói inputnál növeljük a sorszámot
                insertString(getLength(), lineNumber + ". ", numberStyle);
                insertString(getLength(), text + "\n", scriptModeStyle);
                lineNumber++;
            } else {
                insertString(getLength(), text + "\n", textStyle);
            }
        } catch (BadLocationException e) {
            GameLogger.error(LOG_CONTEXT, "Error occurred during getting user input:" + e.getMessage(), e);
        }
    }

    public void appendSystemMessage(String text) {
        try {
            // Rendszerüzenetek mindig sorszám nélkül
            insertString(getLength(), text + "\n", systemStyle);
        } catch (BadLocationException e) {
            GameLogger.error(LOG_CONTEXT, "Error occurred during system messages:" + e.getMessage(), e);
        }
    }


    public void resetLineNumber() {
        lineNumber = 1;
    }
}