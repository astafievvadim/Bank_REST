package com.example.bankcards.util;

import com.example.bankcards.exception.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
@Component
public class EncryptionUtil {

    private final String SECRET;
    private final String type = "AES"; //@TODO Read about alternatives to symmetric encryption

    @Autowired
    public EncryptionUtil(@Value("${key.card_secret_value}") String secret) {
        this.SECRET = secret;
    }

    public String decrypt(String encryptedNumber) {
        try{

            SecretKeySpec k = new SecretKeySpec(SECRET.getBytes(),type);

            Cipher cipher = Cipher.getInstance(type);
            cipher.init(Cipher.DECRYPT_MODE, k);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedNumber);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            return new String(decryptedBytes);

        } catch (Exception e){

            throw new EncryptionException("Decryption error", e);
        }
    }

    public String encrypt(String plainUuid){
        try{

            SecretKeySpec k = new SecretKeySpec(SECRET.getBytes(),type);

            Cipher cipher = Cipher.getInstance(type);
            cipher.init(Cipher.ENCRYPT_MODE, k);

            byte[] encryptedBytes = cipher.doFinal(plainUuid.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e){

            throw new EncryptionException("Encryption error", e);
        }
    }
}
