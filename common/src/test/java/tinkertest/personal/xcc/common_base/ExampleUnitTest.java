package tinkertest.personal.xcc.common_base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        String timeStr = "2023-04-15T05:42:41.099900627Z";
        Date date = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = dateFormat.parse(timeStr);
        } catch (Exception e){
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            dateFormat2.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = dateFormat2.parse(timeStr);
        }

        if (null != date) {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String format = dateFormat2.format(date);
            System.out.println("format="+format);
        } else {
            System.out.println("date = null");
        }
    }

    @Test
    public void testbyte() throws Exception {
        Random random = new Random();
        byte[] array = new byte[32];
        random.nextBytes(array);
        for (int i=0; i<array.length; i++) {
            System.out.println(i+":"+array[i]);
        }
    }

    @Test
    public void testBiginteger() throws Exception{
        BigInteger _amount = new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
        System.out.println(_amount.toString());
    }

    @Test
    public void testArrayList() throws Exception{
        List<String> list = new ArrayList<>(0);
        list.add("1");
        list.add("2");
        System.out.println(Arrays.toString(list.toArray()));
    }

    @Test
    public void testHash() throws  Exception{
        String avatarPath = "file:
        String logo = avatarPath+"0.png";
        String[] address = {
                "dex1wkz6xwz47nmx2lkwrj8n7wn45ltyshf0vpjuuq",
                "0x80b5a32E4F032B2a058b4F29EC95EEfEEB87aDcd",
                "0x7585A33855F4F6657eCe1C8F3F3A75a7d6485d2F",
                "0x7585A33855F4F6657eCe1C8F3F3A75a7d6485d2F",
                "dex1tt280prhy8sf4nlexkfatn8yf22n75f0nafejv",
                "0x80b5a32E4F032B2a058b4F29EC95EEfEEB87aDcd",
                "0x5aD477847721e09acfF93593d5CCe44a953F512f",
                "dst1zr68gt7vezfrgvysgf76j2mg2wm0ck3r234hdz",
                "dst1zr68gt7vezfrgvysgf76j2mg2wm0ck3r234hdz",
                "40","50","80"};

        
        for (String addr : address) {
            int hashCode = Math.abs(addr.hashCode());
            String hexHash = new BigInteger(addr.hashCode()+"").toString(16);
            System.out.println("hashCode="+hashCode+", hexHash="+hexHash);
            int index = hashCode % 20 + 1;
            logo = avatarPath+index+".png";
            System.out.println("logo="+logo);
        }

        System.out.println(" 20%20="+(20%20+1));
        System.out.println(" 40%20="+(40%20+1));
        System.out.println(" 80%20="+(80%20+1));
        System.out.println(" 21%20="+(21%20+1));
        System.out.println(" 41%20="+(41%20+1));
        System.out.println(" 39%20="+(39%20+1));
    }

    @Test
    public void testStrContains() throws  Exception{
        String a = "5465464dst";
        if (a.contains("DST")) {
            System.out.println("contains dst");
        } else {
            System.out.println("no contais dst");
        }
    }

    @Test
    public void testStrSub() throws  Exception{
        String msg = "java.net.SocketTimeoutException: failed to connect to data-seed-prebsc-2-s1.binance.org/67.228.235.93 (port 8545) from /192.168.31.14 (port 37684) after 10000ms";
        int startIndex = msg.lastIndexOf("to")+2;
        int endIndex = msg.indexOf("/");
        String url = msg.substring(startIndex, endIndex);
        System.out.println(startIndex+","+endIndex+", "+url);
    }


    @Test
    public void testParse() throws  Exception{
        getMccSmartChainId("");
        getMccSmartChainId(null);
        getMccSmartChainId("ssjsljsl");
        getMccSmartChainId("dstus_iii-1");
        getMccSmartChainId("dstus_iii-skj");
        getMccSmartChainId("dstus_iii_888-1");
        getMccSmartChainId("dstus_888-1");
        getMccSmartChainId("dstus_888");
    }

    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static void getMccSmartChainId(String evmosChainId) {
        if (isEmpty(evmosChainId)) {
           System.out.println("chainId is empty use default value");
            return;
        }
        String[] arrayStr = evmosChainId.split("_");
        if (null != arrayStr && arrayStr.length == 2) {
            String numberStr = arrayStr[1];
            if (isEmpty(numberStr)) {
                System.out.println("numberStr is empty return default value");
                return ;
            }
            String number = "";
            String[] numberArray = numberStr.split("-");
            if (null != numberArray && numberArray.length > 0) {
                number = numberArray[0];
            }
            if (isEmpty(number)) {
                System.out.println("chainid parse error "+numberStr);
                return ;
            }
            try {
                Integer integer = Integer.parseInt(number);
                if (null != integer) {
                    System.out.println("value ="+integer.intValue());
                } else {
                    System.out.println("chainid parse error integer null "+numberStr);
                    return ;
                }
            } catch (NumberFormatException e){
                e.printStackTrace();
                System.out.println("chainid parse exception : "+e.getMessage());
                return ;
            }
        } else {
            System.out.println("invalidate chainId : "+evmosChainId);
            return ;
        }
    }


    @Test
    public void testSplite() throws  Exception{
        String a = "fc53f36f89e1ecc6f4978cdba1e37d96.d96";
        String[] array = a.split("\\.");
        System.out.println(Arrays.toString(array)+", "+array[0]+", "+array[1]);
        if (array[0].endsWith(array[1])) {
            System.out.println("is same");
        } else {
            System.out.println("no same");
        }


        String b = "fc53f36f89e1ecc6f4978cdba1e37d96 get nonce error";
        String[] words = b.split(" ");
        if (words != null && words.length > 0){
            System.out.println("words[0]="+words[0]);
        } else {
            System.out.println("words is null or size = 0");
        }

    }

    @Test
    public void testRandom() {
        Random random = new Random();
        System.out.println("---"+random.nextInt(3));
        System.out.println("---"+random.nextInt(3));
        System.out.println("---"+random.nextInt(3));
        System.out.println("---"+random.nextInt(3));
        System.out.println("---"+random.nextInt(3));


        System.out.println(getIndexNumByUserId("@jslkjsljsl:223434.nxn"));
        System.out.println(getIndexNumByUserId("@sdesss:6546465.nxn"));
        System.out.println(getIndexNumByUserId("@sserz:48465.nxn"));

        byte[] reulst = "a3@n:b2".getBytes(StandardCharsets.UTF_8);
        System.out.println("reulst ="+Arrays.toString(reulst));
    }


    
    public static String getIndexNumByUserId(String userId) {
        String indexNum = "";
        if (userId.contains(":")) {
            String[] strs = userId.split(":");
            if (strs.length == 2) {
                String str = strs[1];
                int index = str.indexOf(".");
                if (index != -1 && index > 0) {
                    indexNum = str.substring(0, index);
                }
            }
        }
        return indexNum;
    }


    @Test
    public void testAESEncry() {
        String jsonParams = "{\"addr\":\"dst1qx9mk2anp8qrym28rmmup4jqu8x0mywuttx365\",\"servername\":\"1111111.nxn\",\"timestamp\":\"1697847895\",\"wallet_sign\":\"57f22b460960f25c2dd298b20554beca343d787aef6b195480b56f307b7e8ffe3adc40636b1ada7dbf99f5d7cb053a9a247e0ff8e52214889ef56090836dad7601\",\"wallet_pub\":\"03332e781432278d66b7a2942fce5c445344b6e2fd32acfebf358a1a6e2de36cd1\",\"api_sign\":\"3a3b350097e121466061b0a06592bfa4\"}";
        String password = "At$Tr_ibu$*tasdfAt$Tr_edv$*tFg8d";
        String test = getAESToHexCode(jsonParams, password);
        System.out.println("signStr="+test);
    }


    public static String getAESToHexCode(String text, String key){
        String result = null;
        try {

            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"),"AES");


            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encodeResult = cipher.doFinal(text.getBytes("UTF-8"));


            result = byte2Hex(encodeResult);
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


    
    @Test
    public void testMulModFileName() throws IOException {
        File folder = new File("F:\\XuccWork\\Im.Dao\\rootWalletIcon\\");
        String[] fileNames = folder.list();
        File toFolder = new File(folder, "src");

        if (!toFolder.exists()) {
            toFolder.mkdirs();
        }
        File xmlFile = new File(toFolder, "test.txt");
        if (xmlFile.exists()) {
            xmlFile.delete();
        }

        if (fileNames != null && fileNames.length > 0) {
            InputStream is = null;
            OutputStream os = null;
            OutputStream xos = new FileOutputStream(xmlFile);

            for (String fileName :fileNames){
                File file = new File(folder, fileName);
                if (file.isFile()) {
                    String newFileName = fileName.replaceAll("MagicParticlesElement06_00", "rootv");
                    try {
                        File newFile = new File(toFolder, newFileName);
                        if (newFile.exists()) {
                            newFile.delete();
                        }
                        is = new FileInputStream(file);
                        os = new FileOutputStream(newFile);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }

                        String pngName = newFileName.substring(0, newFileName.lastIndexOf("."));

                        StringBuilder sb = new StringBuilder();
                        sb.append("<item android:drawable=\"@mipmap/")
                                .append(pngName)
                                .append("\" android:duration=\"33\" />");
                        sb.append("\n");
                        xos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        is.close();
                        os.close();
                    }
                }
            }

            xos.close();
        }


        






    }

}
