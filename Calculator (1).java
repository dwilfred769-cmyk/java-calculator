import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator extends JFrame {

    private final JTextField display;
    private double currentValue = 0;
    private String pendingOperator = "";
    private boolean startNewNumber = true;

    public Calculator() {
        setTitle("Java Calculator");
        setSize(320, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        display = new JTextField("0");
        display.setEditable(false);
        display.setFont(new Font("SansSerif", Font.BOLD, 32));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setPreferredSize(new Dimension(300, 60));

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.add(display, BorderLayout.CENTER);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        String[] buttons = {
                "C", "\u00B1", "%", "\u00F7",
                "7", "8", "9", "\u00D7",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "\u232B", "="
        };

        for (String label : buttons) {
            JButton button = new JButton(label);
            button.setFont(new Font("SansSerif", Font.PLAIN, 20));
            button.setFocusPainted(false);
            styleButton(button, label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        setLayout(new BorderLayout());
        add(displayPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button, String label) {
        if (label.matches("[0-9.]")) {
            button.setBackground(new Color(230, 230, 230));
        } else if (label.equals("=")) {
            button.setBackground(new Color(255, 149, 0));
            button.setForeground(Color.WHITE);
        } else if (label.equals("C") || label.equals("\u00B1") || label.equals("%")) {
            button.setBackground(new Color(165, 165, 165));
        } else {
            button.setBackground(new Color(255, 159, 10));
            button.setForeground(Color.WHITE);
        }
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = ((JButton) e.getSource()).getText();
            handleInput(command);
        }
    }

    private void handleInput(String command) {
        switch (command) {
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
                appendDigit(command);
                break;
            case ".":
                appendDecimalPoint();
                break;
            case "C":
                clearAll();
                break;
            case "\u232B": // Backspace
                backspace();
                break;
            case "\u00B1": // +/-
                toggleSign();
                break;
            case "%":
                applyPercent();
                break;
            case "+": case "-": case "\u00D7": case "\u00F7":
                setPendingOperator(command);
                break;
            case "=":
                calculateResult();
                break;
        }
    }

    private void appendDigit(String digit) {
        if (startNewNumber) {
            display.setText(digit.equals("0") ? "0" : digit);
            startNewNumber = false;
        } else {
            if (display.getText().equals("0")) {
                display.setText(digit);
            } else {
                display.setText(display.getText() + digit);
            }
        }
    }

    private void appendDecimalPoint() {
        if (startNewNumber) {
            display.setText("0.");
            startNewNumber = false;
        } else if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void clearAll() {
        display.setText("0");
        currentValue = 0;
        pendingOperator = "";
        startNewNumber = true;
    }

    private void backspace() {
        String text = display.getText();
        if (text.length() > 1) {
            display.setText(text.substring(0, text.length() - 1));
        } else {
            display.setText("0");
            startNewNumber = true;
        }
    }

    private void toggleSign() {
        double value = Double.parseDouble(display.getText());
        display.setText(formatNumber(-value));
    }

    private void applyPercent() {
        double value = Double.parseDouble(display.getText());
        display.setText(formatNumber(value / 100));
    }

    private void setPendingOperator(String operator) {
        if (!pendingOperator.isEmpty() && !startNewNumber) {
            calculateResult();
        } else {
            currentValue = Double.parseDouble(display.getText());
        }
        pendingOperator = operator;
        startNewNumber = true;
    }

    private void calculateResult() {
        if (pendingOperator.isEmpty()) return;

        double operand = Double.parseDouble(display.getText());
        double result;

        try {
            switch (pendingOperator) {
                case "+": result = currentValue + operand; break;
                case "-": result = currentValue - operand; break;
                case "\u00D7": result = currentValue * operand; break;
                case "\u00F7":
                    if (operand == 0) throw new ArithmeticException("Cannot divide by zero");
                    result = currentValue / operand;
                    break;
                default: result = operand;
            }
            display.setText(formatNumber(result));
            currentValue = result;
        } catch (ArithmeticException ex) {
            display.setText("Error");
            currentValue = 0;
        }

        pendingOperator = "";
        startNewNumber = true;
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculator calculator = new Calculator();
            calculator.setVisible(true);
        });
    }
}
