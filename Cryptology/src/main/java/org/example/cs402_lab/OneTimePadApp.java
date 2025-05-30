package org.example.cs402_lab;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.security.SecureRandom;

public class OneTimePadApp extends Application {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void start(Stage primaryStage) {
        Label lblPlainText = new Label("Enter Plaintext:");
        TextField txtPlainText = new TextField();

        Label lblKey = new Label("Generated Key:");
        TextField txtKey = new TextField();
        txtKey.setEditable(false);

        Label lblCipherText = new Label("Ciphertext:");
        TextField txtCipherText = new TextField();
        txtCipherText.setEditable(false);

        Label lblDecryptedText = new Label("Decrypted Text:");
        TextField txtDecryptedText = new TextField();
        txtDecryptedText.setEditable(false);

        Button btnEncrypt = new Button("Encrypt");
        Button btnDecrypt = new Button("Decrypt");

        btnEncrypt.setOnAction(e -> {
            String plaintext = txtPlainText.getText().toUpperCase().replaceAll("[^A-Z]", "");
            if (plaintext.isEmpty()) {
                txtCipherText.setText("Enter plaintext!");
                return;
            }
            int seed = (int) (Math.random() * 26);
            int a = (int) (Math.random() * 26);
            int b = (int) (Math.random() * 26);
            String key = generateRandomKey(plaintext.length() ,  seed,a, b);
            String ciphertext = encrypt(plaintext, key);

            txtKey.setText(key);
            txtCipherText.setText(ciphertext);
        });

        btnDecrypt.setOnAction(e -> {
            String ciphertext = txtCipherText.getText().toUpperCase().replaceAll("[^A-Z]", "");
            String key = txtKey.getText().toUpperCase().replaceAll("[^A-Z]", "");
            if (ciphertext.isEmpty() || key.isEmpty()) {
                txtDecryptedText.setText("Enter valid ciphertext and key!");
                return;
            }
            String decryptedText = decrypt(ciphertext, key);
            txtDecryptedText.setText(decryptedText);
        });

        VBox root = new VBox(10, lblPlainText, txtPlainText, lblKey, txtKey,
                lblCipherText, txtCipherText, lblDecryptedText, txtDecryptedText,
                btnEncrypt, btnDecrypt);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);

        Scene scene = new Scene(root, 400, 350);
        primaryStage.setTitle("One-Time Pad Encryption");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static String generateRandomKey(int length, int seed, int a, int b) {
        StringBuilder key = new StringBuilder();
        int x=seed;
        for (int i = 0; i < length; i++) {
            x = (a * x + b) % 26;
            char ch = (char) (x + 'A');
            key.append(ch);
        }
        return key.toString();
    }


    private String encrypt(String plaintext, String key) {
        StringBuilder ciphertext = new StringBuilder();
        int keyIndex = 0;

        for (int i = 0; i < plaintext.length(); i++) {
            char ch = plaintext.charAt(i);

            if (Character.isLetter(ch)) {
                int charPos = ALPHABET.indexOf(Character.toUpperCase(ch));
                int keyPos = ALPHABET.indexOf(key.charAt(keyIndex));
                int encryptedPos = (charPos + keyPos) % ALPHABET.length();

                char encryptedChar = Character.isLowerCase(ch) ?
                        Character.toLowerCase(ALPHABET.charAt(encryptedPos)) :
                        ALPHABET.charAt(encryptedPos);

                ciphertext.append(encryptedChar);
                keyIndex++;
            } else {
                ciphertext.append(ch);
            }
        }
        return ciphertext.toString();
    }


    private String decrypt(String ciphertext, String key) {
        StringBuilder plaintext = new StringBuilder();
        int keyIndex = 0;

        for (int i = 0; i < ciphertext.length(); i++) {
            char ch = ciphertext.charAt(i);

            if (Character.isLetter(ch)) {
                int charPos = ALPHABET.indexOf(Character.toUpperCase(ch));
                int keyPos = ALPHABET.indexOf(key.charAt(keyIndex));
                int decryptedPos = (charPos - keyPos + ALPHABET.length()) % ALPHABET.length();

                char decryptedChar = Character.isLowerCase(ch) ?
                        Character.toLowerCase(ALPHABET.charAt(decryptedPos)) :
                        ALPHABET.charAt(decryptedPos);

                plaintext.append(decryptedChar);
                keyIndex++;
            } else {
                plaintext.append(ch);
            }
        }
        return plaintext.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
