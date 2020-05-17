package com.moormic.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

import lombok.Getter;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_INPUT_LENGTH = 32;
    private static final String OPERATOR_PADDING = " ";
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8,
                    button9, buttonC, buttonAdd, buttonSubtract, buttonMultiply, buttonDivide, buttonEquals;
    private String inputString;
    private TextView inputView, resultView;
    private float result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        bindClearListener();
        bindEqualsListener();
        bindNumberListeners();
        bindOperatorListeners();
    }


    private void initialise() {
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        buttonC = findViewById(R.id.buttonC);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonSubtract = findViewById(R.id.buttonSubtract);
        buttonMultiply = findViewById(R.id.buttonMultiply);
        buttonDivide = findViewById(R.id.buttonDivide);
        buttonEquals = findViewById(R.id.buttonEquals);
        inputView = findViewById(R.id.inputView);
        resultView = findViewById(R.id.resultView);

        resetValues();
        refreshViews();
    }

    private void bindClearListener() {
        buttonC.setOnClickListener(v -> {
            resetValues();
            refreshViews();
        });
    }

    private void bindEqualsListener() {
        buttonEquals.setOnClickListener(v -> {
            if (lastInputIsDigit()) {
                result = calculate();
                refreshViews();
            }
        });
    }

    private void bindNumberListeners() {
        Stream.of(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9)
                .forEach(button -> button.setOnClickListener(v -> appendInput(button.getText().toString())));
    }

    private void bindOperatorListeners() {
        Stream.of(buttonAdd, buttonSubtract, buttonMultiply, buttonDivide)
                .forEach(button -> button.setOnClickListener(v -> {
                    if (lastInputIsDigit()) {
                        appendInput(OPERATOR_PADDING + button.getText().toString() + OPERATOR_PADDING);
                    }
                }));
    }

    private void appendInput(String input) {
        if (inputString.length() < MAX_INPUT_LENGTH) {
            inputString = StringUtils.isNotBlank(inputString) ?
                    inputString.concat(input) :
                    input;
        }
        refreshViews();
    }

    private void resetValues() {
        result = 0;
        inputString = "";
    }

    private void refreshViews() {
        inputView.setText(inputString);
        resultView.setText(String.valueOf(result));
    }

    private float calculate() {
        // TODO: Implement BODMAS (using tree?)
        LinkedList<CalculationNode> calculationQueue = queueSubCalculations();
        calculationQueue.forEach(CalculationNode::calculate);
        return calculationQueue.getLast().getResult();
    }

    private LinkedList<CalculationNode> queueSubCalculations() {
        LinkedList<String> inputQueue = new LinkedList<>(Arrays.asList(inputString.split(OPERATOR_PADDING)));
        LinkedList<CalculationNode> calculationQueue = new LinkedList<>();

        String inputHead = inputQueue.poll();
        if (inputHead != null) {
            CalculationNode head = new CalculationNode(Float.parseFloat(inputHead));
            calculationQueue.add(head);

            CalculationNode previous = head;
            while (!inputQueue.isEmpty()) {
                String operator = inputQueue.poll();
                float operand = Float.parseFloat(inputQueue.poll());
                CalculationNode next = new CalculationNode(previous, operator, operand);
                calculationQueue.add(next);
                previous = next;
            }
        }

        return calculationQueue;
    }

    private boolean lastInputIsDigit() {
        return StringUtils.isNotEmpty(inputString) &&
                Character.isDigit(inputString.charAt(inputString.length() - 1));
    }

    private class CalculationNode {
        @Getter
        private float result;
        private CalculationNode previous;
        private String operator;
        private float operand;

        private CalculationNode(float result) {
            this.result = result;
        }

        private CalculationNode(CalculationNode previous, String operator, float operand) {
            this.previous = previous;
            this.operator = operator;
            this.operand = operand;
        }

        private void calculate() {
            if (getString(R.string.add).equals(operator)) {
                result = previous.getResult() + operand;
            } else if (getString(R.string.subtract).equals(operator)) {
                result = previous.getResult() - operand;
            } else if (getString(R.string.multiply).equals(operator)) {
                result = previous.getResult() * operand;
            } else if (getString(R.string.divide).equals(operator)) {
                result = previous.getResult() / operand;
            }
        }
    }

}
