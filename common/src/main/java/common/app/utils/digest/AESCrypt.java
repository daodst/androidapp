

package common.app.utils.digest;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public final class AESCrypt {

    private static final String TAG = "AESCrypt";

    
    private static final String AES_MODE = "AES/CBC/PKCS7Padding";
    private static final String CHARSET = "UTF-8";

    
    private static final String HASH_ALGORITHM = "SHA-256";

    
    private static final byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private static final String iv = "";

    
    public static boolean DEBUG_LOG_ENABLED = false;


    
    private static SecretKeySpec generateKey(final String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] key = password.getBytes("UTF-8");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }


    
    public static String encrypt(final String password, String message)
            throws GeneralSecurityException {
        try {
            final SecretKeySpec key = generateKey(password);
            byte[] cipherText = encrypt(key, ivBytes, message.getBytes(CHARSET));
            
            String encoded = Base64.encodeToString(cipherText, Base64.NO_WRAP);
            return encoded;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralSecurityException(e);
        }
    }

    public static String encryptToHex(final String password, String message) {
        try {
            final SecretKeySpec key = generateKey(password);

            AlgorithmParameterSpec paramSpec = new IvParameterSpec(message.substring(0,16).getBytes());
            final Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            byte[] bt3 = new byte[ivBytes.length + message.getBytes(CHARSET).length];
            System.arraycopy(ivBytes, 0, bt3, 0, ivBytes.length);
            System.arraycopy(message.getBytes(CHARSET), 0, bt3, ivBytes.length, message.getBytes(CHARSET).length);
            byte[] cipherText = cipher.doFinal(bt3);
            
            String encoded = bytesToHex(cipherText);
            return encoded;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    
    public static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }


    
    public static byte[] encrypt(final SecretKeySpec key, final byte[] iv, final byte[] message)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherText = cipher.doFinal(message);
        return cipherText;
    }

    
    public static Cipher getEncryptCipher(String password) throws GeneralSecurityException {
        try {
            final SecretKeySpec key = generateKey(password);
            final Cipher cipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            return cipher;
        } catch (UnsupportedEncodingException e) {
            if (DEBUG_LOG_ENABLED) {
                Log.e(TAG, "UnsupportedEncodingException ", e);
            }
            throw new GeneralSecurityException(e);
        }
    }

    
    public static Cipher getDecryptCipher(String password) throws GeneralSecurityException {
        try {
            final SecretKeySpec key = generateKey(password);

            final Cipher cipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            return cipher;
        } catch (UnsupportedEncodingException e) {
            if (DEBUG_LOG_ENABLED) {
                Log.e(TAG, "UnsupportedEncodingException ", e);
            }
            throw new GeneralSecurityException(e);
        }
    }


    
    public static String decrypt(final String password, String base64EncodedCipherText)
            throws GeneralSecurityException {

        try {
            final SecretKeySpec key = generateKey(password);

            byte[] decodedCipherText = Base64.decode(base64EncodedCipherText, Base64.NO_WRAP);

            byte[] decryptedBytes = decrypt(key, ivBytes, decodedCipherText);

            String message = new String(decryptedBytes, CHARSET);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralSecurityException(e);
        }
    }


    
    public static byte[] decrypt(final SecretKeySpec key, final byte[] iv, final byte[] decodedCipherText)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(decodedCipherText);

        return decryptedBytes;
    }




    private static void log(String what, byte[] bytes) {
        if (DEBUG_LOG_ENABLED) {
            Log.d(TAG, what + "[" + bytes.length + "] [" + bytesToHex(bytes) + "]");
        }
    }

    private static void log(String what, String value) {
        if (DEBUG_LOG_ENABLED) {
            Log.d(TAG, what + "[" + value.length() + "] [" + value + "]");
        }
    }


    
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private AESCrypt() {
    }
}
