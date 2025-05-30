package org.example.cs402_lab.Task5;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Task5 extends Application {

    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private TextField keyField;
    private ToggleGroup operationGroup;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AES Encryption/Decryption Tool");

        Label inputLabel = new Label("Input Text:");
        inputTextArea = new TextArea();
        inputTextArea.setPromptText("Enter text to encrypt/decrypt here");
        inputTextArea.setWrapText(true);

        Label keyLabel = new Label("128-bit Key (16 characters):");
        keyField = new TextField();
        keyField.setPromptText("Enter 16-character key");

        // Operation selection
        Label operationLabel = new Label("Operation:");
        operationGroup = new ToggleGroup();
        RadioButton encryptRadio = new RadioButton("Encrypt");
        encryptRadio.setToggleGroup(operationGroup);
        encryptRadio.setSelected(true);
        RadioButton decryptRadio = new RadioButton("Decrypt");
        decryptRadio.setToggleGroup(operationGroup);

        Button processButton = new Button("Process");
        processButton.setOnAction(e -> processText());

        Label outputLabel = new Label("Result:");
        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setWrapText(true);

        statusLabel = new Label();

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(
                inputLabel, inputTextArea,
                keyLabel, keyField,
                operationLabel, encryptRadio, decryptRadio,
                processButton,
                outputLabel, outputTextArea,
                statusLabel
        );

        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void processText() {
        try {
            String input = inputTextArea.getText();
            String key = keyField.getText();

            if (key.length() != 16) {
                statusLabel.setText("Error: Key must be exactly 16 characters (128 bits)");
                return;
            }

            boolean encrypt = ((RadioButton) operationGroup.getSelectedToggle()).getText().equals("Encrypt");

            // Create AES instance (no need to convert key to bytes)
            AES aes = new AES();

            String result;
            if (encrypt) {
                result = aes.encrypt(input, key);
                statusLabel.setText("Encryption completed. Check 'aes_log.txt' for details.");
            } else {
                // For decryption, remove any spaces from the hex string
                String cleanedInput = input.replaceAll("\\s", "");
                result = aes.decrypt(cleanedInput, key);
                statusLabel.setText("Decryption completed. Check 'aes_log.txt' for details.");
            }

            outputTextArea.setText(result);

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}