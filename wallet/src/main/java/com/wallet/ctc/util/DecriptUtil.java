

package com.wallet.ctc.util;

import android.text.TextUtils;

import com.wallet.ctc.Constants;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class DecriptUtil {
    public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            
            StringBuffer hexString = new StringBuffer();
            
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String SHA(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            
            StringBuffer hexString = new StringBuffer();
            
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String MD5(String input) {
        try {
            input=getSHA256StrJava(input);
            for(int i=0;i<100;i++){
                input=MD52(input);
            }
            return input;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String MD52(String input) {
        try {
            
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            
            mdInst.update(input.getBytes());
            
            byte[] md = mdInst.digest();
            
            StringBuffer hexString = new StringBuffer();
            
            for (int i = 0; i < md.length; i++) {
                String shaHex = Integer.toHexString(md[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String MD5100(String input) {
        try {
            for(int i=0;i<100;i++){
                input=MD52(input);
            }
            return input;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    
    public static byte[] Encrypt(String sSrc, String sKey) {
        byte[] encrypted=null;
        if(TextUtils.isEmpty(sSrc)){
            return null;
        }
        
        if (sKey == null) {
            sKey="0000000000000000";
        }
        sKey=MD5100(sKey);
        
        if (sKey.length() != 16) {
            System.out.print("Key16");
            int keylen=sKey.length();
            if(keylen>16){
                sKey=sKey.substring(0,16);
            }else {
                for(int i=keylen;i<16;i++){
                    sKey+="0";
                }
            }
        }
        try {
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, Constants.AES);
            Cipher cipher = Cipher.getInstance(Constants.CBC);
            IvParameterSpec iv = new IvParameterSpec(Constants.IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            encrypted = cipher.doFinal(sSrc.getBytes());
        }catch (Exception e){
            LogUtil.d(e.toString());
        }
        return encrypted;
    }

    
	public static String Decrypt(byte[] sSrc, String sKey){
		try {
			
			if (sKey == null) {
                sKey="0000000000000000";
			}
            sKey=MD5100(sKey);
			
			if (sKey.length() != 16) {
				System.out.print("Key16");
                int keylen=sKey.length();
                if(keylen>16){
                    sKey=sKey.substring(0,16);
                }else {
                    for(int i=keylen;i<16;i++){
                        sKey+="0";
                    }
                }
			}
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw,Constants.AES);
			Cipher cipher = Cipher.getInstance(Constants.CBC);
			IvParameterSpec iv = new IvParameterSpec(
                    Constants.IV.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			try {
				byte[] original = cipher.doFinal(sSrc);
				String originalString = new String(original);
				return originalString;
			} catch (Exception e) {
				System.out.println(e.toString());
                LogUtil.d(""+e.toString());
				return null;
			}
		} catch (Exception ex) {
			LogUtil.d(ex.toString());
			return null;
		}
	}
    
    public static String byte2hex(byte[] b) { 
        StringBuffer sb = new StringBuffer(b.length * 2);
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); 
    }


    
    public static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }

    
    private static String toBase64(String content){
        byte[] encrypted=content.getBytes();
        return Base64.encode(encrypted);
    }
    
    private static String base64ToString(String content){
        try {
            byte[] encrypted1 = Base64.decode(content);
            String originalString = new String(encrypted1);
            return originalString;
        } catch (Exception e) {
            return null;
        }
    }

    
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
