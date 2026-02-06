package main.console;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages variable storage with stack-based scoping.
 * Supports variable declaration, retrieval, and automatic resolution in
 * commands.
 */
public class VariableContext {
    private final Deque<Map<String, Object>> scopeStack;
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$([a-zA-Z_][a-zA-Z0-9_]*)");

    /**
     * Constructor initializes with a global scope.
     */
    public VariableContext() {
        this.scopeStack = new ArrayDeque<>();
        // Initialize with global scope
        this.scopeStack.push(new HashMap<>());
    }

    /**
     * Pushes a new scope onto the stack.
     * Used when entering a loop or block scope.
     */
    public void pushScope() {
        scopeStack.push(new HashMap<>());
    }

    /**
     * Pops the current scope from the stack.
     * Used when exiting a loop or block scope.
     * 
     * @throws IllegalStateException if attempting to pop the global scope
     */
    public void popScope() {
        if (scopeStack.size() <= 1) {
            throw new IllegalStateException("Cannot pop global scope");
        }
        scopeStack.pop();
    }

    /**
     * Sets a variable in the current scope.
     * 
     * @param name  the variable name
     * @param value the value to store (Integer or String)
     */
    public void set(String name, Object value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        // Store in the current (top) scope (case-insensitive)
        assert scopeStack.peek() != null;
        scopeStack.peek().put(name.toLowerCase(), value);
    }

    /**
     * Retrieves a variable value, searching from innermost to outermost scope.
     * 
     * @param name the variable name
     * @return the variable value, or null if not found
     */
    public Object get(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        // Search from top (innermost) to bottom (outermost) of stack (case-insensitive)
        String lowerName = name.toLowerCase();
        for (Map<String, Object> scope : scopeStack) {
            if (scope.containsKey(lowerName)) {
                return scope.get(lowerName);
            }
        }
        return null;
    }

    /**
     * Checks if a variable exists in any scope.
     * 
     * @param name the variable name
     * @return true if the variable exists, false otherwise
     */
    public boolean has(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        // Case-insensitive lookup
        String lowerName = name.toLowerCase();
        for (Map<String, Object> scope : scopeStack) {
            if (scope.containsKey(lowerName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resolves all variable references in a command string.
     * Variables are referenced using $varname syntax.
     * 
     * @param command the command string with potential variable references
     * @return the command string with variables replaced by their values
     */
    public String resolveVariables(String command) {
        if (command == null || command.isEmpty()) {
            return command;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(command);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1);
            Object value = get(varName);

            if (value != null) {
                // Replace the variable reference with its value
                matcher.appendReplacement(result, Matcher.quoteReplacement(value.toString()));
            } else {
                // Leave the variable reference as-is if not found
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Clears all scopes except the global scope.
     * Useful for resetting the context.
     */
    public void reset() {
        while (scopeStack.size() > 1) {
            scopeStack.pop();
        }
        assert scopeStack.peek() != null;
        scopeStack.peek().clear();
    }

    /**
     * Returns the current scope depth.
     * 
     * @return the number of scopes on the stack
     */
    public int getScopeDepth() {
        return scopeStack.size();
    }
}