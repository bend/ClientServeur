package com.ben.crypt.rsa;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

/**
 *
 * @author benoitdaccache
 */
public class RSA {

    protected static final String ALGORITHM = "RSA";

    /**
     * Generate key which contains a pair of private and public key using 1024 bytes
     * @return key pair
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();
        return key;

    }

    /**
     * Encrypt a text using public key.
     * @param text The original unencrypted text
     * @param key The public key
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public static byte[] encrypt(byte[] text, PublicKey key) throws Exception {
        byte[] cipherText = null;
// get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        System.out.println("nProvider is:" + cipher.getProvider().getInfo());
// encrypt the plaintext using the public key
        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipherText = cipher.doFinal(text);
        return cipherText;
    }

    /**
     * Decrypt text using private key
     * @param text The encrypted text
     * @param key The private key
     * @return The unencrypted text
     * @throws java.lang.Exception
     */
    public static byte[] decrypt(byte[] text, PrivateKey key) throws Exception {
        byte[] dectyptedText = null;
// decrypt the text using the private key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        dectyptedText = cipher.doFinal(text);
        return dectyptedText;
    }

}
