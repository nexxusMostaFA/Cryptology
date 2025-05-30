package org.example.cs402_lab;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedWriter;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.application.Application;

public class Caesar_Cipher extends Application {
    private static final String OUTPUT_FILE_PATH = "C:\\Users\\mostafa\\Desktop\\CS402\\CS402\\src\\main\\java\\org\\example\\cs402_lab\\output.txt";
    @Override
    public void start(Stage stage) {
        stage.setTitle("Caesar Cipher");
        VBox pane = new VBox(); // virtual box
        Scene scene = new Scene(pane, 500, 500);
        stage.setScene(scene);
        stage.show();

        Label enter_text = new Label("Enter Text:");
        pane.getChildren().add(enter_text);

        TextArea input_textArea = new TextArea();
        pane.getChildren().add(input_textArea);

        Label enter_key = new Label("Enter Key:");
        pane.getChildren().add(enter_key);

        TextField keyField = new TextField();
        pane.getChildren().add(keyField);

        Label press_button = new Label("Press Encrypt or Decrypt Button!");
        pane.getChildren().add(press_button);

        Button encrypt_button = new Button("Encrypt!");
        pane.getChildren().add(encrypt_button);
        Button decrypt_button = new Button("Decrypt!");
        pane.getChildren().add(decrypt_button);

        Label result = new Label("Result:");
        pane.getChildren().add(result);

        TextArea output_textArea = new TextArea();
        pane.getChildren().add(output_textArea);

        encrypt_button.setOnAction(e -> {
            String text = input_textArea.getText();
            String key = keyField.getText();
            boolean isDigitOnly = isNumeric(key);
            if (text.isEmpty() || key.isEmpty() || !isDigitOnly) {
                return;
            }
            output_textArea.setText(encrypt(text, Integer.parseInt(key)));  // "123" >> 123
        });

        decrypt_button.setOnAction(e -> {
            String text = input_textArea.getText();
            String key = keyField.getText();
            boolean isDigitOnly = isNumeric(key);
            if (text.isEmpty() || key.isEmpty() || !isDigitOnly) {
                return;
            }
            output_textArea.setText(decrypt(text, Integer.parseInt(key)));
        });

        Button attack_button = new Button("Attack!");
        pane.getChildren().add(attack_button);

        attack_button.setOnAction(e -> {
            String text = input_textArea.getText();
            ArrayList<String> decryptionAttempts = attack(text);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {
                for (String line : decryptionAttempts) {
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("File written successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String encrypt(String text, int key) {
        return shiftText(text, key);
    }

    public static String decrypt(String text, int key) {
        return shiftText(text, -key);
    }

    private static String shiftText(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (!Character.isAlphabetic(ch)) {
                result.append(ch);
            } else {
                char base = Character.isLowerCase(ch) ? 'a' : 'A';
                result.append((char) (((ch - base + (shift % 26) + 26) % 26) + base));
            }
        }
        return result.toString();
    }

    public static ArrayList<String> attack(String cipher_text) {
        ArrayList<String> decryptionAttempts = new ArrayList<>();
        for (int key = 1; key <= 26; key++) {
            decryptionAttempts.add("Key: " + key + ", " + "Plain Text: " + decrypt(cipher_text, key));
        }
        return decryptionAttempts;
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char ch : str.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }
}