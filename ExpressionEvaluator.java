import java.util.Stack;

/**
 * Pure logic class — the stack-based expression evaluation engine.
 * No UI, no I/O. Any frontend (terminal, Swing, web, etc.) can call
 * into this class.
 *
 *   1) infixToPostfix()  -> converts infix to postfix using a
 *      Stack<Character> for operators.
 *   2) evaluatePostfix() -> evaluates the postfix expression using a
 *      Stack<Double> for operands.
 */
public class ExpressionEvaluator {

    /**
     * Converts an infix expression (e.g. "(5+3)*2") into a postfix
     * expression (e.g. "5 3 + 2 *"). Tokens are space-separated so
     * multi-digit / decimal numbers survive the round trip.
     */
    public static String infixToPostfix(String expression) {
        StringBuilder result = new StringBuilder();
        Stack<Character> operators = new Stack<>();

        int n = expression.length();
        int i = 0;

        while (i < n) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                int start = i;
                boolean sawDot = false;
                while (i < n && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    if (expression.charAt(i) == '.') {
                        if (sawDot) {
                            throw new IllegalArgumentException("Invalid number near position " + i);
                        }
                        sawDot = true;
                    }
                    i++;
                }
                result.append(expression, start, i).append(' ');
            } else if (c == '(') {
                operators.push(c);
                i++;
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    result.append(operators.pop()).append(' ');
                }
                if (operators.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                operators.pop(); // discard '('
                i++;
            } else if (isOperator(c)) {
                while (!operators.isEmpty() && operators.peek() != '('
                        && precedence(operators.peek()) >= precedence(c)) {
                    result.append(operators.pop()).append(' ');
                }
                operators.push(c);
                i++;
            } else {
                throw new IllegalArgumentException("Unsupported character: '" + c + "'");
            }
        }

        while (!operators.isEmpty()) {
            char op = operators.pop();
            if (op == '(') {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            result.append(op).append(' ');
        }

        return result.toString().trim();
    }

    /**
     * Evaluates a space-separated postfix expression using a value stack.
     */
    public static double evaluatePostfix(String postfix) {
        Stack<Double> values = new Stack<>();

        for (String token : postfix.split("\\s+")) {
            if (token.isEmpty()) {
                continue;
            }

            if (isNumber(token)) {
                values.push(Double.parseDouble(token));
            } else if (token.length() == 1 && isOperator(token.charAt(0))) {
                if (values.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression: missing operand");
                }
                double right = values.pop();
                double left = values.pop();
                values.push(applyOperator(left, right, token.charAt(0)));
            } else {
                throw new IllegalArgumentException("Unsupported token: '" + token + "'");
            }
        }

        if (values.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }

        return values.pop();
    }

    /**
     * Convenience method: does infix -> postfix -> evaluate in one call.
     */
    public static double evaluate(String infixExpression) {
        return evaluatePostfix(infixToPostfix(infixExpression));
    }

    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    private static double applyOperator(double left, double right, char operator) {
        switch (operator) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '*':
                return left * right;
            case '/':
                if (right == 0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                return left / right;
            default:
                throw new IllegalArgumentException("Unknown operator: '" + operator + "'");
        }
    }
}