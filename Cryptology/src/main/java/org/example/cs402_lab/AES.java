package org.example.cs402_lab;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AES extends Application {

    private TextField plaintextField;
    private TextField keyField;
    private TextArea outputArea;
    private File outputFile;
    private final AES128 aes = new AES128();

    @Override
    public void start(Stage primaryStage) {
        // Create main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create input controls
        VBox inputBox = new VBox(10);
        inputBox.setPadding(new Insets(10));

        Label plaintextLabel = new Label("Plaintext (Hex - 16 bytes / 32 hex chars):");
        plaintextField = new TextField();
        plaintextField.setPromptText("Enter plaintext in hex (e.g., 00112233445566778899AABBCCDDEEFF)");

        Label keyLabel = new Label("Key (Hex - 16 bytes / 32 hex chars):");
        keyField = new TextField();
        keyField.setPromptText("Enter key in hex (e.g., 000102030405060708090A0B0C0D0E0F)");

        Button encryptButton = new Button("Encrypt");
        encryptButton.setOnAction(e -> encryptData());

        Button decryptButton = new Button("Decrypt");
        decryptButton.setOnAction(e -> decryptData());

        Button selectFileButton = new Button("Select Output File");
        selectFileButton.setOnAction(e -> selectOutputFile(primaryStage));

        inputBox.getChildren().addAll(plaintextLabel, plaintextField, keyLabel, keyField,
                encryptButton, decryptButton, selectFileButton);

        // Create output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);

        // Add components to layout
        root.setTop(inputBox);
        root.setCenter(outputArea);

        // Create scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("AES-128 Encryption/Decryption");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void encryptData() {
        try {
            // Get plaintext and key
            byte[] plaintext = hexStringToByteArray(plaintextField.getText().trim());
            byte[] key = hexStringToByteArray(keyField.getText().trim());

            // Validate input
            if (plaintext.length != 16) {
                showAlert("Error", "Plaintext must be exactly 16 bytes (32 hex characters)");
                return;
            }
            if (key.length != 16) {
                showAlert("Error", "Key must be exactly 16 bytes (32 hex characters)");
                return;
            }

            // Perform encryption and collect output
            StringBuilder result = new StringBuilder();
            result.append("============ AES-128 ENCRYPTION ============\n");
            result.append("Plaintext: ").append(byteArrayToHexString(plaintext)).append("\n");
            result.append("Key: ").append(byteArrayToHexString(key)).append("\n\n");

            // Generate key schedule and encrypt
            List<String> roundOutputs = new ArrayList<>();
            byte[] ciphertext = aes.encrypt(plaintext, key, roundOutputs);

            for (String output : roundOutputs) {
                result.append(output).append("\n");
            }

            result.append("Ciphertext: ").append(byteArrayToHexString(ciphertext)).append("\n");
            result.append("===========================================\n");

            // Display output
            outputArea.setText(result.toString());

            // Save to file if file is selected
            if (outputFile != null) {
                saveToFile(result.toString());
            }

        } catch (Exception e) {
            showAlert("Error", "Error during encryption: " + e.getMessage());
        }
    }

    private void decryptData() {
        try {
            // Get ciphertext and key
            byte[] ciphertext = hexStringToByteArray(plaintextField.getText().trim());
            byte[] key = hexStringToByteArray(keyField.getText().trim());

            // Validate input
            if (ciphertext.length != 16) {
                showAlert("Error", "Ciphertext must be exactly 16 bytes (32 hex characters)");
                return;
            }
            if (key.length != 16) {
                showAlert("Error", "Key must be exactly 16 bytes (32 hex characters)");
                return;
            }

            // Perform decryption and collect output
            StringBuilder result = new StringBuilder();
            result.append("============ AES-128 DECRYPTION ============\n");
            result.append("Ciphertext: ").append(byteArrayToHexString(ciphertext)).append("\n");
            result.append("Key: ").append(byteArrayToHexString(key)).append("\n\n");

            // Generate key schedule and decrypt
            List<String> roundOutputs = new ArrayList<>();
            byte[] plaintext = aes.decrypt(ciphertext, key, roundOutputs);

            for (String output : roundOutputs) {
                result.append(output).append("\n");
            }

            result.append("Plaintext: ").append(byteArrayToHexString(plaintext)).append("\n");
            result.append("===========================================\n");

            // Display output
            outputArea.setText(result.toString());

            // Save to file if file is selected
            if (outputFile != null) {
                saveToFile(result.toString());
            }

        } catch (Exception e) {
            showAlert("Error", "Error during decryption: " + e.getMessage());
        }
    }

    private void selectOutputFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Output File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        outputFile = fileChooser.showSaveDialog(primaryStage);

        if (outputFile != null) {
            outputArea.appendText("\nOutput will be saved to: " + outputFile.getAbsolutePath() + "\n");
        }
    }

    private void saveToFile(String content) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(content);
            outputArea.appendText("Output saved to file successfully.\n");
        } catch (IOException e) {
            showAlert("Error", "Failed to save to file: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static byte[] hexStringToByteArray(String hex) {
        // Remove any spaces or other formatting
        hex = hex.replaceAll("\\s", "");

        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }

        return data;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// AES-128 Implementation Class
class AES128 {
    // S-box for SubBytes transformation
    private static final int[] S_BOX = {
            0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
            0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
            0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
            0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
            0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
            0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
            0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
            0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
            0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
            0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
            0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
            0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
            0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
            0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
            0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
            0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };

    // Inverse S-box for InvSubBytes transformation
    private static final int[] INV_S_BOX = {
            0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
            0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
            0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
            0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
            0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
            0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
            0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
            0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
            0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
            0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
            0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
            0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
            0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
            0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
            0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
            0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d
    };

    // Round constants for key expansion
    private static final int[] RCON = {
            0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36
    };

    // Number of rounds for AES-128
    private static final int NR = 10;

    /**
     * Encrypts a 16-byte plaintext using AES-128 algorithm
     * @param plaintext The plaintext to encrypt (16 bytes)
     * @param key The encryption key (16 bytes)
     * @param roundOutputs List to store round outputs
     * @return The encrypted ciphertext (16 bytes)
     */
    public byte[] encrypt(byte[] plaintext, byte[] key, List<String> roundOutputs) {
        // Convert plaintext to state array (4x4 matrix)
        byte[][] state = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = plaintext[i * 4 + j];
            }
        }

        // Key expansion
        byte[][] expandedKey = keyExpansion(key, roundOutputs);

        // Initial round: AddRoundKey
        StringBuilder round0Output = new StringBuilder();
        round0Output.append("Round 0 (AddRoundKey):\n");

        byte[][] roundKey = extractRoundKey(expandedKey, 0);
        state = addRoundKey(state, roundKey);
        round0Output.append(stateToString(state));
        roundOutputs.add(round0Output.toString());

        // Main rounds (1 to Nr-1)
        for (int round = 1; round < NR; round++) {
            StringBuilder roundOutput = new StringBuilder();
            roundOutput.append("Round ").append(round).append(":\n");

            // SubBytes
            roundOutput.append("SubBytes:\n");
            state = subBytes(state);
            roundOutput.append(stateToString(state)).append("\n");

            // ShiftRows
            roundOutput.append("ShiftRows:\n");
            state = shiftRows(state);
            roundOutput.append(stateToString(state)).append("\n");

            // MixColumns
            roundOutput.append("MixColumns:\n");
            state = mixColumns(state);
            roundOutput.append(stateToString(state)).append("\n");

            // AddRoundKey
            roundOutput.append("AddRoundKey:\n");
            roundKey = extractRoundKey(expandedKey, round);
            state = addRoundKey(state, roundKey);
            roundOutput.append(stateToString(state));

            roundOutputs.add(roundOutput.toString());
        }

        // Final round (Nr): SubBytes, ShiftRows, AddRoundKey (no MixColumns)
        StringBuilder finalRoundOutput = new StringBuilder();
        finalRoundOutput.append("Round ").append(NR).append(" (Final):\n");

        // SubBytes
        finalRoundOutput.append("SubBytes:\n");
        state = subBytes(state);
        finalRoundOutput.append(stateToString(state)).append("\n");

        // ShiftRows
        finalRoundOutput.append("ShiftRows:\n");
        state = shiftRows(state);
        finalRoundOutput.append(stateToString(state)).append("\n");

        // AddRoundKey
        finalRoundOutput.append("AddRoundKey:\n");
        roundKey = extractRoundKey(expandedKey, NR);
        state = addRoundKey(state, roundKey);
        finalRoundOutput.append(stateToString(state));

        roundOutputs.add(finalRoundOutput.toString());

        // Convert state back to byte array
        byte[] ciphertext = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                ciphertext[i * 4 + j] = state[j][i];
            }
        }

        return ciphertext;
    }

    /**
     * Decrypts a 16-byte ciphertext using AES-128 algorithm
     * @param ciphertext The ciphertext to decrypt (16 bytes)
     * @param key The decryption key (16 bytes)
     * @param roundOutputs List to store round outputs
     * @return The decrypted plaintext (16 bytes)
     */
    public byte[] decrypt(byte[] ciphertext, byte[] key, List<String> roundOutputs) {
        // Convert ciphertext to state array (4x4 matrix)
        byte[][] state = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = ciphertext[i * 4 + j];
            }
        }

        // Key expansion
        byte[][] expandedKey = keyExpansion(key, roundOutputs);

        // Initial round: AddRoundKey with last round key
        StringBuilder round0Output = new StringBuilder();
        round0Output.append("Round 0 (AddRoundKey):\n");

        byte[][] roundKey = extractRoundKey(expandedKey, NR);
        state = addRoundKey(state, roundKey);
        round0Output.append(stateToString(state));
        roundOutputs.add(round0Output.toString());

        // Main rounds (1 to Nr-1) in reverse
        for (int round = 1; round < NR; round++) {
            StringBuilder roundOutput = new StringBuilder();
            roundOutput.append("Round ").append(round).append(":\n");

            // InvShiftRows
            roundOutput.append("InvShiftRows:\n");
            state = invShiftRows(state);
            roundOutput.append(stateToString(state)).append("\n");

            // InvSubBytes
            roundOutput.append("InvSubBytes:\n");
            state = invSubBytes(state);
            roundOutput.append(stateToString(state)).append("\n");

            // AddRoundKey
            roundOutput.append("AddRoundKey:\n");
            roundKey = extractRoundKey(expandedKey, NR - round);
            state = addRoundKey(state, roundKey);
            roundOutput.append(stateToString(state)).append("\n");

            // InvMixColumns
            roundOutput.append("InvMixColumns:\n");
            state = invMixColumns(state);
            roundOutput.append(stateToString(state));

            roundOutputs.add(roundOutput.toString());
        }

        // Final round (Nr): InvShiftRows, InvSubBytes, AddRoundKey (no InvMixColumns)
        StringBuilder finalRoundOutput = new StringBuilder();
        finalRoundOutput.append("Round ").append(NR).append(" (Final):\n");

        // InvShiftRows
        finalRoundOutput.append("InvShiftRows:\n");
        state = invShiftRows(state);
        finalRoundOutput.append(stateToString(state)).append("\n");

        // InvSubBytes
        finalRoundOutput.append("InvSubBytes:\n");
        state = invSubBytes(state);
        finalRoundOutput.append(stateToString(state)).append("\n");

        // AddRoundKey
        finalRoundOutput.append("AddRoundKey:\n");
        roundKey = extractRoundKey(expandedKey, 0);
        state = addRoundKey(state, roundKey);
        finalRoundOutput.append(stateToString(state));

        roundOutputs.add(finalRoundOutput.toString());

        // Convert state back to byte array
        byte[] plaintext = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                plaintext[i * 4 + j] = state[j][i];
            }
        }

        return plaintext;
    }

    /**
     * Extracts a round key from the expanded key
     * @param expandedKey The expanded key
     * @param round The round number
     * @return The round key as a 4x4 matrix
     */
    private byte[][] extractRoundKey(byte[][] expandedKey, int round) {
        byte[][] roundKey = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                roundKey[i][j] = expandedKey[i][round * 4 + j];
            }
        }
        return roundKey;
    }

    /**
     * Performs the key expansion for AES-128
     * @param key The 16-byte key
     * @param roundOutputs List to store round outputs
     * @return The expanded key as a 4x44 matrix (11 round keys)
     */
    private byte[][] keyExpansion(byte[] key, List<String> roundOutputs) {
        StringBuilder keyExpansionOutput = new StringBuilder();
        keyExpansionOutput.append("Key Expansion:\n");

        // Initialize the first four words (columns) of the expanded key with the input key
        byte[][] w = new byte[4][4 * (NR + 1)];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                w[j][i] = key[i * 4 + j];
            }
        }

        keyExpansionOutput.append("Initial key words (w[0-3]):\n");
        for (int i = 0; i < 4; i++) {
            keyExpansionOutput.append(String.format("w[%d]: ", i));
            for (int j = 0; j < 4; j++) {
                keyExpansionOutput.append(String.format("%02X ", w[j][i] & 0xff));
            }
            keyExpansionOutput.append("\n");
        }
        keyExpansionOutput.append("\n");

        // Expand the key
        for (int i = 4; i < 4 * (NR + 1); i++) {
            // Copy the previous word
            byte[] temp = new byte[4];
            for (int j = 0; j < 4; j++) {
                temp[j] = w[j][i - 1];
            }

            // Every fourth word undergoes special treatment
            if (i % 4 == 0) {
                keyExpansionOutput.append(String.format("w[%d] calculation (i mod 4 = 0):\n", i));

                // RotWord operation: left circular shift by one byte
                keyExpansionOutput.append("RotWord(temp): ");
                byte t = temp[0];
                for (int j = 0; j < 3; j++) {
                    temp[j] = temp[j + 1];
                    keyExpansionOutput.append(String.format("%02X ", temp[j] & 0xff));
                }
                temp[3] = t;
                keyExpansionOutput.append(String.format("%02X\n", temp[3] & 0xff));

                // SubWord operation: apply S-box to each byte
                keyExpansionOutput.append("SubWord(RotWord): ");
                for (int j = 0; j < 4; j++) {
                    temp[j] = (byte) S_BOX[temp[j] & 0xff];
                    keyExpansionOutput.append(String.format("%02X ", temp[j] & 0xff));
                }
                keyExpansionOutput.append("\n");

                // XOR with Rcon
                keyExpansionOutput.append(String.format("XOR with Rcon[%d/4]: ", i));
                temp[0] ^= RCON[i / 4 - 1];for (int j = 0; j < 4; j++) {
                    if (j == 0) {
                        keyExpansionOutput.append(String.format("%02X ", temp[j] & 0xff));
                    } else {
                        keyExpansionOutput.append(String.format("%02X ", 0x00));
                    }
                }
                keyExpansionOutput.append("\n");
            }

            // Calculate the new word: w[i] = w[i-4] XOR temp
            keyExpansionOutput.append(String.format("w[%d] = w[%d] XOR temp: ", i, i - 4));
            for (int j = 0; j < 4; j++) {
                w[j][i] = (byte) (w[j][i - 4] ^ temp[j]);
                keyExpansionOutput.append(String.format("%02X ", w[j][i] & 0xff));
            }
            keyExpansionOutput.append("\n\n");
        }

        return w;
    }

    /**
     * Applies SubBytes transformation to the state matrix
     * @param state The state matrix
     * @return The transformed state matrix
     */
    private byte[][] subBytes(byte[][] state) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = (byte) S_BOX[state[i][j] & 0xff];
            }
        }
        return state;
    }

    /**
     * Applies InvSubBytes transformation to the state matrix
     * @param state The state matrix
     * @return The transformed state matrix
     */
    private byte[][] invSubBytes(byte[][] state) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = (byte) INV_S_BOX[state[i][j] & 0xff];
            }
        }
        return state;
    }

    /**
     * Applies ShiftRows transformation to the state matrix
     * @param state The state matrix
     * @return The transformed state matrix
     */
    private byte[][] shiftRows(byte[][] state) {
        byte[][] newState = new byte[4][4];

        // Row 0: No shift
        for (int j = 0; j < 4; j++) {
            newState[0][j] = state[0][j];
        }

        // Row 1: Shift left by 1
        for (int j = 0; j < 4; j++) {
            newState[1][j] = state[1][(j + 1) % 4];
        }

        // Row 2: Shift left by 2
        for (int j = 0; j < 4; j++) {
            newState[2][j] = state[2][(j + 2) % 4];
        }

        // Row 3: Shift left by 3
        for (int j = 0; j < 4; j++) {
            newState[3][j] = state[3][(j + 3) % 4];
        }

        return newState;
    }

    /**
     * Applies InvShiftRows transformation to the state matrix
     * @param state The state matrix
     * @return The transformed state matrix
     */
    private byte[][] invShiftRows(byte[][] state) {
        byte[][] newState = new byte[4][4];

        // Row 0: No shift
        for (int j = 0; j < 4; j++) {
            newState[0][j] = state[0][j];
        }

        // Row 1: Shift right by 1
        for (int j = 0; j < 4; j++) {
            newState[1][j] = state[1][(j + 3) % 4];
        }

        // Row 2: Shift right by 2
        for (int j = 0; j < 4; j++) {
            newState[2][j] = state[2][(j + 2) % 4];
        }

        // Row 3: Shift right by 3
        for (int j = 0; j < 4; j++) {
            newState[3][j] = state[3][(j + 1) % 4];
        }

        return newState;
    }

    /**
     * Multiplies two bytes in GF(2^8)
     * @param a First byte
     * @param b Second byte
     * @return Product in GF(2^8)
     */
    private byte gmul(byte a, byte b) {
        byte p = 0;
        byte high_bit;

        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) {
                p ^= a;
            }

            high_bit = (byte) (a & 0x80);
            a <<= 1;
            if (high_bit != 0) {
                a ^= 0x1b; // x^8 + x^4 + x^3 + x + 1
            }
            b >>= 1;
        }

        return p;
    }

    /**
     * Applies MixColumns transformation to the state matrix
     * @param state The state matrix
     * @return The transformed state matrix
     */
    private byte[][] mixColumns(byte[][] state) {
        byte[][] result = new byte[4][4];

        for (int j = 0; j < 4; j++) {
            result[0][j] = (byte) (gmul((byte) 0x02, state[0][j]) ^
                    gmul((byte) 0x03, state[1][j]) ^
                    state[2][j] ^
                    state[3][j]);

            result[1][j] = (byte) (state[0][j] ^
                    gmul((byte) 0x02, state[1][j]) ^
                    gmul((byte) 0x03, state[2][j]) ^
                    state[3][j]);

            result[2][j] = (byte) (state[0][j] ^
                    state[1][j] ^
                    gmul((byte) 0x02, state[2][j]) ^
                    gmul((byte) 0x03, state[3][j]));

            result[3][j] = (byte) (gmul((byte) 0x03, state[0][j]) ^
                    state[1][j] ^
                    state[2][j] ^
                    gmul((byte) 0x02, state[3][j]));
        }

        return result;
    }

    /**
     * Applies InvMixColumns transformation to the state matrix
     * @param state The state matrix
     * @return The transformed state matrix
     */
    private byte[][] invMixColumns(byte[][] state) {
        byte[][] result = new byte[4][4];

        for (int j = 0; j < 4; j++) {
            result[0][j] = (byte) (gmul((byte) 0x0e, state[0][j]) ^
                    gmul((byte) 0x0b, state[1][j]) ^
                    gmul((byte) 0x0d, state[2][j]) ^
                    gmul((byte) 0x09, state[3][j]));

            result[1][j] = (byte) (gmul((byte) 0x09, state[0][j]) ^
                    gmul((byte) 0x0e, state[1][j]) ^
                    gmul((byte) 0x0b, state[2][j]) ^
                    gmul((byte) 0x0d, state[3][j]));

            result[2][j] = (byte) (gmul((byte) 0x0d, state[0][j]) ^
                    gmul((byte) 0x09, state[1][j]) ^
                    gmul((byte) 0x0e, state[2][j]) ^
                    gmul((byte) 0x0b, state[3][j]));

            result[3][j] = (byte) (gmul((byte) 0x0b, state[0][j]) ^
                    gmul((byte) 0x0d, state[1][j]) ^
                    gmul((byte) 0x09, state[2][j]) ^
                    gmul((byte) 0x0e, state[3][j]));
        }

        return result;
    }

    /**
     * Applies AddRoundKey transformation to the state matrix
     * @param state The state matrix
     * @param roundKey The round key
     * @return The transformed state matrix
     */
    private byte[][] addRoundKey(byte[][] state, byte[][] roundKey) {
        byte[][] result = new byte[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = (byte) (state[i][j] ^ roundKey[i][j]);
            }
        }

        return result;
    }

    /**
     * Converts a state matrix to a string representation for display
     * @param state The state matrix
     * @return String representation of the state matrix
     */
    private String stateToString(byte[][] state) {
        StringBuilder result = new StringBuilder();

        result.append("┌─────────────────────────┐\n");
        for (int i = 0; i < 4; i++) {
            result.append("│ ");
            for (int j = 0; j < 4; j++) {
                result.append(String.format("%02X ", state[i][j] & 0xff));
            }
            result.append("│\n");
        }
        result.append("└─────────────────────────┘");

        return result.toString();
    }
}

