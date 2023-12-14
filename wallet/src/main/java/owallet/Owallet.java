

package owallet;


public class Owallet {
    public static native String sign(String var0, String var1);

    public static native String signBtc(String var0, String var1);

    public static native String signIdentifier(String var0, String var1);

    public static native String signNew(String var0, String var1);

    public static native String signTron(String var0, String var1);

    public static native String signTron2(String var0, String var1);

    public static native String createEthTransaction(String var0);

    public static native String createFilTransaction(String var0);

    public static native String createTronTransaction(String var0);

    public static native String createXrpTransaction(String var0);

    public static native String createBtcTransaction(String var0);

    public static native String createEthToken(String var0);

    public static String getIdentifier(String var0){
        return "{\"result\":\"unkown\"}";
    }

    public static native String createXrpTrust(String var0);

    public static native String xrpSign(String var0, String var1);

    public static native String newEthWalletByPrivKey(String var0, String var1);

    public static native String tronHex2Addr(String var0);
}
