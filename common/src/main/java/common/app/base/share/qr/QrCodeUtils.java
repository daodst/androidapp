

package common.app.base.share.qr;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import common.app.ActivityRouter;
import common.app.AppApplication;
import common.app.BuildConfig;
import common.app.R;
import common.app.base.fragment.mall.api.CommonMallApi;
import common.app.base.model.http.HttpMethods;
import common.app.base.share.AppShare;
import common.app.pojo.SchemeBean;
import common.app.utils.SpUtil;



public class QrCodeUtils {

    
    private static final String KEY_TYPE = "qrtype";
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "intro";
    private static final String KEY_TOTAL = "total";
    private static final String KEY_REMARK = "remark";
    private static final String KEY_FIXED = "fixed";
    private static final String TIME = "TIME";

    private static final String URL_BASE = HttpMethods.BASE_SITE + "wap/#/download/share?";
    
    private static final String URL_USER = URL_BASE;
    private static final String URL_APP = URL_BASE;
    private static final String TAG = "QrCodeUtils";

    private Context mContext;

    private CommonMallApi mApi = new CommonMallApi();
    private Gson gson = new Gson();

    private QrCodeUtils(Context context) {
        this.mContext = context;
    }

    public synchronized static QrCodeUtils createInstance(Context context) {
        return new QrCodeUtils(context);
    }

    
    private static Bitmap createQrBitmapByHw(String content, int width, int height) {
        int qrType = HmsScan.QRCODE_SCAN_TYPE;
        try {
            
            HmsBuildBitmapOption options = new HmsBuildBitmapOption.Creator().setBitmapMargin(2).setBitmapColor(Color.BLACK).setBitmapBackgroundColor(Color.WHITE).create();
            Bitmap resultImage = ScanUtil.buildBitmap(content, qrType, width, height, options);
            return resultImage;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Bitmap createQrBitmap(String content,  int width, int height) {
        return createQrBitmapByHw(content, width, height);
    }

    private static Bitmap createQrBitmap(String content) {
        return createQrBitmapByHw(content, 280, 280);
    }

    
    public static String analyzeImgFileQr(String localImagPath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(localImagPath);
            HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(AppApplication.getInstance().getApplicationContext(), bitmap, new HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create());
            if (hmsScans != null && hmsScans.length > 0 && hmsScans[0] != null && !TextUtils.isEmpty(hmsScans[0].getOriginalValue())) {
                return hmsScans[0].getOriginalValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    
    public static String analyzeBitmap(Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        try {
            HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(AppApplication.getInstance().getApplicationContext(), bitmap, new HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create());
            if (hmsScans != null && hmsScans.length > 0 && hmsScans[0] != null && !TextUtils.isEmpty(hmsScans[0].getOriginalValue())) {
                return hmsScans[0].getOriginalValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    
    public static Bitmap getQrCode(String userName) {
        return createQrBitmap(userName);
    }





    private String mTitle = "";

    public void parseQrCode(String content, String title) {
        mTitle = title;
        parseQrCode(content);
    }

    
    public void parseQrCode(String content) {
        if (content.startsWith("iban")) {
            try {
                String[] pam = content.split("\\?");
                if (pam.length == 2) {
                    String other = pam[0];
                    String data = content.substring(9, other.length());
                    String parms = pam[1];
                    String amountStr = "";
                    String type = "";
                    Map<String, String> contentMap = new HashMap();
                    if (parms.contains("&")) {
                        String[] splitResult = parms.split("\\&");
                        for (String param : splitResult) {
                            String[] paramArray = param.split("\\=");
                            contentMap.put(paramArray[0], paramArray[1]);
                        }
                        amountStr = contentMap.get("amount");
                        type = contentMap.get("token").toUpperCase();
                    }
                    String toAddress;
                    if (type.equals("BTC") || type.equals("LTC") || type.equals("DOGE")) {
                        toAddress = data;
                    } else {
                        toAddress = new BigInteger(data, 36).toString(16);
                        if (toAddress.length() < 40) {
                            for (int i = toAddress.length(); i < 40; i++) {
                                toAddress = "0" + toAddress;
                            }
                        }
                        toAddress = "0x" + toAddress;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("amountStr", amountStr);
                    intent.putExtra("toAddress", toAddress);
                    intent.putExtra("tokenName", type);
                    ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("amountStr", "");
                    intent.putExtra("toAddress", content);
                    intent.putExtra("tokenName", "");
                    ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
                }
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.putExtra("amountStr", "");
                intent.putExtra("toAddress", content);
                intent.putExtra("tokenName", "");
                ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
            }
            return;
        }
        if (content.startsWith("ethereum") || content.startsWith("tron") || content.startsWith("bitcoin") || content.startsWith("ripple")) {
            try {
                String[] pam = content.split("\\?");
                if (pam.length == 2) {
                    String walletAddress = pam[0];
                    walletAddress = walletAddress.replace("ethereum:", "");
                    walletAddress = walletAddress.replace("bitcoin:", "");
                    walletAddress = walletAddress.replace("ripple:", "");
                    walletAddress = walletAddress.replace("tron:", "");
                    String parms = pam[1];
                    String amountStr = "";
                    String type = "";
                    String des = "0";
                    double dec=18;
                    Map<String, String> contentMap = new HashMap();
                    if (parms.contains("&")) {
                        String[] splitResult = parms.split("\\&");
                        for (String param : splitResult) {
                            String[] paramArray = param.split("\\=");
                            if(paramArray.length>1) {
                                contentMap.put(paramArray[0], paramArray[1]);
                            }
                        }
                        amountStr = contentMap.get("value");
                        des = contentMap.get("decimal");
                        dec=new BigDecimal(des).intValue();
                        if (null != contentMap.get("contractAddress")) {
                            type = contentMap.get("contractAddress").toUpperCase();
                        }
                    }
                    if (content.startsWith("bitcoin")) {
                        amountStr = contentMap.get("amount");
                    } else if (content.startsWith("ripple")) {
                        if (!TextUtils.isEmpty(amountStr))
                            amountStr = new BigDecimal(amountStr).divide(new BigDecimal(Math.pow(10, dec)), (int)dec, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
                    } else if (content.startsWith("tron")) {
                        if (!TextUtils.isEmpty(amountStr) )
                            amountStr  = new BigDecimal(amountStr).divide(new BigDecimal(Math.pow(10, dec)), (int)dec, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
                    } else {
                        if (!walletAddress.startsWith("0x") && walletAddress.length() != 34 && !walletAddress.startsWith("3f")) {
                            if ((!TextUtils.isEmpty(BuildConfig.ENABLE_MCC_ADDRESS) && walletAddress.startsWith(BuildConfig.ENABLE_MCC_ADDRESS))) {
                                
                            } else {
                                walletAddress = "0x" + walletAddress;
                            }
                        }
                        if (!TextUtils.isEmpty(amountStr) )
                            amountStr = new BigDecimal(amountStr).divide(new BigDecimal(Math.pow(10, dec)), (int)dec, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
                    }
                    Intent intent = new Intent();
                    intent.putExtra("amountStr", amountStr);
                    intent.putExtra("toAddress", walletAddress);
                    intent.putExtra("tokenName", type);
                    ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("amountStr", "");
                    intent.putExtra("toAddress", content);
                    intent.putExtra("tokenName", "");
                    ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.putExtra("amountStr", "");
                intent.putExtra("toAddress", content);
                intent.putExtra("tokenName", "");
                ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
            }
            return;
        }
        
        Map<String, String> contentMap = doParse(content);

        if (contentMap.containsKey("articleid")) {
            String id = contentMap.get("articleid");
            try {
                Class clazz = Class.forName("com.wallet.ctc.base.BaseWebViewNoTitleActivity");
                Intent intent = new Intent(mContext, clazz);
                intent.putExtra("url", SpUtil.getHostApi() + "home/article/info/appid/" + SpUtil.getAppid() + "/id/" + id + "/inapp");
                intent.putExtra("id", id);
                mContext.startActivity(intent);
            } catch (Exception e) {

            }
            return;
        }

        
        if (contentMap.containsKey(KEY_TYPE)) {
            String qrType = contentMap.get(KEY_TYPE);
            switch (qrType) {
                default:
                    startNewWebPage(content);
                    
                    break;
            }
        } else {
            if (content.startsWith("http://") || content.startsWith("https://")) {
                startNewWebPage(content);
            } else if (content.startsWith("{")) {
                try {
                    Intent intent = new Intent();
                    intent.putExtra("data", content);
                    ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
                } catch (Exception e) {
                    startNewWebPage(content);
                }
            } else {
                Intent intent = new Intent();
                intent.putExtra("amountStr", "");
                intent.putExtra("toAddress", content);
                intent.putExtra("tokenName", "");
                intent.putExtra("data", content);
                ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
            }
        }
    }

    
    public SchemeBean parseScheme(String content) {
        try {
            String[] pam = content.split("\\?");
            if (pam.length == 2) {
                String walletAddress = pam[0];
                String scheme=walletAddress.substring(0,walletAddress.indexOf(":"));
                walletAddress = walletAddress.replace("ethereum:", "");
                walletAddress = walletAddress.replace("bitcoin:", "");
                walletAddress = walletAddress.replace("ripple:", "");
                walletAddress = walletAddress.replace("tron:", "");
                String parms = pam[1];
                String amountStr = "";
                String type = "";
                String des = "0";
                int dec=18;
                Map<String, String> contentMap = new HashMap();
                if (parms.contains("&")) {
                    String[] splitResult = parms.split("\\&");
                    for (String param : splitResult) {
                        String[] paramArray = param.split("\\=");
                        contentMap.put(paramArray[0], paramArray[1]);
                    }
                    amountStr = contentMap.get("value");
                    des = contentMap.get("decimal");
                    dec=new BigDecimal(des).intValue();
                    if (null != contentMap.get("contractAddress")) {
                        type = contentMap.get("contractAddress").toUpperCase();
                    }
                }
                if (content.startsWith("bitcoin")) {
                    amountStr = contentMap.get("amount");
                } else if (content.startsWith("ripple")) {
                    if (!TextUtils.isEmpty(amountStr))
                        amountStr = new BigDecimal(amountStr).divide(new BigDecimal(Math.pow(10, dec))).toPlainString();
                } else if (content.startsWith("tron")) {
                    if (!TextUtils.isEmpty(amountStr))
                        amountStr = new BigDecimal(amountStr).divide(new BigDecimal(Math.pow(10, dec))).toPlainString();
                } else {
                    if (!walletAddress.startsWith("0x") && walletAddress.length() != 34 && !walletAddress.startsWith("3f")) {
                        if ((!TextUtils.isEmpty(BuildConfig.ENABLE_MCC_ADDRESS) && walletAddress.startsWith(BuildConfig.ENABLE_MCC_ADDRESS))) {
                            
                        } else {
                            walletAddress = "0x" + walletAddress;
                        }
                    }
                    if (!TextUtils.isEmpty(amountStr))
                        amountStr = new BigDecimal(amountStr).divide(new BigDecimal(Math.pow(10, dec))).toPlainString();
                }
                SchemeBean intent = new SchemeBean();
                intent.setScheme(scheme);
                intent.setAmountStr(amountStr);
                intent.setToAddress(walletAddress);
                intent.setTokenName(type);
                return intent;
            } else {
                SchemeBean intent = new SchemeBean();
                intent.setAmountStr("");
                intent.setToAddress(content);
                intent.setTokenName("");
                return intent;
            }
        } catch (Exception e) {
            e.printStackTrace();
            SchemeBean intent = new SchemeBean();
            intent.setAmountStr("");
            intent.setToAddress(content);
            intent.setTokenName("");
            return intent;
        }
    }


    private void startNewWebPage(String url) {
        Intent intent = ActivityRouter.getIntent(mContext, ActivityRouter.Common.A_Web);
        intent.putExtra("url", url);
        intent.putExtra("title", mContext.getString(R.string.unknow_web_content_title));
        intent.putExtra("changeTitle", true);
        mContext.startActivity(intent);
    }

    
    private static Map<String, String> doParse(String content) {
        Map<String, String> contentMap = new HashMap();
        if (content.startsWith(HttpMethods.BASE_SITE.substring(0, HttpMethods.BASE_SITE.length() - 1)) ||
                content.startsWith(BuildConfig.SCHEME)) {
            if (content.contains("?")) {
                String[] data = content.split("\\?");
                if (null != data && data.length == 2) {
                    String parms = data[1];
                    if (parms.contains("&")) {
                        String[] splitResult = parms.split("\\&");
                        for (String param : splitResult) {
                            String[] paramArray = param.split("\\=");
                            contentMap.put(paramArray[0], paramArray[1]);
                        }
                    } else {
                        String[] paramArray = parms.split("\\=");
                        contentMap.put(paramArray[0], paramArray[1]);
                    }
                }
            }
        }
        return contentMap;
    }


    public static Bitmap creatQRCodeImg(String text, Bitmap logo) {
        return createQrBitmap(text);
    }


}
