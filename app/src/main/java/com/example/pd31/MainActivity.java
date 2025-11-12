package com.example.pd31;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private StringBuilder expression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.textView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupButtons();
    }

    private void setupButtons() {
        int[] numberButtonIds = {
                R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four,
                R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.dot
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(this::onNumberClick);
        }

        int[] operatorButtonIds = {R.id.plus, R.id.minus, R.id.multiplication, R.id.division};
        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(this::onOperatorClick);
        }

        findViewById(R.id.equals).setOnClickListener(this::onEqualsClick);
        findViewById(R.id.c).setOnClickListener(this::onClearClick);
        findViewById(R.id.plusminus).setOnClickListener(this::onPlusMinusClick);
        findViewById(R.id.backspace).setOnClickListener(this::onBackspaceClick);
        findViewById(R.id.ce).setOnClickListener(this::onClearEntryClick);
    }

    private void onNumberClick(View view) {
        Button button = (Button) view;
        expression.append(button.getText().toString());
        updateDisplay();
    }

    private void onOperatorClick(View view) {
        Button button = (Button) view;
        expression.append(" ").append(button.getText()).append(" ");
        updateDisplay();
    }

    private void onEqualsClick(View view) {
        String exprStr = expression.toString();
        if (exprStr.isEmpty()) return;

        // To prevent crashes, don't evaluate if the expression ends with an operator.
        char lastChar = exprStr.charAt(exprStr.length() - 1);
        if (lastChar == ' '){ // all operators are added with trailing space
            return;
        }

        try {
            double result = evaluateExpression(exprStr);
            if (result == (long) result) {
                expression = new StringBuilder(String.format("%d", (long)result));
            } else {
                expression = new StringBuilder(String.valueOf(result));
            }
            updateDisplay();
        } catch (Exception e) {
            display.setText("Error");
            expression = new StringBuilder();
        }
    }

    private void onClearClick(View view) {
        expression = new StringBuilder();
        updateDisplay();
    }

    private void onClearEntryClick(View view) {
        String currentExpr = expression.toString();
        if (currentExpr.isEmpty()) {
            return;
        }

        if (currentExpr.endsWith(" ")) { // Operator (" op ") was the last thing added.
            String trimmed = currentExpr.trim();
            int lastSpace = trimmed.lastIndexOf(' ');
            if (lastSpace != -1) {
                expression = new StringBuilder(trimmed.substring(0, lastSpace));
            } else {
                expression.setLength(0);
            }
        } else {
            // A number was the last thing added. Remove it, but keep the operator before it.
            int lastSpace = currentExpr.lastIndexOf(' ');
            if (lastSpace != -1) {
                expression.setLength(lastSpace + 1);
            } else {
                // No operators, just a number
                expression.setLength(0);
            }
        }
        updateDisplay();
    }

    private void onBackspaceClick(View view) {
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
            updateDisplay();
        }
    }

    private void onPlusMinusClick(View view) {
        if (expression.length() > 0) {
            // This is a simplified implementation. A more robust solution would be needed for complex expressions.
            if (expression.charAt(0) == '-') {
                expression.deleteCharAt(0);
            } else {
                expression.insert(0, "-");
            }
            updateDisplay();
        }
    }

    private void updateDisplay() {
        display.setText(expression.toString());
    }

    private double evaluateExpression(String expression) {
        // This is a simple evaluator and does not handle operator precedence.
        String[] tokens = expression.split(" ");
        double result = Double.parseDouble(tokens[0]);

        for (int i = 1; i < tokens.length; i += 2) {
            String operator = tokens[i];
            double nextOperand = Double.parseDouble(tokens[i + 1]);

            switch (operator) {
                case "+":
                    result += nextOperand;
                    break;
                case "-":
                    result -= nextOperand;
                    break;
                case "*":
                    result *= nextOperand;
                    break;
                case "/":
                    if (nextOperand != 0) {
                        result /= nextOperand;
                    } else {
                        throw new ArithmeticException("Division by zero");
                    }
                    break;
            }
        }
        return result;
    }
}
