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

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Monoalphabetic_Cipher extends Application {

     private final String ALPHABETIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private List<Character> PLAIN = new ArrayList<>();
    private List<Character> CIPHER = new ArrayList<>();
    private String filePath = "monoalphabetic_results.txt";
    private File resultsFile;

    // UI Components
    private GridPane mappingGridPane;
    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private Label statusLabel;
    private ToggleGroup operationGroup;
    private TextField keyField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Monoalphabetic Cipher");

         createResultsFile();

         initializeFixedMapping();

         BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f7;");

         Label headerLabel = new Label("Monoalphabetic Cipher");
        headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#3a3a3c"));
        headerLabel.setPadding(new Insets(0, 0, 15, 0));

         VBox keySection = createKeySection();
//        VBox mappingSection = createMappingSection();
        VBox inputOutputSection = createInputOutputSection();
        HBox actionButtons = createActionButtons();

         VBox mainContent = new VBox(15);
        mainContent.getChildren().addAll(headerLabel, keySection, inputOutputSection, actionButtons);
        root.setCenter(mainContent);

         statusLabel = new Label("Ready");
        statusLabel.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(statusLabel);

         Scene scene = new Scene(root, 650, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createResultsFile() {
        resultsFile = new File(filePath);
        if (!resultsFile.exists()) {
            try {
                resultsFile.createNewFile();
                statusLabel.setText("Results file created: " + filePath);
            } catch (IOException e) {
                showAlert("Error", "Failed to create results file: " + e.getMessage());
            }
        }
    }

    private void initializeFixedMapping() {

        PLAIN.clear();
        for (int i = 0; i < ALPHABETIC.length(); i++) {
            PLAIN.add(ALPHABETIC.charAt(i));
        }

         CIPHER.clear();


        String fixedCipherAlphabet = "QZIHWOUYGCMKPRSAFEVJXBLNTD";


        for (int i = 0; i < fixedCipherAlphabet.length(); i++) {
            CIPHER.add(fixedCipherAlphabet.charAt(i));
        }

         saveMappingToResultsFile();
    }

    private VBox createKeySection() {
        // Key input
        Label keyLabel = new Label("Cipher Key (optional):");
        keyLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        keyField = new TextField();
        keyField.setPromptText("Using predefined mapping (key disabled)");
        keyField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        keyField.setDisable(true); // Disable key field as we're using fixed mapping

        HBox keyBox = new HBox(10);
        keyBox.getChildren().addAll(keyLabel, keyField);
        keyBox.setAlignment(Pos.CENTER_LEFT);

         Label operationLabel = new Label("Operation:");
        operationLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        operationGroup = new ToggleGroup();
        RadioButton encryptRadio = new RadioButton("Encrypt");
        encryptRadio.setToggleGroup(operationGroup);
        encryptRadio.setSelected(true);

        RadioButton decryptRadio = new RadioButton("Decrypt");
        decryptRadio.setToggleGroup(operationGroup);

        HBox radioBox = new HBox(20);
        radioBox.getChildren().addAll(operationLabel, encryptRadio, decryptRadio);

        VBox keySection = new VBox(15);
        keySection.getChildren().addAll(keyBox, radioBox);

        TitledPane keyPane = new TitledPane("Key & Operation", keySection);
        keyPane.setExpanded(true);
        keyPane.setStyle("-fx-font-weight: bold;");

        return new VBox(keyPane);
    }

    private VBox createMappingSection() {
        Label mappingHeaderLabel = new Label("Substitution Mapping");
        mappingHeaderLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

         mappingGridPane = new GridPane();
        mappingGridPane.setHgap(5);
        mappingGridPane.setVgap(10);
        mappingGridPane.setPadding(new Insets(15));
        mappingGridPane.setStyle("-fx-background-color: #e3e3e5; -fx-background-radius: 8;");

         updateMappingDisplay();

        VBox mappingContent = new VBox(10);
        mappingContent.getChildren().addAll(mappingHeaderLabel, mappingGridPane);

        TitledPane mappingPane = new TitledPane("Substitution Table (Fixed Mapping)", mappingContent);
        mappingPane.setExpanded(true);
        mappingPane.setStyle("-fx-font-weight: bold;");

        return new VBox(mappingPane);
    }

    private void updateMappingDisplay() {
        mappingGridPane.getChildren().clear();

         Label plainHeader = new Label("Plain");
        plainHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        Label cipherHeader = new Label("Cipher");
        cipherHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        mappingGridPane.add(plainHeader, 0, 0);
        mappingGridPane.add(cipherHeader, 1, 0);

         Separator separator = new Separator();
        separator.setPrefWidth(200);
        mappingGridPane.add(separator, 0, 1, 2, 1);

         int row = 2;
        for (int i = 0; i < PLAIN.size(); i++) {
            if (i % 13 == 0 && i > 0) {
                 row = 2;

                 Label plainChar = new Label(PLAIN.get(i).toString());
                plainChar.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                plainChar.setMinWidth(30);

                Label cipherChar = new Label(CIPHER.get(i).toString());
                cipherChar.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                cipherChar.setMinWidth(30);

                mappingGridPane.add(plainChar, 2, row);
                mappingGridPane.add(cipherChar, 3, row);
            } else {

                 Label plainChar = new Label(PLAIN.get(i).toString());
                plainChar.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                plainChar.setMinWidth(30);

                Label cipherChar = new Label(CIPHER.get(i).toString());
                cipherChar.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                cipherChar.setMinWidth(30);

                mappingGridPane.add(plainChar, 0, row);
                mappingGridPane.add(cipherChar, 1, row);
            }
            row++;
        }
    }

    private VBox createInputOutputSection() {

        Label inputLabel = new Label("Input Text:");
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        inputTextArea = new TextArea();
        inputTextArea.setPromptText("Enter text to encrypt/decrypt");
        inputTextArea.setPrefRowCount(5);
        inputTextArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

        HBox inputButtonBox = new HBox(10);
        inputButtonBox.getChildren().addAll(inputLabel);
        inputButtonBox.setAlignment(Pos.CENTER_LEFT);

        VBox inputSection = new VBox(10);
        inputSection.getChildren().addAll(inputButtonBox, inputTextArea);


        Label outputLabel = new Label("Output Text:");
        outputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setPrefRowCount(5);
        outputTextArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

        HBox outputButtonBox = new HBox(10);
        outputButtonBox.getChildren().addAll(outputLabel);
        outputButtonBox.setAlignment(Pos.CENTER_LEFT);

        VBox outputSection = new VBox(10);
        outputSection.getChildren().addAll(outputButtonBox, outputTextArea);

         VBox ioContent = new VBox(20);
        ioContent.getChildren().addAll(inputSection, outputSection);

        TitledPane ioPane = new TitledPane("Text Input/Output", ioContent);
        ioPane.setExpanded(true);
        ioPane.setStyle("-fx-font-weight: bold;");

        return new VBox(ioPane);
    }

    private HBox createActionButtons() {
        Button processButton = new Button("Process");
        processButton.setPrefWidth(120);
        processButton.setStyle("-fx-background-color: #0071e3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        processButton.setOnAction(e -> {
            processText();
            saveResultToFile();
        });

        Button clearButton = new Button("Clear All");
        clearButton.setPrefWidth(120);
        clearButton.setStyle("-fx-background-color: #5a5a5c; -fx-text-fill: white; -fx-background-radius: 8;");
        clearButton.setOnAction(e -> {
            inputTextArea.clear();
            outputTextArea.clear();
            statusLabel.setText("Input and output cleared.");
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));
        buttonBox.getChildren().addAll(processButton, clearButton);

        return buttonBox;
    }

    private void saveMappingToResultsFile() {
        try (FileWriter writer = new FileWriter(resultsFile, true)) {
            writer.write("\n--- FIXED MAPPING (" + new Date() + ") ---\n");
            for (int i = 0; i < PLAIN.size(); i++) {
                writer.write(PLAIN.get(i) + " -> " + CIPHER.get(i) + "\n");
            }
            writer.write("-------------------------\n");
        } catch (IOException e) {
            showAlert("Error", "Failed to save mapping to results file: " + e.getMessage());
        }
    }

    private void saveResultToFile() {
        try (FileWriter writer = new FileWriter(resultsFile, true)) {
            RadioButton selectedRadioButton = (RadioButton) operationGroup.getSelectedToggle();
            String operation = selectedRadioButton.getText();

            writer.write("\n--- " + operation.toUpperCase() + " OPERATION (" + new Date() + ") ---\n");
            writer.write("Input: " + inputTextArea.getText() + "\n");
            writer.write("Output: " + outputTextArea.getText() + "\n");
            writer.write("-------------------------\n");

            statusLabel.setText("Result saved to: " + filePath);
        } catch (IOException e) {
            showAlert("Error", "Failed to save result to file: " + e.getMessage());
        }
    }

    private void processText() {
        String text = inputTextArea.getText();

        if (text.isEmpty()) {
            showAlert("Error", "Please enter text to process");
            return;
        }

        // Determine operation
        RadioButton selectedRadioButton = (RadioButton) operationGroup.getSelectedToggle();
        String operation = selectedRadioButton.getText();

        // Process text and show result
        String result;
        if (operation.equals("Encrypt")) {
            result = encrypt(text);
            statusLabel.setText("Text encrypted successfully and saved to file.");
        } else {
            result = decrypt(text);
            statusLabel.setText("Text decrypted successfully and saved to file.");
        }

        outputTextArea.setText(result);
    }

    public String encrypt(String plaintext) {
        StringBuilder cipherText = new StringBuilder();
        for (char ch : plaintext.toCharArray()) {
            if (Character.isLetter(ch)) {
//                boolean isLower = Character.isLowerCase(ch);
                char upperCh = Character.toUpperCase(ch);

                int index = PLAIN.indexOf(upperCh);
                if (index != -1) {
                    char encryptedChar = CIPHER.get(index);
                    cipherText.append(encryptedChar);
                } else {
                    cipherText.append(ch); // Keep non-alphabetic characters as is
                }
            } else {
                cipherText.append(ch); // Keep non-alphabetic characters as is
            }
        }
        return cipherText.toString();
    }

    public String decrypt(String ciphertext) {
        StringBuilder plainText = new StringBuilder();
        for (char ch : ciphertext.toCharArray()) {
            if (Character.isLetter(ch)) {
//                boolean isLower = Character.isLowerCase(ch);
                char upperCh = Character.toUpperCase(ch);

                int index = CIPHER.indexOf(upperCh);
                if (index != -1) {
                    char decryptedChar = PLAIN.get(index);
                    plainText.append(decryptedChar);
                } else {
                    plainText.append(ch); // Keep non-alphabetic characters as is
                }
            } else {
                plainText.append(ch); // Keep non-alphabetic characters as is
            }
        }
        return plainText.toString();
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