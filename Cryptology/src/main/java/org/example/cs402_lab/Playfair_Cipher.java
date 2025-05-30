package org.example.cs402_lab;
import java.util.Set;
import java.util.HashSet;

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

public class Playfair_Cipher extends Application {

    private TextField keyField;
    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private GridPane matrixGridPane;
    private char[][] matrix = new char[5][5];
    private ToggleGroup operationGroup;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Playfair Cipher");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f7;");

        Label headerLabel = new Label("Playfair Cipher");
        headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#3a3a3c"));
        headerLabel.setPadding(new Insets(0, 0, 15, 0));

         VBox inputSection = createInputSection();

         VBox matrixSection = createMatrixSection();

         VBox outputSection = createOutputSection();

         HBox actionButtons = createActionButtons();

         VBox mainContent = new VBox(15);
        mainContent.getChildren().addAll(headerLabel, inputSection, matrixSection, outputSection, actionButtons);
        root.setCenter(mainContent);

         Label statusBar = new Label("Ready");
        statusBar.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(statusBar);

         Scene scene = new Scene(root, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createInputSection() {

         Label keyLabel = new Label("Key:");
        keyLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        keyField = new TextField();
        keyField.setPromptText("Enter key: ");
        keyField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

         Label operationLabel = new Label("Operation:");
        operationLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        operationGroup = new ToggleGroup();
        RadioButton encryptRadio = new RadioButton("Encrypt");
        encryptRadio.setToggleGroup(operationGroup);
        encryptRadio.setSelected(true);

        RadioButton decryptRadio = new RadioButton("Decrypt");
        decryptRadio.setToggleGroup(operationGroup);

        HBox radioBox = new HBox(20);
        radioBox.getChildren().addAll(encryptRadio, decryptRadio);

         Label inputLabel = new Label("Input Text:");
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        inputTextArea = new TextArea();
        inputTextArea.setPromptText("Enter text to encrypt/decrypt");
        inputTextArea.setPrefRowCount(3);
        inputTextArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

        VBox inputSection = new VBox(10);
        inputSection.getChildren().addAll(keyLabel, keyField, operationLabel, radioBox, inputLabel, inputTextArea);

        TitledPane inputPane = new TitledPane("Input", inputSection);
        inputPane.setExpanded(true);
        inputPane.setStyle("-fx-font-weight: bold;");

        VBox container = new VBox(inputPane);
        return container;
    }

    private VBox createMatrixSection() {
        Label matrixHeaderLabel = new Label("The Matrix");
        matrixHeaderLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

         matrixGridPane = new GridPane();
        matrixGridPane.setHgap(10);
        matrixGridPane.setVgap(10);
        matrixGridPane.setPadding(new Insets(15));
        matrixGridPane.setStyle("-fx-background-color: #e3e3e5; -fx-background-radius: 8;");

         resetMatrixDisplay();

        Button generateMatrixButton = new Button("Generate Matrix");
        generateMatrixButton.setStyle("-fx-background-color: #5a5a5c; -fx-text-fill: white; -fx-background-radius: 8;");
        generateMatrixButton.setOnAction(e -> generateAndDisplayMatrix());

        VBox matrixContent = new VBox(10);
        matrixContent.getChildren().addAll(matrixHeaderLabel, matrixGridPane, generateMatrixButton);

        TitledPane matrixPane = new TitledPane("Playfair Matrix", matrixContent);
        matrixPane.setExpanded(true);
        matrixPane.setStyle("-fx-font-weight: bold;");

        VBox container = new VBox(matrixPane);
        return container;
    }

    private void resetMatrixDisplay() {
        matrixGridPane.getChildren().clear();

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Label cellLabel = new Label(" ");
                cellLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
                cellLabel.setMinWidth(40);
                cellLabel.setMinHeight(40);
                cellLabel.setAlignment(Pos.CENTER);
                cellLabel.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: #cccccc;");

                matrixGridPane.add(cellLabel, j, i);
            }
        }

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = ' ';
            }
        }
    }

    private VBox createOutputSection() {
        Label outputLabel = new Label("Output Text:");
        outputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setPrefRowCount(3);
        outputTextArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

        VBox outputContent = new VBox(10);
        outputContent.getChildren().addAll(outputLabel, outputTextArea);

        TitledPane outputPane = new TitledPane("Output", outputContent);
        outputPane.setExpanded(true);
        outputPane.setStyle("-fx-font-weight: bold;");

        VBox container = new VBox(outputPane);
        return container;
    }

    private HBox createActionButtons() {
        Button processButton = new Button("Process");
        processButton.setPrefWidth(120);
        processButton.setStyle("-fx-background-color: #0071e3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        processButton.setOnAction(e -> processText());

        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(120);
        clearButton.setStyle("-fx-background-color: #5a5a5c; -fx-text-fill: white; -fx-background-radius: 8;");
        clearButton.setOnAction(e -> {
            keyField.clear();
            inputTextArea.clear();
            outputTextArea.clear();
            resetMatrixDisplay();
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));
        buttonBox.getChildren().addAll(processButton, clearButton);

        return buttonBox;
    }

    private void generateAndDisplayMatrix() {
        String key = keyField.getText();

        if (key.isEmpty()) {
            showAlert("Error", "Please enter a key");
            return;
        }

        generateMatrix(key);
        displayMatrix();
    }

    private void generateMatrix() {
        generateMatrix(keyField.getText());
    }

    private void generateMatrix(String key) {

         key = key.toUpperCase().replaceAll("[^A-Z]", "");

         key = key.replaceAll("J", "I");

         Set<Character> usedChars = new HashSet<>();

         StringBuilder keyMatrix = new StringBuilder();

         for (char c : key.toCharArray()) {
            if (!usedChars.contains(c)) {
                usedChars.add(c);
                keyMatrix.append(c);
            }
        }

         for (char c = 'A'; c <= 'Z'; c++) {
            if (c != 'J' && !usedChars.contains(c)) {
                usedChars.add(c);
                keyMatrix.append(c);
            }
        }

         int index = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = keyMatrix.charAt(index++);
            }
        }

         StringBuilder matrixInfo = new StringBuilder();
        matrixInfo.append("Matrix generated with key: ").append(key).append("\n\n");
        matrixInfo.append("Playfair Matrix:\n");

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrixInfo.append(matrix[i][j]).append(" ");
            }
            matrixInfo.append("\n");
        }

        outputTextArea.setText(matrixInfo.toString());
    }

    private void displayMatrix() {
        matrixGridPane.getChildren().clear();

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Label cellLabel = new Label(String.valueOf(matrix[i][j]));
                cellLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
                cellLabel.setMinWidth(40);
                cellLabel.setMinHeight(40);
                cellLabel.setAlignment(Pos.CENTER);
                cellLabel.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: #cccccc;");

                matrixGridPane.add(cellLabel, j, i);
            }
        }
    }

    private void processText() {
        String key = keyField.getText();
        String text = inputTextArea.getText();

        if (key.isEmpty() || text.isEmpty()) {
            showAlert("Error", "Please enter both key and text");
            return;
        }

         generateMatrix();
        displayMatrix();

         RadioButton selectedRadioButton = (RadioButton) operationGroup.getSelectedToggle();
        String operation = selectedRadioButton.getText();

         StringBuilder resultDetails = new StringBuilder();

         resultDetails.append("Matrix generated with key: ").append(key.toUpperCase()).append("\n\n");
         resultDetails.append("Playfair Matrix:\n");

//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 5; j++) {
//                resultDetails.append(matrix[i][j]).append(" ");
//            }
//            resultDetails.append("\n");
//        }
//        resultDetails.append("\n");

        String result;

        if (operation.equals("Encrypt")) {
            result = encrypt(text, resultDetails);
        } else {
            result = decrypt(text, resultDetails);
        }

        outputTextArea.setText(resultDetails.toString() + "\n\n result: " + result);
    }

    private String encrypt(String text, StringBuilder details) {

         text = text.toUpperCase().replaceAll("J", "I").replaceAll("[^A-Z]", "");
        details.append("text: ").append(text).append("\n\n");

         StringBuilder preprocessed = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            preprocessed.append(text.charAt(i));

            if (i + 1 < text.length()) {
                if (text.charAt(i) == text.charAt(i + 1)) {
                    preprocessed.append('X');
                    details.append("Inserted X between duplicate letters: ").append(text.charAt(i)).append(text.charAt(i)).append("\n");
                }
            }
        }

        if (preprocessed.length() % 2 != 0) {
            preprocessed.append('X');
            details.append("Added X at the end to make even length\n");
        }

        String preparedText = preprocessed.toString();
        details.append("\nPrepared text ").append(preparedText).append("\n\n");
        details.append("Encryption steps:\n");

        StringBuilder encryptedText = new StringBuilder();

         for (int i = 0; i < preparedText.length(); i += 2) {
            char first = preparedText.charAt(i);
            char second = preparedText.charAt(i + 1);

            int[] posFirst = findPosition(first);
            int[] posSecond = findPosition(second);

            char encFirst, encSecond;

            details.append("Digraph ").append(first).append(second).append(": ");

            if (posFirst[0] == posSecond[0]) {
                // Move right, wrapping around
                encFirst = matrix[posFirst[0]][(posFirst[1] + 1) % 5];
                encSecond = matrix[posSecond[0]][(posSecond[1] + 1) % 5];
                details.append("Same row - shift right → ").append(encFirst).append(encSecond).append("\n");
            }
            else if (posFirst[1] == posSecond[1]) {
                // Move down, wrapping around
                encFirst = matrix[(posFirst[0] + 1) % 5][posFirst[1]];
                encSecond = matrix[(posSecond[0] + 1) % 5][posSecond[1]];
                details.append("Same column - shift down → ").append(encFirst).append(encSecond).append("\n");
            }
            else {
                encFirst = matrix[posFirst[0]][posSecond[1]];
                encSecond = matrix[posSecond[0]][posFirst[1]];
                details.append("Rectangle - use corners → ").append(encFirst).append(encSecond).append("\n");
            }

            encryptedText.append(encFirst).append(encSecond);
        }

         StringBuilder formattedOutput = new StringBuilder();
        for (int i = 0; i < encryptedText.length(); i++) {
            formattedOutput.append(encryptedText.charAt(i));
            if ((i + 1) % 5 == 0 && i < encryptedText.length() - 1) {
                formattedOutput.append(" ");
            }
        }

        return formattedOutput.toString();
    }

    private String decrypt(String text, StringBuilder details) {
        // Preprocessing: convert to uppercase, replace J with I, and remove non-alphabetic chars and spaces
        text = text.toUpperCase().replaceAll("J", "I").replaceAll("[^A-Z]", "");
        details.append("Preprocessed ciphertext: ").append(text).append("\n\n");

        if (text.length() % 2 != 0) {
            showAlert("Error", "Ciphertext length must be even for decryption");
            return "";
        }

        details.append("Decryption steps:\n");
        StringBuilder decryptedText = new StringBuilder();

         for (int i = 0; i < text.length(); i += 2) {
            char first = text.charAt(i);
            char second = text.charAt(i + 1);

            int[] posFirst = findPosition(first);
            int[] posSecond = findPosition(second);

            char decFirst, decSecond;

            details.append("Digraph ").append(first).append(second).append(": ");

            if (posFirst[0] == posSecond[0]) {

                 decFirst = matrix[posFirst[0]][(posFirst[1] + 4) % 5];
                decSecond = matrix[posSecond[0]][(posSecond[1] + 4) % 5];
                details.append("Same row - shift left → ").append(decFirst).append(decSecond).append("\n");
            }
            else if (posFirst[1] == posSecond[1]) {

                 decFirst = matrix[(posFirst[0] + 4) % 5][posFirst[1]];
                decSecond = matrix[(posSecond[0] + 4) % 5][posSecond[1]];
                details.append("Same column - shift up → ").append(decFirst).append(decSecond).append("\n");
            }
            else {
                decFirst = matrix[posFirst[0]][posSecond[1]];
                decSecond = matrix[posSecond[0]][posFirst[1]];
                details.append("Rectangle - use corners → ").append(decFirst).append(decSecond).append("\n");
            }

            decryptedText.append(decFirst).append(decSecond);
        }

         StringBuilder processedText = new StringBuilder(decryptedText);

        details.append("\nRaw decrypted text: ").append(decryptedText).append("\n");
        details.append("Removing inserted X characters...\n");

         for (int i = 1; i < processedText.length() - 1; i++) {
            if (processedText.charAt(i) == 'X' &&
                    i-1 >= 0 && i+1 < processedText.length() &&
                    processedText.charAt(i-1) == processedText.charAt(i+1)) {
                processedText.deleteCharAt(i);
                details.append("Removed X between repeated letters at position ").append(i).append("\n");
                i--;
            }
        }

         if (processedText.length() > 0 && processedText.charAt(processedText.length() - 1) == 'X') {
            processedText.deleteCharAt(processedText.length() - 1);
            details.append("Removed trailing X\n");
        }

        return processedText.toString();
    }

    private int[] findPosition(char c) {
        int[] position = new int[2];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == c) {
                    position[0] = i;
                    position[1] = j;
                    return position;
                }
            }
        }

         showAlert("Error", "Character '" + c + "' not found in matrix. Ensure matrix is properly generated.");

         return position;
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