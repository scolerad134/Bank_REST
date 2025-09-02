package com.example.bankcards.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${app.encryption.key:defaultEncryptionKeyForDevelopmentOnly}")
    private String encryptionKey;

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    public String encrypt(String plaintext) {
        try {
            SecretKey key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            
            byte[] iv = generateIV();
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes());
            
            byte[] encryptedData = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);
            
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKey key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[decodedData.length - GCM_IV_LENGTH];
            
            System.arraycopy(decodedData, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(decodedData, GCM_IV_LENGTH, cipherText, 0, cipherText.length);
            
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
            
            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    private byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        
        int length = cardNumber.length();
        String lastFour = cardNumber.substring(length - 4);
        String masked = "*".repeat(length - 4);
        
        return masked + lastFour;
    }

    public String formatCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }
        
        return String.format("%s %s %s %s", 
                cardNumber.substring(0, 4),
                cardNumber.substring(4, 8),
                cardNumber.substring(8, 12),
                cardNumber.substring(12, 16));
    }
}



