package org.example.cs402_lab;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

public class VigenereCipher extends Application {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("Vigenère Cipher");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label inputLabel = new Label("Input Text:");
        TextArea inputTextArea = new TextArea();
        inputTextArea.setPromptText("Enter the text to encrypt/decrypt");
        inputTextArea.setWrapText(true);

        Label keyLabel = new Label("Key:");
        TextField keyField = new TextField();
        keyField.setPromptText("Enter encryption/decryption key (letters only)");

        Label keyValidationLabel = new Label("");
        keyValidationLabel.setTextFill(Color.RED);

        Label operationLabel = new Label("Operation:");
        ToggleGroup operationGroup = new ToggleGroup();
        RadioButton encryptRadio = new RadioButton("Encrypt");
        RadioButton decryptRadio = new RadioButton("Decrypt");
        encryptRadio.setToggleGroup(operationGroup);
        decryptRadio.setToggleGroup(operationGroup);
        encryptRadio.setSelected(true);

        HBox operationBox = new HBox(10, encryptRadio, decryptRadio);

        Button processButton = new Button("Process");
        processButton.setDefaultButton(true);

        Button clearButton = new Button("Clear All");

        HBox buttonBox = new HBox(10, processButton, clearButton);
        buttonBox.setAlignment(Pos.CENTER);

        Label outputLabel = new Label("Result:");
        TextArea outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setWrapText(true);

        keyField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                keyValidationLabel.setText("");
                processButton.setDisable(true);
                return;
            }

            if (!newValue.matches("[a-zA-Z]+")) {
                processButton.setDisable(true);
            } else {
                keyValidationLabel.setText("");
                processButton.setDisable(false);
            }
        });

        processButton.setDisable(true);

        processButton.setOnAction(e -> {
            try {
                String inputText = inputTextArea.getText();
                if (inputText.trim().isEmpty()) {
                    showAlert("Error", "Input text cannot be empty");
                    return;
                }

                String key = keyField.getText().toUpperCase();
                boolean isEncrypt = encryptRadio.isSelected();

                String result;
                if (isEncrypt) {
                    result = encrypt(inputText, key);
                } else {
                    result = decrypt(inputText, key);
                }

                outputTextArea.setText(result);
            } catch (Exception ex) {
                showAlert("Error", "An error occurred: " + ex.getMessage());
            }
        });

        clearButton.setOnAction(e -> {
            inputTextArea.clear();
            keyField.clear();
            outputTextArea.clear();
            keyValidationLabel.setText("");
            encryptRadio.setSelected(true);
        });

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                titleLabel,
                inputLabel, inputTextArea,
                keyLabel, keyField, keyValidationLabel,
                operationLabel, operationBox,
                buttonBox,
                outputLabel, outputTextArea
        );

        root.setStyle("-fx-background-color: #f5f5f5;");
        processButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        clearButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        Scene scene = new Scene(root, 600, 650);
        primaryStage.setTitle("Vigenère Cipher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String encrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        int keyLength = key.length();
        int keyIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

             if (Character.isLetter(c)) {
                char upperC = Character.toUpperCase(c);
                char keyChar = Character.toUpperCase(key.charAt(keyIndex % keyLength));
                int shift = ALPHABET.indexOf(keyChar);
                int originalPos = ALPHABET.indexOf(upperC);
                int newPos = (originalPos + shift) % 26;
                char encryptedChar = ALPHABET.charAt(newPos);

                 result.append(Character.isUpperCase(c) ? encryptedChar : Character.toLowerCase(encryptedChar));
                keyIndex++;
            } else {
                 result.append(c);
            }
        }

        return result.toString();
    }

    private String decrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        int keyLength = key.length();
        int keyIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

             if (Character.isLetter(c)) {
                char upperC = Character.toUpperCase(c);
                char keyChar = Character.toUpperCase(key.charAt(keyIndex % keyLength));
                int shift = ALPHABET.indexOf(keyChar);
                int originalPos = ALPHABET.indexOf(upperC);
                int newPos = (originalPos - shift + 26) % 26;
                char decryptedChar = ALPHABET.charAt(newPos);

                 result.append(Character.isUpperCase(c) ? decryptedChar : Character.toLowerCase(decryptedChar));
                keyIndex++;
            } else {
                 result.append(c);
            }
        }

        return result.toString();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}