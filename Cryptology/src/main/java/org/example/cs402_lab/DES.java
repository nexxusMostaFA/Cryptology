package org.example.cs402_lab;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class DES extends Application {

    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private TextField keyField;
    private ToggleGroup modeGroup;
    private Label keyLengthLabel;
    private Label inputLengthLabel;
    private CheckBox blockPaddingCheckbox;
    private TextArea keyInfoTextArea;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DES Encryption Tool");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));
        applyStyles(mainLayout);

        HBox menuBar = createMenuBar();
        mainLayout.setTop(menuBar);

        VBox contentArea = createContentArea();
        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 800, 700);
        applyInlineStyles(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void applyInlineStyles(Scene scene) {
        scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', Helvetica, Arial, sans-serif; -fx-background-color: #f5f5f5;");
    }

    private void applyStyles(Region region) {
        region.setStyle("-fx-spacing: 10;");
    }

    private HBox createMenuBar() {
        HBox menuBar = new HBox(10);
        menuBar.setPadding(new Insets(10));
        menuBar.setAlignment(Pos.CENTER_LEFT);
        applyStyles(menuBar);

        Button clearBtn = new Button("Clear");
        String buttonStyle = "-fx-background-color: #2196f3; -fx-text-fill: white; -fx-padding: 8px 16px; " +
                "-fx-background-radius: 4px;";
        clearBtn.setStyle(buttonStyle);

        clearBtn.setOnAction(e -> {
            inputTextArea.clear();
            outputTextArea.clear();
            keyField.clear();
            keyInfoTextArea.clear();
            updateInputLength(0);
            updateKeyLength(0);
        });

        menuBar.getChildren().add(clearBtn);
        return menuBar;
    }

    private VBox createContentArea() {
        VBox contentArea = new VBox(15);
        contentArea.setPadding(new Insets(10));
        applyStyles(contentArea);

        VBox inputSection = new VBox(5);
        Label inputLabel = new Label("Plain Text:");
        inputLabel.setStyle("-fx-font-weight: bold;");
        inputTextArea = new TextArea();
        inputTextArea.setWrapText(true);
        inputTextArea.setPrefRowCount(6);
        inputTextArea.setStyle("-fx-font-size: 14px; -fx-border-color: #ddd; -fx-border-radius: 4px;");

        HBox inputInfoBox = new HBox(10);
        inputInfoBox.setAlignment(Pos.CENTER_RIGHT);
        inputLengthLabel = new Label("Length: 0 bytes");
        inputLengthLabel.setStyle("-fx-text-fill: #666;");
        inputInfoBox.getChildren().add(inputLengthLabel);

        inputSection.getChildren().addAll(inputLabel, inputTextArea, inputInfoBox);

        inputTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateInputLength(newValue.length());
        });

        Label outputLabel = new Label("Output:");
        outputLabel.setStyle("-fx-font-weight: bold;");
        outputTextArea = new TextArea();
        outputTextArea.setWrapText(true);
        outputTextArea.setPrefRowCount(6);
        outputTextArea.setEditable(false);
        outputTextArea.setStyle("-fx-font-size: 14px; -fx-border-color: #ddd; -fx-border-radius: 4px;");

        Label keyInfoLabel = new Label("Key Information:");
        keyInfoLabel.setStyle("-fx-font-weight: bold;");
        keyInfoTextArea = new TextArea();
        keyInfoTextArea.setWrapText(true);
        keyInfoTextArea.setPrefRowCount(4);
        keyInfoTextArea.setEditable(false);
        keyInfoTextArea.setStyle("-fx-font-size: 14px; -fx-border-color: #ddd; -fx-border-radius: 4px;");

        VBox keySection = new VBox(5);
        Label keyLabel = new Label("Encryption Key:");
        keyLabel.setStyle("-fx-font-weight: bold;");

        HBox keyInputRow = new HBox(10);
        keyInputRow.setAlignment(Pos.CENTER_LEFT);

        keyField = new TextField();
        keyField.setPrefWidth(300);
        keyField.setPromptText("Enter encryption key (any length)");
        keyField.setStyle("-fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 4px;");

        keyInputRow.getChildren().add(keyField);

        HBox keyInfoBox = new HBox(10);
        keyInfoBox.setAlignment(Pos.CENTER_LEFT);
        keyLengthLabel = new Label("Key length: 0 bytes");
        keyLengthLabel.setStyle("-fx-text-fill: #666;");

        keyInfoBox.getChildren().add(keyLengthLabel);

        keySection.getChildren().addAll(keyLabel, keyInputRow, keyInfoBox);

        keyField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateKeyLength(newValue.length());
            if (!newValue.isEmpty()) {
                try {
                    byte[] keyBytes = prepareKeyBytes(newValue);
                    displayKeyInfo(keyBytes);
                } catch (Exception e) {
                    keyInfoTextArea.setText("Error generating key preview: " + e.getMessage());
                }
            } else {
                keyInfoTextArea.clear();
            }
        });

        HBox modeSection = new HBox(15);
        modeSection.setAlignment(Pos.CENTER_LEFT);
        applyStyles(modeSection);

        Label modeLabel = new Label("Mode:");
        modeLabel.setStyle("-fx-font-weight: bold;");
        modeGroup = new ToggleGroup();

        RadioButton encryptRadio = new RadioButton("Encrypt");
        encryptRadio.setToggleGroup(modeGroup);
        encryptRadio.setSelected(true);
        encryptRadio.setStyle("-fx-padding: 5px;");

        RadioButton decryptRadio = new RadioButton("Decrypt");
        decryptRadio.setToggleGroup(modeGroup);
        decryptRadio.setStyle("-fx-padding: 5px;");

        blockPaddingCheckbox = new CheckBox("Use Block Padding");
        blockPaddingCheckbox.setSelected(true);
        blockPaddingCheckbox.setStyle("-fx-padding: 5px;");

        modeSection.getChildren().addAll(modeLabel, encryptRadio, decryptRadio, blockPaddingCheckbox);

        Button processBtn = new Button("Process");
        processBtn.setStyle("-fx-background-color: #4caf50; -fx-font-weight: bold; -fx-padding: 10px 20px; " +
                "-fx-font-size: 14px; -fx-background-radius: 4px;");
        processBtn.setOnAction(e -> processData());

        contentArea.getChildren().addAll(
                inputSection,
                keySection,
                modeSection,
                processBtn,
                outputLabel, outputTextArea,
                keyInfoLabel, keyInfoTextArea
        );

        return contentArea;
    }

    private void updateKeyLength(int length) {
        keyLengthLabel.setText(String.format("Key length: %d bytes", length));
        keyLengthLabel.setStyle("-fx-text-fill: #4caf50;");
    }

    private void updateInputLength(int length) {
        inputLengthLabel.setText("Length: " + length + " bytes");
    }

    private void displayKeyInfo(byte[] keyBytes) {
        StringBuilder info = new StringBuilder();

        info.append("Hex: ");
        for (byte b : keyBytes) {
            info.append(String.format("%02X", b)).append(" ");
        }
        info.append("\n");

        info.append("Binary: ");
        for (byte b : keyBytes) {
            info.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0')).append(" ");
        }

        keyInfoTextArea.setText(info.toString());
    }

    private void processData() {
        String input = inputTextArea.getText().trim();
        if (input.isEmpty()) {
            showError("No Input", "Please enter text to process.");
            return;
        }

        String keyText = keyField.getText().trim();
        if (keyText.isEmpty()) {
            showError("No Key", "Please enter an encryption key.");
            return;
        }

        RadioButton selectedMode = (RadioButton) modeGroup.getSelectedToggle();
        boolean isEncrypting = selectedMode.getText().equals("Encrypt");
        boolean useBlockPadding = blockPaddingCheckbox.isSelected();

        try {
            byte[] keyBytes = prepareKeyBytes(keyText);
            displayKeyInfo(keyBytes);

            byte[] adjustedKeyBytes = adjustKeySize(keyBytes);
            SecretKeySpec secretKey = new SecretKeySpec(adjustedKeyBytes, "DES");

            String transformation = "DES/ECB/" + (useBlockPadding ? "PKCS5Padding" : "NoPadding");

            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(isEncrypting ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey);

            byte[] result;
            if (isEncrypting) {
                String filteredInput = input.replaceAll("[^A-Za-z0-9]", "");
                byte[] inputBytes = filteredInput.getBytes(StandardCharsets.UTF_8);

                if (!useBlockPadding) {
                    int blockSize = 8;
                    inputBytes = padToMultipleOfBlockSize(inputBytes, blockSize);
                }

                result = cipher.doFinal(inputBytes);
                String encoded = Base64.getEncoder().encodeToString(result);

                encoded = encoded.replaceAll("=+$", "");

                outputTextArea.setText(encoded);
            } else {
                try {
                    String paddedInput = addBase64Padding(input);
                    byte[] inputBytes = Base64.getDecoder().decode(paddedInput);
                    result = cipher.doFinal(inputBytes);

                    String decrypted = new String(result, StandardCharsets.UTF_8);
                    outputTextArea.setText(decrypted);
                } catch (IllegalArgumentException e) {
                    throw new Exception("Invalid Base64 input for decryption.");
                }
            }

        } catch (InvalidKeyException e) {
            showError("Invalid Key", "The encryption key is invalid.");
        } catch (Exception e) {
            showError("Processing Error", "Error processing data: " + e.getMessage());
        }
    }

    private byte[] adjustKeySize(byte[] keyBytes) {
        byte[] result = new byte[8];

        if (keyBytes.length >= 8) {
             System.arraycopy(keyBytes, 0, result, 0, 8);
        } else {
             System.arraycopy(keyBytes, 0, result, 0, keyBytes.length);
         }

        return result;
    }

    private int getBlockSize(String algorithm) {
        return 8;
    }

    private String addBase64Padding(String input) {
        switch (input.length() % 4) {
            case 2: return input + "==";
            case 3: return input + "=";
            default: return input;
        }
    }

    private byte[] padToMultipleOfBlockSize(byte[] input, int blockSize) {
        if (input.length % blockSize == 0) {
            return input;
        }

        int paddedLength = ((input.length / blockSize) + 1) * blockSize;
        byte[] padded = new byte[paddedLength];
        System.arraycopy(input, 0, padded, 0, input.length);
        return padded;
    }

    private byte[] prepareKeyBytes(String keyText) {
        if (keyText.matches("[0-9A-Fa-f]+")) {
            int byteLength = (keyText.length() + 1) / 2;
            byte[] result = new byte[byteLength];

            for (int i = 0; i < keyText.length(); i += 2) {
                int end = Math.min(i + 2, keyText.length());
                String hexByte = keyText.substring(i, end);
                if (hexByte.length() == 2) {
                    result[i/2] = (byte) Integer.parseInt(hexByte, 16);
                } else if (hexByte.length() == 1) {
                    result[i/2] = (byte) Integer.parseInt(hexByte + "0", 16);
                }
            }
            return result;
        } else {
            return keyText.getBytes(StandardCharsets.UTF_8);
        }
    }

    private void showError(String title, String message) {
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