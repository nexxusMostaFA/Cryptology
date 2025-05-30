package org.example.cs402_lab.Task5;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AES {
     private int[][] state = new int[4][4];

     private int[][] roundKeys = new int[11][16];

     public String encrypt(String plaintext, String key) throws Exception {
        validateKey(key);
        logToFile("=== Starting AES Encryption ===");
        logToFile("Original Plaintext: " + plaintext);
        logToFile("Key: " + key);

         byte[] paddedPlaintext = padPlaintext(plaintext.getBytes(StandardCharsets.UTF_8));
        logToFile("Padded Plaintext: " + bytesToHex(paddedPlaintext));

         keyExpansion(key.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        int blocks = paddedPlaintext.length / 16;

        for (int blockNum = 0; blockNum < blocks; blockNum++) {
            int start = blockNum * 16;
            byte[] block = Arrays.copyOfRange(paddedPlaintext, start, start + 16);

            logToFile("\nProcessing Block " + (blockNum + 1) + " of " + blocks);
            logToFile("Block Content: " + bytesToHex(block));

             byte[] encryptedBlock = encryptBlock(block);
            result.append(bytesToHex(encryptedBlock));

            logToFile("Encrypted Block: " + bytesToHex(encryptedBlock));
        }

        logToFile("\nFinal Encrypted Result: " + result.toString());
        return result.toString();
    }

     public String decrypt(String ciphertext, String key) throws Exception {
        validateKey(key);
        logToFile("=== Starting AES Decryption ===");
        logToFile("Encrypted Input: " + ciphertext);
        logToFile("Key: " + key);

         keyExpansion(key.getBytes(StandardCharsets.UTF_8));

         byte[] cipherBytes = hexStringToByteArray(ciphertext);
        int blocks = cipherBytes.length / 16;

        byte[] allDecryptedBytes = new byte[blocks * 16];

        for (int blockNum = 0; blockNum < blocks; blockNum++) {
            int start = blockNum * 16;
            byte[] block = Arrays.copyOfRange(cipherBytes, start, start + 16);

            logToFile("\nProcessing Block " + (blockNum + 1) + " of " + blocks);
            logToFile("Block Content: " + bytesToHex(block));

             byte[] decryptedBlock = decryptBlock(block);
            System.arraycopy(decryptedBlock, 0, allDecryptedBytes, blockNum*16, 16);

            logToFile("Decrypted Block: " + bytesToHex(decryptedBlock));
        }

         int paddingLength = allDecryptedBytes[allDecryptedBytes.length - 1];
        if (paddingLength > 0 && paddingLength <= 16) {
            allDecryptedBytes = Arrays.copyOf(allDecryptedBytes, allDecryptedBytes.length - paddingLength);
        }

        String decryptedText = new String(allDecryptedBytes, StandardCharsets.UTF_8);
        logToFile("\nFinal Decrypted Result: " + decryptedText);
        return decryptedText;
    }

     private byte[] encryptBlock(byte[] block) {
         initializeState(block);
        logState("Initial State:");

         addRoundKey(0);
        logState("After Initial AddRoundKey:");

         for (int round = 1; round <= 9; round++) {
            subBytes();
            logState("After SubBytes (Round " + round + "):");

            shiftRows();
            logState("After ShiftRows (Round " + round + "):");

            mixColumns();
            logState("After MixColumns (Round " + round + "):");

            addRoundKey(round);
            logState("After AddRoundKey (Round " + round + "):");
        }

         subBytes();
        logState("After SubBytes (Final Round):");

        shiftRows();
        logState("After ShiftRows (Final Round):");

        addRoundKey(10);
        logState("After Final AddRoundKey:");

         return stateToByteArray();
    }

     private byte[] decryptBlock(byte[] block) {
         initializeState(block);
        logState("Initial State:");

         addRoundKey(10);
        logState("After Initial AddRoundKey:");

         for (int round = 9; round >= 1; round--) {
            inverseShiftRows();
            logState("After InverseShiftRows (Round " + round + "):");

            inverseSubBytes();
            logState("After InverseSubBytes (Round " + round + "):");

            addRoundKey(round);
            logState("After AddRoundKey (Round " + round + "):");

            inverseMixColumns();
            logState("After InverseMixColumns (Round " + round + "):");
        }

         inverseShiftRows();
        logState("After InverseShiftRows (Final Round):");

        inverseSubBytes();
        logState("After InverseSubBytes (Final Round):");

        addRoundKey(0);
        logState("After Final AddRoundKey:");

         return stateToByteArray();
    }


    private void subBytes() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int val = state[i][j];
                 int row = (val >>> 4) & 0x0F;
                int col = val & 0x0F;
                state[i][j] = Constants.sbox[row][col];
            }
        }
    }


    private void inverseSubBytes() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int val = state[i][j];
                 int row = (val >>> 4) & 0x0F;
                int col = val & 0x0F;
                state[i][j] = Constants.inverseSbox[row][col];
            }
        }
    }


    private void shiftRows() {

         int temp = state[1][0];
        state[1][0] = state[1][1];
        state[1][1] = state[1][2];
        state[1][2] = state[1][3];
        state[1][3] = temp;

        int temp2 = state[2][0];
        state[2][0] = state[2][2];
        state[2][2] = temp2;

         temp2 = state[2][1];
        state[2][1] = state[2][3];
        state[2][3] = temp2;

         temp = state[3][3];
        state[3][3] = state[3][2];
        state[3][2] = state[3][1];
        state[3][1] = state[3][0];
        state[3][0] = temp;
    }

    private void inverseShiftRows() {

        int temp = state[1][3];
        state[1][3] = state[1][2];
        state[1][2] = state[1][1];
        state[1][1] = state[1][0];
        state[1][0] = temp;


        int temp2 = state[2][0];
        state[2][0] = state[2][2];
        state[2][2] = temp2;

         temp2 = state[2][1];
        state[2][1] = state[2][3];
        state[2][3] = temp2;


        temp = state[3][0];
        state[3][0] = state[3][1];
        state[3][1] = state[3][2];
        state[3][2] = state[3][3];
        state[3][3] = temp;
    }


    private void mixColumns() {
        int[][] temp = new int[4][4];

        for (int c = 0; c < 4; c++) {
            temp[0][c] = multiply(Constants.mixColumn[0][0], state[0][c]) ^
                    multiply(Constants.mixColumn[0][1], state[1][c]) ^
                    multiply(Constants.mixColumn[0][2], state[2][c]) ^
                    multiply(Constants.mixColumn[0][3], state[3][c]);

            temp[1][c] = multiply(Constants.mixColumn[1][0], state[0][c]) ^
                    multiply(Constants.mixColumn[1][1], state[1][c]) ^
                    multiply(Constants.mixColumn[1][2], state[2][c]) ^
                    multiply(Constants.mixColumn[1][3], state[3][c]);

            temp[2][c] = multiply(Constants.mixColumn[2][0], state[0][c]) ^
                    multiply(Constants.mixColumn[2][1], state[1][c]) ^
                    multiply(Constants.mixColumn[2][2], state[2][c]) ^
                    multiply(Constants.mixColumn[2][3], state[3][c]);

            temp[3][c] = multiply(Constants.mixColumn[3][0], state[0][c]) ^
                    multiply(Constants.mixColumn[3][1], state[1][c]) ^
                    multiply(Constants.mixColumn[3][2], state[2][c]) ^
                    multiply(Constants.mixColumn[3][3], state[3][c]);
        }

         for (int i = 0; i < 4; i++) {
            System.arraycopy(temp[i], 0, state[i], 0, 4);
        }
    }

    private void inverseMixColumns() {
        int[][] temp = new int[4][4];

        for (int c = 0; c < 4; c++) {
            temp[0][c] = multiply(Constants.inverseMixColumn[0][0], state[0][c]) ^
                    multiply(Constants.inverseMixColumn[0][1], state[1][c]) ^
                    multiply(Constants.inverseMixColumn[0][2], state[2][c]) ^
                    multiply(Constants.inverseMixColumn[0][3], state[3][c]);

            temp[1][c] = multiply(Constants.inverseMixColumn[1][0], state[0][c]) ^
                    multiply(Constants.inverseMixColumn[1][1], state[1][c]) ^
                    multiply(Constants.inverseMixColumn[1][2], state[2][c]) ^
                    multiply(Constants.inverseMixColumn[1][3], state[3][c]);

            temp[2][c] = multiply(Constants.inverseMixColumn[2][0], state[0][c]) ^
                    multiply(Constants.inverseMixColumn[2][1], state[1][c]) ^
                    multiply(Constants.inverseMixColumn[2][2], state[2][c]) ^
                    multiply(Constants.inverseMixColumn[2][3], state[3][c]);

            temp[3][c] = multiply(Constants.inverseMixColumn[3][0], state[0][c]) ^
                    multiply(Constants.inverseMixColumn[3][1], state[1][c]) ^
                    multiply(Constants.inverseMixColumn[3][2], state[2][c]) ^
                    multiply(Constants.inverseMixColumn[3][3], state[3][c]);
        }

         for (int i = 0; i < 4; i++) {
            System.arraycopy(temp[i], 0, state[i], 0, 4);
        }
    }


    private void addRoundKey(int round) {
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                state[r][c] ^= roundKeys[round][c * 4 + r];
            }
        }
    }



    private int multiply(int a, int b) {
         int product = 0;

        for (int bitPosition = 0; bitPosition < 8; bitPosition++) {
             if ((b & 0x01) == 1) {
                product ^= a;
            }

             boolean overflow = (a & 0x80) != 0;

             a <<= 1;


            if (overflow) {
                a ^= 0x1B;
            }

             b >>= 1;
        }

         return product & 0xFF;
    }


    private void keyExpansion(byte[] key) {
         for (int i = 0; i < 16; i++) {
            roundKeys[0][i] = key[i] & 0xFF;
        }

         for (int round = 1; round <= 10; round++) {
             int[] temp = Arrays.copyOfRange(roundKeys[round-1], 12, 16);

             int t = temp[0];
            temp[0] = temp[1];
            temp[1] = temp[2];
            temp[2] = temp[3];
            temp[3] = t;

             for (int i = 0; i < 4; i++) {
                int val = temp[i];
                int row = (val >>> 4) & 0x0F;
                int col = val & 0x0F;
                temp[i] = Constants.sbox[row][col];
            }

             temp[0] ^= Constants.RCon[round-1];

             for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (i == 0) {
                        roundKeys[round][j] = roundKeys[round-1][j] ^ temp[j];
                    } else {
                        roundKeys[round][i*4 + j] = roundKeys[round-1][i*4 + j] ^ roundKeys[round][(i-1)*4 + j];
                    }
                }
            }

            logRoundKey(round);
        }
    }


    private void initializeState(byte[] block) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = block[i * 4 + j] & 0xFF;
            }
        }
    }


    private byte[] stateToByteArray() {
        byte[] out = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out[i * 4 + j] = (byte) state[j][i];
            }
        }
        return out;
    }


    private byte[] padPlaintext(byte[] plaintext) {
        int paddingLength = 16 - (plaintext.length % 16);
        byte[] padded = new byte[plaintext.length + paddingLength];
        System.arraycopy(plaintext, 0, padded, 0, plaintext.length);

         for (int i = plaintext.length; i < padded.length; i++) {
            padded[i] = (byte) paddingLength;
        }

        return padded;
    }

    private void logState(String message) {
        logToFile(message);
        for (int i = 0; i < 4; i++) {
            logToFile(String.format("[%02X, %02X, %02X, %02X]",
                    state[i][0], state[i][1], state[i][2], state[i][3]));
        }
    }

    private void logRoundKey(int round) {
        logToFile("\nRound Key " + round + ":");
        for (int i = 0; i < 4; i++) {
            logToFile(String.format("[%02X, %02X, %02X, %02X]",
                    roundKeys[round][i*4], roundKeys[round][i*4+1],
                    roundKeys[round][i*4+2], roundKeys[round][i*4+3]));
        }
    }

    private void logToFile(String message) {
        try (FileWriter writer = new FileWriter("C:\\Users\\mostafa\\Desktop\\CS402\\CS402\\src\\main\\java\\org\\example\\cs402_lab\\AESfile.txt", true)) {
            writer.write(message + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private void validateKey(String key) {
        if (key.length() != 16) {
            throw new IllegalArgumentException("Key must be exactly 16 characters (128 bits)");
        }
    }
}