/**
 * Class to handle multiple blocks of data using ECB mode
 */
class AESBlockHandler {
    private final AES128 aes;

    public AESBlockHandler() {
        aes = new AES128();
    }

    /**
     * Encrypts multiple blocks of data using ECB mode
     * @param data The data to encrypt
     * @param key The encryption key (16 bytes)
     * @param outputFile File to write output to
     * @return The encrypted data
     */
    public byte[] encryptBlocks(byte[] data, byte[] key, String outputFile) throws IOException {
        // Pad data to multiple of 16 bytes if needed
        byte[] paddedData = padData(data);

        // Prepare output file if specified
        FileWriter writer = null;
        if (outputFile != null) {
            writer = new FileWriter(outputFile);
            writer.write("============== AES-128 BLOCK ENCRYPTION ==============\n");
            writer.write("Key: " + byteArrayToHexString(key) + "\n\n");
        }

        byte[] result = new byte[paddedData.length];
        int numBlocks = paddedData.length / 16;

        // Process each block
        for (int i = 0; i < numBlocks; i++) {
            byte[] block = new byte[16];
            System.arraycopy(paddedData, i * 16, block, 0, 16);

            if (writer != null) {
                writer.write("Block " + (i + 1) + " Plaintext: " + byteArrayToHexString(block) + "\n");
            }

            // Encrypt the block
            List<String> roundOutputs = new ArrayList<>();
            byte[] encryptedBlock = aes.encrypt(block, key, roundOutputs);

            // Copy to result
            System.arraycopy(encryptedBlock, 0, result, i * 16, 16);

            if (writer != null) {
                writer.write("Block " + (i + 1) + " Ciphertext: " + byteArrayToHexString(encryptedBlock) + "\n\n");
            }
        }

        if (writer != null) {
            writer.write("==================================================\n");
            writer.close();
        }

        return result;
    }

