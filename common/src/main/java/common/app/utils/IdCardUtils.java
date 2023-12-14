

package common.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class IdCardUtils {
    
    private static String[] cityCode = {"11", "12", "13", "14", "15", "21",
            "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42",
            "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
            "63", "64", "65", "71", "81", "82", "91"};

    
    private static int[] power = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
            8, 4, 2};

    
    public static boolean isValidatedAllIdcard(String idcard) {
        if (idcard == null || "".equals(idcard)) {
            return false;
        }
        if (idcard.length() == 15) {
            return validate15IDCard(idcard);
        }
        return validate18Idcard(idcard);
    }

    
    public static boolean validate18Idcard(String idcard) {
        if (idcard == null) {
            return false;
        }

        
        if (idcard.length() != 18) {
            return false;
        }
        
        String idcard17 = idcard.substring(0, 17);

        
        if (!isDigital(idcard17)) {
            return false;
        }

        String provinceid = idcard.substring(0, 2);
        
        if (!checkProvinceid(provinceid)) {
            return false;
        }

        
        String birthday = idcard.substring(6, 14);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        try {
            Date birthDate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthDate);
            if (!tmpDate.equals(birthday)) {
                return false;
            }

        } catch (ParseException e1) {

            return false;
        }

        
        String idcard18Code = idcard.substring(17, 18);

        char[] c = idcard17.toCharArray();

        int[] bit = converCharToInt(c);

        int sum17 = 0;

        sum17 = getPowerSum(bit);

        
        String checkCode = getCheckCodeBySum(sum17);
        if (null == checkCode) {
            return false;
        }
        
        if (!idcard18Code.equalsIgnoreCase(checkCode)) {
            return false;
        }

        return true;
    }

    
    public static boolean validate15IDCard(String idcard) {
        if (idcard == null) {
            return false;
        }
        
        if (idcard.length() != 15) {
            return false;
        }

        
        if (!isDigital(idcard)) {
            return false;
        }

        String provinceid = idcard.substring(0, 2);
        
        if (!checkProvinceid(provinceid)) {
            return false;
        }

        String birthday = idcard.substring(6, 12);

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

        try {
            Date birthDate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthDate);
            if (!tmpDate.equals(birthday)) {
                return false;
            }

        } catch (ParseException e1) {

            return false;
        }

        return true;
    }

    
    public static String convertIdcarBy15bit(String idcard) {
        if (idcard == null) {
            return null;
        }

        
        if (idcard.length() != 15) {
            return null;
        }

        
        if (!isDigital(idcard)) {
            return null;
        }

        String provinceid = idcard.substring(0, 2);
        
        if (!checkProvinceid(provinceid)) {
            return null;
        }

        String birthday = idcard.substring(6, 12);

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

        Date birthdate = null;
        try {
            birthdate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthdate);
            if (!tmpDate.equals(birthday)) {
                return null;
            }

        } catch (ParseException e1) {
            return null;
        }

        Calendar cday = Calendar.getInstance();
        cday.setTime(birthdate);
        String year = String.valueOf(cday.get(Calendar.YEAR));

        String idcard17 = idcard.substring(0, 6) + year + idcard.substring(8);

        char[] c = idcard17.toCharArray();
        String checkCode = "";

        
        int[] bit = converCharToInt(c);

        int sum17 = 0;
        sum17 = getPowerSum(bit);

        
        checkCode = getCheckCodeBySum(sum17);

        
        if (null == checkCode) {
            return null;
        }
        
        idcard17 += checkCode;
        return idcard17;
    }

    
    private static boolean checkProvinceid(String provinceid) {
        for (String id : cityCode) {
            if (id.equals(provinceid)) {
                return true;
            }
        }
        return false;
    }

    
    private static boolean isDigital(String str) {
        return str.matches("^[0-9]*$");
    }

    
    private static int getPowerSum(int[] bit) {

        int sum = 0;

        if (power.length != bit.length) {
            return sum;
        }

        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < power.length; j++) {
                if (i == j) {
                    sum = sum + bit[i] * power[j];
                }
            }
        }
        return sum;
    }

    
    private static String getCheckCodeBySum(int sum17) {
        String checkCode = null;
        switch (sum17 % 11) {
            case 10:
                checkCode = "2";
                break;
            case 9:
                checkCode = "3";
                break;
            case 8:
                checkCode = "4";
                break;
            case 7:
                checkCode = "5";
                break;
            case 6:
                checkCode = "6";
                break;
            case 5:
                checkCode = "7";
                break;
            case 4:
                checkCode = "8";
                break;
            case 3:
                checkCode = "9";
                break;
            case 2:
                checkCode = "x";
                break;
            case 1:
                checkCode = "0";
                break;
            case 0:
                checkCode = "1";
                break;
        }
        return checkCode;
    }

    
    private static int[] converCharToInt(char[] c) throws NumberFormatException {
        int[] a = new int[c.length];
        int k = 0;
        for (char temp : c) {
            a[k++] = Integer.parseInt(String.valueOf(temp));
        }
        return a;
    }

    public static void main(String[] args) throws Exception {
        String idcard15 = "130321860311519";
        String idcard18 = "210102198617083732";
        
        System.out.println(isValidatedAllIdcard(idcard15));
        
        System.out.println(isValidatedAllIdcard(idcard18));
        
        System.out.println(convertIdcarBy15bit(idcard15));
    }
}
