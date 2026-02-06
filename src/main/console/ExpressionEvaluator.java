package main.console;

/**
 * Parses and evaluates expressions for conditions and assignments.
 * Supports comparison operators (<, >, <=, >=, ==, !=) and assignment operators
 * (=, +=, -=, *=, /=).
 */
public class ExpressionEvaluator {
    private final VariableContext context;

    public ExpressionEvaluator(VariableContext context) {
        this.context = context;
    }

    /**
     * Evaluates a boolean condition expression.
     * Supports: <, >, <=, >=, ==, !=
     * 
     * @param expression the condition to evaluate (e.g., "i < 10", "x != 0")
     * @return true if condition is met, false otherwise
     * @throws IllegalArgumentException if expression is invalid
     */
    public boolean evaluateCondition(String expression) {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty");
        }

        expression = context.resolveVariables(expression.trim());

        // Try comparison operators in order of specificity (>= before >, etc.)
        String[] operators = { "<=", ">=", "==", "!=", "<", ">" };

        for (String op : operators) {
            if (expression.contains(op)) {
                String[] parts = expression.split(java.util.regex.Pattern.quote(op), 2);
                if (parts.length == 2) {
                    Object left = parseValue(parts[0].trim());
                    Object right = parseValue(parts[1].trim());

                    return compare(left, right, op);
                }
            }
        }

        throw new IllegalArgumentException("Invalid condition expression: " + expression);
    }

    /**
     * Executes an assignment expression.
     * Supports: =, +=, -=, *=, /=
     * 
     * @param expression the assignment to execute (e.g., "i = 0", "i += 1")
     * @throws IllegalArgumentException if expression is invalid
     */
    public void executeAssignment(String expression) {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty");
        }

        expression = expression.trim();

        // Try compound assignment operators first
        String[] compoundOps = { "+=", "-=", "*=", "/=" };
        for (String op : compoundOps) {
            if (expression.contains(op)) {
                String[] parts = expression.split(java.util.regex.Pattern.quote(op), 2);
                if (parts.length == 2) {
                    String varName = parts[0].trim();
                    String rightSide = context.resolveVariables(parts[1].trim());

                    Object currentValue = context.get(varName);
                    if (currentValue == null) {
                        throw new IllegalArgumentException("Variable not defined: " + varName);
                    }

                    int current = toInt(currentValue);
                    int operand = toInt(parseValue(rightSide));
                    int result;

                    switch (op) {
                        case "+=" -> result = current + operand;
                        case "-=" -> result = current - operand;
                        case "*=" -> result = current * operand;
                        case "/=" -> {
                            if (operand == 0) {
                                throw new IllegalArgumentException("Division by zero");
                            }
                            result = current / operand;
                        }
                        default -> throw new IllegalArgumentException("Unknown operator: " + op);
                    }

                    context.set(varName, result);
                    return;
                }
            }
        }

        // Try simple assignment
        if (expression.contains("=")) {
            String[] parts = expression.split(java.util.regex.Pattern.quote("="), 2);
            if (parts.length == 2) {
                String varName = parts[0].trim();
                String rightSide = context.resolveVariables(parts[1].trim());
                Object value = parseValue(rightSide);
                context.set(varName, value);
                return;
            }
        }

        throw new IllegalArgumentException("Invalid assignment expression: " + expression);
    }

    /**
     * Compares two values using the specified operator.
     * 
     * @param left  the left operand
     * @param right the right operand
     * @param op    the comparison operator
     * @return the result of the comparison
     */
    private boolean compare(Object left, Object right, String op) {
        // Try numeric comparison first
        if (left instanceof Integer && right instanceof Integer) {
            int l = (Integer) left;
            int r = (Integer) right;

            return switch (op) {
                case "<" -> l < r;
                case ">" -> l > r;
                case "<=" -> l <= r;
                case ">=" -> l >= r;
                case "==" -> l == r;
                case "!=" -> l != r;
                default -> throw new IllegalArgumentException("Unknown operator: " + op);
            };
        }

        // Fall back to string comparison
        String l = left.toString();
        String r = right.toString();

        return switch (op) {
            case "==" -> l.equals(r);
            case "!=" -> !l.equals(r);
            default -> throw new IllegalArgumentException("Cannot use operator " + op + " with non-numeric values");
        };
    }

    /**
     * Parses a string value to either Integer or String.
     * 
     * @param value the value to parse
     * @return Integer if numeric, otherwise String
     */
    private Object parseValue(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        // Try to parse as integer
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Return as string if not a number
            return value;
        }
    }

    /**
     * Converts an object to integer.
     * 
     * @param value the value to convert
     * @return the integer value
     * @throws IllegalArgumentException if value cannot be converted to integer
     */
    private int toInt(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert to integer: " + value);
            }
        }

        throw new IllegalArgumentException("Cannot convert to integer: " + value);
    }
}
