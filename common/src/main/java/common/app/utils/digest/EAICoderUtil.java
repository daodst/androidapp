

package common.app.utils.digest;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import common.app.utils.Base64Utils;
import common.app.utils.RSAUtils;



public class EAICoderUtil {

    public static final String RSA_PUBLICK_KEY = "publickey";
    public static final String RSA_PRIVATE_KEY = "privatekey";


    private static final String RSA_PUBLICE = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5dbx/ZY0RbrDgMJFdwNOQ5iLa" + "\r" +
                                              "S7m7JjGCWYwB5RHPSp9emKNA4GvCXhNrGoKxA7FsruQPhYqvSCJ2hU9DyXclXJ0d" + "\r" +
                                              "Eyj1o9ZBaKf6hF+OL8I
                                              "j9OwTv+rDBIRaCdh/wIDAQAB" + "\r";




    private static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALl1vH9ljRFusOAw" + "\r" +
            "kV3A05DmItpLubsmMYJZjAHlEc9Kn16Yo0Dga8JeE2sagrEDsWyu5A+Fiq9IInaF" + "\r" +
            "T0PJdyVcnR0TKPWj1kFop/qEX44vwj/+V2NOxYaJ4Cb1o5x
            "UxV1oc7ZYOeP07BO/6sMEhFoJ2H/AgMBAAECgYB7BmD+WY0UrUrjzRQBDzLJAgDI" + "\r" +
            "skcIoLNi9qfrcds4mRXTGInjNXwGOYXEHJfpeLuvjux2Z22yDLXfzVrharl/jDNf" + "\r" +
            "K/bJGVhfiwV5x+yS+u1TXF86aSB/thcqleFXvRGIV3WuX7um+q7J8oA/OUPGxOyB" + "\r" +
            "FjC8HltCWFRPm1az8QJBANsei0poDiq0vY0b3WrLetxO9Gp00D/fzBgIfbsw874s" + "\r" +
            "UUWc3LHF/kLGSOvkvue93cnggCMeuG+i8Y2QalVz7/sCQQDYrN6BJhBNxUJnXBTr" + "\r" +
            "F4bZ3Hms3GYLN7E+RuFpNB/wgzVN0LOkdKyogVLCt1inSa8szrLOXTcA3zMVfFdt" + "\r" +
            "HMLNAkEAj6JmDFBJeRUha+5oJilcUC4xaddI65X4Y4itYpekL3U9kTRSNvZixcLU" + "\r" +
            "6kz4F1EOodbYKC1rGULmtLWF/p4RIQJBAJ/KJLEjrAReg9ELxFV3XTiPcp/7Tbna" + "\r" +
            "EXk29ocKLL/HU2kWj1Spwqbl8G2uns+H9IrbyFuNvMGE2PxwXV0XR8UCQAkKuKJL" + "\r" +
            "TR0SqEpT1VKqpIauusMP5Jqfysvf/iR+nyS3zSId07+JZE9+uLgJOnoHVHRZNLB1" + "\r" +
            "NWlpbP7I2GirvPo=" + "\r";

    
    private static final String ALGORITHM = "RSA";

    
    public static byte[] getSecureRandom(){
        SecureRandom sr = new SecureRandom();
        byte[] output = new byte[16];
        sr.nextBytes(output);
        return output;
    }

    
    public static String getHashCode(String message){
        String result = null;
        try {
            byte[] input = message.getBytes();
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(input);
            byte[] output = sha.digest();
            result = Base64.encodeToString(output, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getSha256Code(String message){
        String result = null;
        try {
            byte[] input = message.getBytes();
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(input);
            result = byte2Hex(sha.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }


    
    public static String getMD5Code(String message){
        String result = null;
        try {
            byte[] input = message.getBytes("utf-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input);
            byte[] output = md.digest();
            StringBuffer strBuffer = new StringBuffer();
            for (int i = 0; i < output.length; i++) {
                if (Integer.toHexString(0xff & output[i]).length() == 1) {
                    strBuffer.append("0").append(Integer.toHexString(0xff & output[i]));
                } else {
                    strBuffer.append(Integer.toHexString(0xff & output[i]));
                }
            }
            result = strBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    
    public static String getMd5Code16(String message) {
        String md5Str = getMD5Code(message);
        if (!TextUtils.isEmpty(md5Str) && md5Str.length() > 24) {
            return md5Str.substring(8, 24);
        }
        return md5Str;
    }

    
    public static String generateAESKey(){
        
        String key = null;
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] keyBytes = secretKey.getEncoded();
            key = Base64.encodeToString(keyBytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }


    
    public static String getAESCode(String text, String key){
        String result = null;
        try {
            
            SecretKey secretKey = new SecretKeySpec(key.getBytes(),"AES");
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encodeResult = cipher.doFinal(text.getBytes());
            result = Base64.encodeToString(encodeResult, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e2) {
            e2.printStackTrace();
        } catch (IllegalBlockSizeException e3) {
            e3.printStackTrace();
        } catch (BadPaddingException e4) {
            e4.printStackTrace();
        }
        return result;
    }

    public static String testAesDecry(byte[] textBytes, String key) {
        String result = null;
        try {
            
            SecretKey secretKey = new SecretKeySpec(key.getBytes(),"AES");

            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encodeResult = cipher.doFinal(textBytes);
            result = new String(encodeResult, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e2) {
            e2.printStackTrace();
        } catch (IllegalBlockSizeException e3) {
            e3.printStackTrace();
        } catch (BadPaddingException e4) {
            e4.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String getAESToHexCode(String text, String key){
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(key)) {
            return "";
        }
        return AESCrypt.encryptToHex(key, text);
    }


    
    public static Map<String,String> getRSAKey(){
        Map<String,String> keyMap = null;
        try {
            keyMap = new HashMap<String,String>();

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            RSAPublicKey rsaPublicKey = (RSAPublicKey)keyPair.getPublic();
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)keyPair.getPrivate();


            
            String publicKey = Base64.encodeToString(rsaPublicKey.getEncoded(), Base64.DEFAULT);
            String privateKey = Base64.encodeToString(rsaPrivateKey.getEncoded(), Base64.DEFAULT);
            keyMap.put(RSA_PUBLICK_KEY,publicKey);
            keyMap.put(RSA_PRIVATE_KEY,privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyMap;
    }

    
    private static PublicKey getPublicKeyFromX509(String algorithm,
                                                  String bysKey) throws NoSuchAlgorithmException, Exception {
        byte[] decodedKey = Base64.decode(bysKey,Base64.DEFAULT);

        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(x509);
    }

    
    public static String getRSAPublicCode(String text){
        return getRSAPublicCode(RSA_PUBLICE, text);
    }

    public static String getRSAPublicCode(String rsa_public, String text){
        String signStr = null;
        try
        {
            
            PublicKey publicKey = RSAUtils.loadPublicKey(rsa_public);
            
            
            byte[] encryptByte = RSAUtils.encryptData(text.getBytes(), publicKey);
            
            String afterencrypt = Base64Utils.encode(encryptByte);
            signStr = afterencrypt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signStr;
    }

    
    public static String getRSAPrivateCode(String signStr){
        String str = null;
        try
        {
            
            PrivateKey privateKey = RSAUtils.loadPrivateKey(RSA_PRIVATE);
            
            
            byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(signStr), privateKey);
            str = new String(decryptByte);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return str;
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

}