    /**
     * Decrypts multiple blocks of data using ECB mode
     * @param data The data to decrypt
     * @param key The decryption key (16 bytes)
     * @param outputFile File to write output to
     * @return The decrypted data
     */
    public byte[] decryptBlocks(byte[] data, byte[] key, String outputFile) throws IOException {
        if (data.length % 16 != 0) {
            throw new IllegalArgumentException("Encrypted data length must be a multiple of 16 bytes");
        }

        // Prepare output file if specified
        FileWriter writer = null;
        if (outputFile != null) {
            writer = new FileWriter(outputFile);
            writer.write("============== AES-128 BLOCK DECRYPTION ==============\n");
            writer.write("Key: " + byteArrayToHexString(key) + "\n\n");
        }

        byte[] result = new byte[data.length];
        int numBlocks = data.length / 16;

        // Process each block
        for (int i = 0; i < numBlocks; i++) {
            byte[] block = new byte[16];
            System.arraycopy(data, i * 16, block, 0, 16);

            if (writer != null) {
                writer.write("Block " + (i + 1) + " Ciphertext: " + byteArrayToHexString(block) + "\n");
            }

            // Decrypt the block
            List<String> roundOutputs = new ArrayList<>();
            byte[] decryptedBlock = aes.decrypt(block, key, roundOutputs);

            // Copy to result
            System.arraycopy(decryptedBlock, 0, result, i * 16, 16);

            if (writer != null) {
                writer.write("Block " + (i + 1) + " Plaintext: " + byteArrayToHexString(decryptedBlock) + "\n\n");
            }
        }

        if (writer != null) {
            writer.write("==================================================\n");
            writer.close();
        }

        // Remove padding
        return removePadding(result);
    }

