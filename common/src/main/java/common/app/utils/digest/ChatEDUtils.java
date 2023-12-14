

package common.app.utils.digest;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;



public final class ChatEDUtils {

    private static final String DEFAULT_PWD = "";
    private static final boolean isSign = true; 

    
    private static String getSignPwd(String pwd,String id){
        return pwd + id;
    }

    
    public static String encryptTxt(String id, String text) {
        if (!isSign || TextUtils.isEmpty(text) || TextUtils.isEmpty(id)) {
            return text;
        }
        try {
            String sign = AESCrypt.encrypt(getSignPwd(DEFAULT_PWD,id), text);
            return sign;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return text;
        }
    }

    
    public static String decryptTxt(String id, String signedTtext) {
        if (!isSign || TextUtils.isEmpty(signedTtext) || TextUtils.isEmpty(id)) {
            return signedTtext;
        }
        try {
            String sign = AESCrypt.decrypt(getSignPwd(DEFAULT_PWD,id), signedTtext);
            return sign;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return signedTtext;
        }
    }

    
    public static String encryptTxt(String pwd, String id, String text) {
        if (!isSign || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(id) || TextUtils.isEmpty(text)) {
            return text;
        }
        try {
            String sign = AESCrypt.encrypt(getSignPwd(pwd,id), text);
            return sign;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return text;
        }
    }

    
    public static String decryptTxt(String pwd, String id, String signedTtext) {
        if (!isSign || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(id) || TextUtils.isEmpty(signedTtext)) {
            return signedTtext;
        }
        try {
            String sign = AESCrypt.decrypt(getSignPwd(pwd,id), signedTtext);
            return sign;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return signedTtext;
        }
    }


    

    private static String BASE_PATH = Environment.getExternalStorageDirectory() + "/XsyChatFile";
    static {
        File baseFile = new File(BASE_PATH);
        if(!baseFile.exists()) { 
            baseFile.mkdirs();
        }
    }
    
    private static String getSignFilePath(File srcFile) {
        if (null == srcFile || !srcFile.exists()) {
            return null;
        }
        String destDirs = BASE_PATH  + "/" + EAICoderUtil.getMD5Code(srcFile.getParent());
        File destFile = new File(destDirs);
        if(!destFile.exists()) { 
            destFile.mkdirs();
        }
        String destPath = destDirs + "/" + "sign_" + srcFile.getName();
        return destPath;
    }

    

    
    private static String getUnSignFilePath(File signedFile) {
        if (null == signedFile || !signedFile.exists()) {
            return null;
        }
        String srcDirs = BASE_PATH  + "/" + EAICoderUtil.getMD5Code(signedFile.getParent());
        File srcFile = new File(srcDirs);
        if(!srcFile.exists()) { 
            srcFile.mkdirs();
        }
        String srcPath = srcDirs + "/" + "un" + signedFile.getName();
        return srcPath;
    }

    
    
    public static String encryptFile(String id, String filePath) {
        if (!isSign || TextUtils.isEmpty(id) || TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        File file = new File(filePath);
        if (null == file || !file.exists()) {
            return filePath;
        }

        
        String destPath = getSignFilePath(file);
        File destfile = new File(destPath);
        if (null != destfile && destfile.exists()) {
            return destPath;
        }
        
        if (doEncryptFile(DEFAULT_PWD,id,destPath,file) != null) {
            return destPath;
        }

        return filePath;
    }

    
    public static String encryptFile(String pwd, String id, String filePath) {
        if (!isSign || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(id) || TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        File file = new File(filePath);
        if (null == file || !file.exists()) {
            return filePath;
        }

        
        String destPath = getSignFilePath(file);
        File destfile = new File(destPath);
        if (null != destfile && destfile.exists()) {
            return destPath;
        }
        
        if (doEncryptFile(pwd,id,destPath,file) != null) {
            return destPath;
        } else {
        }

        return filePath;
    }

    
    private static String doEncryptFile(String pwd, String id, String destPath, File file) {
        try {
            long filelen = file.length();
            long offset = 0;
            CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(file), AESCrypt.getEncryptCipher(getSignPwd(pwd,id)));
            FileOutputStream out = new FileOutputStream(destPath);
            byte[] buffer = new byte[2048];
            int len = cipherInputStream.read(buffer);
            while (len > 0) {
                out.write(buffer, 0, len);
                out.flush();
                offset += len;
                len = cipherInputStream.read(buffer);
            }
            out.close();
            cipherInputStream.close();
            return destPath;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    

    
    public static String decryptFile(String id, String signedFilePath) {
        if (!isSign || TextUtils.isEmpty(id) || TextUtils.isEmpty(signedFilePath)) {
            return signedFilePath;
        }
        File file = new File(signedFilePath);
        if (null == file || !file.exists()) {
            return signedFilePath;
        }

        
        String srcPath = getUnSignFilePath(file);
        File srcfile = new File(srcPath);
        if (null != srcfile && srcfile.exists()) {
            return srcPath;
        }
        if (doDecryptFile(DEFAULT_PWD,id,srcPath,file) != null) {
            return srcPath;
        }
        return signedFilePath;
    }

    
    public static String decryptFile(String pwd, String id, String signedFilePath) {
        if (!isSign || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(id) || TextUtils.isEmpty(signedFilePath)) {
            return signedFilePath;
        }
        File file = new File(signedFilePath);
        if (null == file || !file.exists()) {
            return signedFilePath;
        }
        
        String srcPath = getUnSignFilePath(file);
        File srcfile = new File(srcPath);
        if (null != srcfile && srcfile.exists()) {
            return srcPath;
        }
        if (doDecryptFile(pwd,id,srcPath,file) != null) {
            return srcPath;
        }
        return signedFilePath;
    }

    
    private static String doDecryptFile(String pwd, String id, String srcPath, File file) {
        try {
            long filelen = file.length();
            long offset = 0;
            FileInputStream fileInputStream = new FileInputStream(file);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(new FileOutputStream(srcPath), AESCrypt.getDecryptCipher(getSignPwd(pwd,id)));
            byte[] buffer = new byte[2048];
            int len = fileInputStream.read(buffer);
            while (len > 0) {
                cipherOutputStream.write(buffer, 0, len);
                cipherOutputStream.flush();
                offset += len;
                len = fileInputStream.read(buffer);
            }
            cipherOutputStream.close();
            fileInputStream.close();
            return srcPath;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
