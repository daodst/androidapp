

package com.wallet.ctc.crypto;

import com.wallet.ctc.util.LogUtil;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Pattern;



public class CoinValidationUtil {

    private final static String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static String ETH_RULE = "^(0x)[0-9a-fA-F]{40}$";
    private static String TRX_RULE = "^(T)[a-zA-Z0-9]{33}$";

    public static boolean isWalletAddress(int type, String address) {
        LogUtil.d(address + "   " + address.length());
        if (type == WalletUtil.ETH_COIN || type == WalletUtil.ETF_COIN || type == WalletUtil.DMF_COIN || type == WalletUtil.DMF_BA_COIN || type == WalletUtil.HT_COIN || type == WalletUtil.BNB_COIN || type == WalletUtil.DM_COIN || type == WalletUtil.MCC_COIN) {
            return Pattern.matches(ETH_RULE, address);
        }
        if (WalletUtil.BTC_COIN == type || type == WalletUtil.DOGE_COIN || type == WalletUtil.SGB_COIN || WalletUtil.DOT_COIN == type) {
            return bitCoinAddressValidate(address);
        }
        
        if (type == WalletUtil.TRX_COIN) {
            return Pattern.matches(TRX_RULE, address);
        }
        return true;
    }


    
    public static boolean bitCoinAddressValidate(String addr) {
        if (addr.length() < 26 || addr.length() > 35)
            return false;
        byte[] decoded = decodeBase58To25Bytes(addr);
        if (decoded == null)
            return false;

        byte[] hash1 = sha256(Arrays.copyOfRange(decoded, 0, 21));
        byte[] hash2 = sha256(hash1);

        return Arrays.equals(Arrays.copyOfRange(hash2, 0, 4), Arrays.copyOfRange(decoded, 21, 25));
    }


    private static byte[] decodeBase58To25Bytes(String input) {
        BigInteger num = BigInteger.ZERO;
        for (char t : input.toCharArray()) {
            int p = ALPHABET.indexOf(t);
            if (p == -1)
                return null;
            num = num.multiply(BigInteger.valueOf(58)).add(BigInteger.valueOf(p));
        }

        byte[] result = new byte[25];
        byte[] numBytes = num.toByteArray();
        System.arraycopy(numBytes, 0, result, result.length - numBytes.length, numBytes.length);
        return result;
    }

    private static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
