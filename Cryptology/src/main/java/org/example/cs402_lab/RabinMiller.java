package org.example.cs402_lab;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.util.Random;

public class RabinMiller extends Application {

    public static String millerRabinTest(BigInteger n, int iterations) {
        if (n.compareTo(BigInteger.TWO) < 0) return "Composite";
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return "May Be Prime";
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return "Composite";

        BigInteger d = n.subtract(BigInteger.ONE);
        int r = 0;

        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            r++;
        }

        Random rand = new Random();
        for (int i = 0; i < iterations; i++) {
            BigInteger a = new BigInteger(n.bitLength() - 2, rand).add(BigInteger.TWO);
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) continue;

            boolean passed = false;
            for (int j = 0; j < r - 1; j++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    passed = true;
                    break;
                }
            }
            if (!passed) return "Composite";
        }
        return "May Be Prime";
    }

    @Override
    public void start(Stage stage) {
        Label inputLabel = new Label("Enter Number:");
        TextField inputField = new TextField();
        Button checkButton = new Button("Check Prime");
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        checkButton.setOnAction(e -> {
            String input = inputField.getText().trim();
            try {
                BigInteger number = new BigInteger(input);
                String result = millerRabinTest(number, 10);
                outputArea.setText(result);
            } catch (Exception ex) {
                outputArea.setText("Invalid input");
            }
        });

        VBox layout = new VBox(10, inputLabel, inputField, new HBox(10, checkButton), outputArea);
        layout.setPadding(new Insets(15));
        Scene scene = new Scene(layout, 500, 400);
        stage.setScene(scene);
        stage.setTitle("Rabin-Miller Primality Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}