    /**
     * Pads data to be a multiple of 16 bytes using PKCS#7 padding
     * @param data The data to pad
     * @return The padded data
     */
    private byte[] padData(byte[] data) {
        int paddingLength = 16 - (data.length % 16);
        if (paddingLength == 0) {
            paddingLength = 16; // If data length is already a multiple of 16, add a full block of padding
        }

        byte[] paddedData = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedData, 0, data.length);

        // Add padding bytes
        for (int i = data.length; i < paddedData.length; i++) {
            paddedData[i] = (byte) paddingLength;
        }

        return paddedData;
    }


    private byte[] removePadding(byte[] data) {
        int paddingLength = data[data.length - 1] & 0xff;
        if (paddingLength > 16 || paddingLength == 0) {
            // Invalid padding, return data as is
            return data;
        }

        // Verify padding
        for (int i = data.length - paddingLength; i < data.length; i++) {
            if ((data[i] & 0xff) != paddingLength) {
                // Invalid padding, return data as is
                return data;
            }
        }

        // Remove padding
        byte[] result = new byte[data.length - paddingLength];
        System.arraycopy(data, 0, result, 0, result.length);
        return result;
    }

    /**
     * Converts a byte array to a hex string
     * @param bytes The byte array
     * @return Hex string representation
     */
    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b & 0xff));
        }
        return result.toString();
    }
}