package org.example.cs402_lab;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA extends Application {

    private TextArea inputArea, outputArea;
    private TextField publicKeyField, privateKeyField;
    private BigInteger p, q, n, phi, e, d;
    private final SecureRandom random = new SecureRandom();
    private final int BIT_LENGTH = 512;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        inputArea = new TextArea();
        inputArea.setPromptText("Enter Plaintext or Ciphertext Here");

        outputArea = new TextArea();
        outputArea.setPromptText("Output Will Appear Here");

        publicKeyField = new TextField();
        publicKeyField.setEditable(false);
        privateKeyField = new TextField();
        privateKeyField.setEditable(false);

        Button generateBtn = new Button("Generate Keys");
        Button encryptBtn = new Button("Encrypt");
        Button decryptBtn = new Button("Decrypt");
        Button saveKeysBtn = new Button("Save Keys to File");

        generateBtn.setOnAction(e -> generateKeys());
        encryptBtn.setOnAction(e -> encryptText());
        decryptBtn.setOnAction(e -> decryptText());
        saveKeysBtn.setOnAction(e -> saveKeysToFile());

        VBox keyBox = new VBox(10,
                new Label("Public Key (e, n):"), publicKeyField,
                new Label("Private Key (d, n):"), privateKeyField);

        HBox buttonBox = new HBox(10, generateBtn, encryptBtn, decryptBtn, saveKeysBtn);
        VBox layout = new VBox(15,
                new Label("Input / Plaintext or Ciphertext:"), inputArea,
                buttonBox,
                new Label("Output:"), outputArea,
                keyBox);

        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14;");

        Scene scene = new Scene(layout, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("RSA Cipher with JavaFX");
        primaryStage.show();
    }

    private void generateKeys() {
        p = BigInteger.probablePrime(BIT_LENGTH, random);
        q = BigInteger.probablePrime(BIT_LENGTH, random);
        n = p.multiply(q); //n=p*q
        phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE)); // phi=(p-1)(p-1)

        do {
            e = new BigInteger(BIT_LENGTH / 2, random);
        } while (!e.gcd(phi).equals(BigInteger.ONE));       //gcd(e,phi)=1

        d = e.modInverse(phi);     // d=e^-1 mod phi

        publicKeyField.setText("(" + e + ", " + n + ")");
        privateKeyField.setText("(" + d + ", " + n + ")");
        showInfo("RSA Keys Generated Successfully!");
    }

    private void encryptText() {
        String plain = inputArea.getText();
        if (plain.isEmpty()) {
            showInfo("Please enter plaintext.");
            return;
        }
        if (e == null || n == null) {
            showInfo("Please generate keys first.");
            return;
        }

        try {
            byte[] bytes = plain.getBytes();
            int blockSize = (n.bitLength() - 1) / 8;
            StringBuilder cipherText = new StringBuilder();

            for (int i = 0; i < bytes.length; i += blockSize) {
                int len = Math.min(blockSize, bytes.length - i);
                byte[] block = new byte[len];
                System.arraycopy(bytes, i, block, 0, len);

                BigInteger m = new BigInteger(1, block);
                BigInteger ciph = m.modPow(e, n);        //  c = m^e mod n
                cipherText.append(ciph.toString()) ;
            }

            outputArea.setText(cipherText.toString());
        } catch (Exception ex) {
            showInfo("Encryption failed: " + ex.getMessage());
        }
    }

    private void decryptText() {
        String cipher = inputArea.getText().trim();
        if (cipher.isEmpty()) {
            showInfo("Please enter ciphertext.");
            return;
        }
        if (d == null || n == null) {
            showInfo("Please generate keys first.");
            return;
        }

        try {
            String[] parts = cipher.split(" ");
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            for (String part : parts) {
                if (part.isEmpty()) continue;

                BigInteger ciph = new BigInteger(part);
                BigInteger m = ciph.modPow(d, n); //  m = c^d mod n
                byte[] block = m.toByteArray();

                if (block.length > 1 && block[0] == 0) {
                    byte[] trimmed = new byte[block.length - 1];
                    System.arraycopy(block, 1, trimmed, 0, trimmed.length);
                    byteStream.write(trimmed);
                } else {
                    byteStream.write(block);
                }
            }

            String result = new String(byteStream.toByteArray());
            outputArea.setText(result);
        } catch (Exception ex) {
            showInfo("Decryption failed: " + ex.getMessage());
        }
    }


    private void saveKeysToFile() {
        if (e == null || d == null || n == null) {
            showInfo("Please generate keys first.");
            return;
        }

        try (FileWriter writer = new FileWriter("D:\\lol")) {
            writer.write("Public Key (e, n):\n");
            writer.write("(" + e.toString() + ",\n" + n.toString() + ")\n\n");

            writer.write("Private Key (d, n):\n");
            writer.write("(" + d.toString() + ",\n" + n.toString() + ")\n");

            showInfo("Keys saved to lol successfully.");
        } catch (IOException ex) {
            showInfo("Error saving keys: " + ex.getMessage());
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("RSA Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


