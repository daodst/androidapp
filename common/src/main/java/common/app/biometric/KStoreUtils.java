package common.app.biometric;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import common.app.utils.SpUtil;


public class KStoreUtils {
    private static final String TAG = "KStoreUtils";
    private final String KEY_NAME;
    private static final String CHARSET = "UTF-8";

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String SEPARATOR = ",";

    private KStoreUtils(String keyName) {
        this.KEY_NAME = keyName;
    }

    public static KStoreUtils getInstance(String keyName) {
        
        return new KStoreUtils(keyName);
    }

    
    public Cipher startEncryptCipher() throws InvalidKeyException, InvalidAlgorithmParameterException {
        
        generateSecretKey();

        Cipher cipher = getCipher();
        SecretKey secretKey = getSecretKey();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    
    public Cipher startDecryptCipher() throws InvalidKeyException, InvalidAlgorithmParameterException, AssertionError {
        
        String vkString = SpUtil.getAppWalletPassword(KEY_NAME);
        if (vkString == null || !vkString.contains(SEPARATOR)) {
            throw new AssertionError("IV");
        }
        String[] parts = vkString.split(SEPARATOR);
        if (parts.length != 2) {
            throw new AssertionError("String to decrypt must be of the form: 'BASE64_DATA" + SEPARATOR + "BASE64_IV'");
        }

        byte[] ivBytes = Base64.decode(parts[1], Base64.NO_WRAP);

        Cipher cipher = getCipher();
        SecretKey secretKey = getSecretKey();
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher;
    }

    
    public String encrypt(Cipher cipher, String plaintext) {
        try {
            String iv = Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP);
            byte[] data = cipher.doFinal(plaintext.getBytes(Charset.defaultCharset()));
            String message = Base64.encodeToString(data, Base64.NO_WRAP) + SEPARATOR + iv;
            
            SpUtil.setAppWalletPassword(KEY_NAME, message);
            return message;
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    
    public String decrypt(Cipher cipher) {
        
        String decodedCipherText = SpUtil.getAppWalletPassword(KEY_NAME);
        if (decodedCipherText == null || !decodedCipherText.contains(SEPARATOR)) {

            return "";
        }
        String[] parts = decodedCipherText.split(SEPARATOR);
        if (parts.length != 2) {
            throw new AssertionError("String to decrypt must be of the form: 'BASE64_DATA" + SEPARATOR + "BASE64_IV'");
        }

        try {
            byte[] decryptBytes = Base64.decode(parts[0], Base64.NO_WRAP);

            byte[] data = cipher.doFinal(decryptBytes);
            
            return new String(data, CHARSET);
        } catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    
    private void generateSecretKey() {



        
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

            
            KeyGenParameterSpec parameterSpec = new KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    
                    
                    
                    
                    .setInvalidatedByBiometricEnrollment(true)
                    .build();

            keyGenerator.init(parameterSpec);
            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException | NoSuchProviderException |
                 InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    
    public SecretKey getSecretKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);

            
            Log.i(TAG, "KEY_NAME="+KEY_NAME);
            keyStore.load(null);
            return ((SecretKey) keyStore.getKey(KEY_NAME, null));
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException |
                 UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        }
    }

    private Cipher getCipher() {
        try {
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
