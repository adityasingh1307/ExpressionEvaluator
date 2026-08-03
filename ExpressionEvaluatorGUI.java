import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Desktop GUI frontend for the ExpressionEvaluator engine.
 * Pure Java Swing, no server, no external dependencies.
 *
 * This file contains ZERO evaluation logic — every calculation is
 * delegated to ExpressionEvaluator.evaluate(...) in ExpressionEvaluator.java.
 *
 * Compile:  javac ExpressionEvaluator.java ExpressionEvaluatorGUI.java
 * Run:      java ExpressionEvaluatorGUI
 */
public class ExpressionEvaluatorGUI extends JFrame {

    // ---- Palette -------------------------------------------------------
    private static final Color BG            = new Color(0x06, 0x06, 0x0A);
    private static final Color CARD_BG        = new Color(0x12, 0x12, 0x1A);
    private static final Color BORDER_COLOR   = new Color(0x2A, 0x2A, 0x38);
    private static final Color TEXT_MAIN      = new Color(0xEE, 0xF0, 0xF8);
    private static final Color TEXT_DIM       = new Color(0x9A, 0x9E, 0xB2);
    private static final Color VIOLET         = new Color(0x7C, 0x6C, 0xF6);
    private static final Color SUCCESS        = new Color(0x46, 0xE0, 0xA8);
    private static final Color DANGER         = new Color(0xFF, 0x6B, 0x7A);
    private static final Color INPUT_BG       = new Color(0x0B, 0x0B, 0x12);

    private final JTextField expressionField;
    private final JButton evaluateButton;
    private final JButton clearButton;
    private final JPanel resultPanel;
    private final JLabel resultLabel;
    private final JLabel resultValueLabel;

    public ExpressionEvaluatorGUI() {
        super("Expression Evaluator");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 420);
        setMinimumSize(new Dimension(400, 380));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(28, 28, 28, 28)
        ));

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));
        outer.add(card);
        add(outer, BorderLayout.CENTER);

        // ---- Eyebrow -----------------------------------------------------
        JLabel eyebrow = new JLabel("● EXPRESSION EVALUATOR");
        eyebrow.setForeground(VIOLET);
        eyebrow.setFont(new Font("Monospaced", Font.PLAIN, 11));
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(eyebrow);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        // ---- Title ---------------------------------------------------
        JLabel title = new JLabel("Type the math. Get the answer.");
        title.setForeground(TEXT_MAIN);
        title.setFont(new Font("SansSerif", Font.BOLD, 19));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel subtitle = new JLabel("<html>Supports + - * / and nested parentheses,<br>evaluated with a two-stack algorithm.</html>");
        subtitle.setForeground(TEXT_DIM);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // ---- Input ---------------------------------------------------
        expressionField = new JTextField();
        expressionField.setBackground(INPUT_BG);
        expressionField.setForeground(TEXT_MAIN);
        expressionField.setCaretColor(TEXT_MAIN);
        expressionField.setFont(new Font("Monospaced", Font.PLAIN, 18));
        expressionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        expressionField.setAlignmentX(Component.LEFT_ALIGNMENT);
        expressionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        card.add(expressionField);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        // ---- Buttons ---------------------------------------------------
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        evaluateButton = makeButton("Evaluate", VIOLET, TEXT_MAIN, true);
        clearButton = makeButton("Clear", CARD_BG, TEXT_DIM, false);

        buttonRow.add(evaluateButton);
        buttonRow.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonRow.add(clearButton);
        card.add(buttonRow);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        // ---- Result panel -----------------------------------------------
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        resultPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        resultPanel.setVisible(false);

        resultLabel = new JLabel("RESULT");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resultValueLabel = new JLabel(" ");
        resultValueLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        resultValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resultPanel.add(resultLabel);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        resultPanel.add(resultValueLabel);
        card.add(resultPanel);

        // ---- Examples -----------------------------------------------
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        JLabel examplesLabel = new JLabel("TRY ONE");
        examplesLabel.setForeground(new Color(0x63, 0x67, 0x7D));
        examplesLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        examplesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(examplesLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel chipRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        chipRow.setOpaque(false);
        chipRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] examples = {"2+3", "(5+3)*2", "10/(2+3)", "(2+3)*(4-1)"};
        for (String ex : examples) {
            chipRow.add(makeChip(ex));
        }
        card.add(chipRow);

        // ---- Wiring -----------------------------------------------
        evaluateButton.addActionListener(this::onEvaluate);
        clearButton.addActionListener(e -> {
            expressionField.setText("");
            resultPanel.setVisible(false);
            expressionField.requestFocusInWindow();
        });
        expressionField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onEvaluate(null);
                }
            }
        });

        expressionField.requestFocusInWindow();
    }

    private JButton makeButton(String text, Color bg, Color fg, boolean primary) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(fg);
        button.setBackground(bg);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBorder(primary
                ? new EmptyBorder(10, 18, 10, 18)
                : BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(9, 17, 9, 17)));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(!primary);
        return button;
    }

    private JButton makeChip(String expr) {
        JButton chip = new JButton(expr);
        chip.setFont(new Font("Monospaced", Font.PLAIN, 12));
        chip.setForeground(TEXT_DIM);
        chip.setBackground(new Color(0x1A, 0x1A, 0x24));
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        chip.setFocusPainted(false);
        chip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chip.addActionListener(e -> {
            expressionField.setText(expr);
            resultPanel.setVisible(false);
            expressionField.requestFocusInWindow();
        });
        return chip;
    }

    /**
     * The only place this file touches the engine: a single call to
     * ExpressionEvaluator.evaluate(...). All stack logic lives in
     * ExpressionEvaluator.java.
     */
    private void onEvaluate(ActionEvent e) {
        String expression = expressionField.getText().trim();

        if (expression.isEmpty()) {
            showError("Please enter an expression first.");
            return;
        }

        try {
            double result = ExpressionEvaluator.evaluate(expression);
            showResult(result);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showResult(double value) {
        String display = (value == Math.floor(value) && !Double.isInfinite(value))
                ? String.valueOf((long) value)
                : String.valueOf(value);

        resultLabel.setText("RESULT");
        resultLabel.setForeground(SUCCESS);
        resultValueLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        resultValueLabel.setText(display);
        resultValueLabel.setForeground(TEXT_MAIN);
        resultPanel.setBackground(Color.BLUE);
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SUCCESS, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        resultPanel.setVisible(true);
    }

    private void showError(String message) {
        resultLabel.setText("ERROR");
        resultLabel.setForeground(DANGER);
        resultValueLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultValueLabel.setText(message);
        resultValueLabel.setForeground(TEXT_DIM);
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DANGER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        resultPanel.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpressionEvaluatorGUI gui = new ExpressionEvaluatorGUI();
            gui.setVisible(true);
        });
    }
}