package de.filipzocktan.util.crypto;

import io.sentry.Sentry;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Crypto {

    private PublicKey pubKey;
    private PrivateKey privKey;
    private PublicKey serverKey;
    private boolean hasServerKey = false;

    public Crypto() {
        KeyPair keyPair_tmp = null;
        try {
            keyPair_tmp = buildKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Sentry.capture(e);
        }
        pubKey = keyPair_tmp.getPublic();
        privKey = keyPair_tmp.getPrivate();
    }

    public static String keyToString(PublicKey key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = factory.getKeySpec(key, X509EncodedKeySpec.class);
        return new String(Base64.getEncoder().encode(spec.getEncoded()));
    }

    public KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public byte[] encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privKey);

        return cipher.doFinal(message.getBytes());
    }

    public byte[] decrypt(byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, serverKey);

        return cipher.doFinal(encrypted);
    }

    public void setServerKey(byte[] key) {
        PublicKey pubKey1;
        X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
        try {
            pubKey1 = KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (InvalidKeySpecException e) {
            pubKey1 = null;
            e.printStackTrace();
            Sentry.capture(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Sentry.capture(e);
            pubKey1 = null;
        }
        this.serverKey = pubKey1;
        this.hasServerKey = true;
    }

    public boolean hasServerKey() {

        return hasServerKey;
    }

}




