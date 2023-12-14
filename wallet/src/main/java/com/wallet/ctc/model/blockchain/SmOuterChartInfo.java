package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class SmOuterChartInfo {

    private static final String TAG = "SmOuterChartInfo";
    public Map<String, String> destory_data;
    public Map<String, String> wallet_data;

    private SmChartInfo mDestoryChartInfo;
    private SmChartInfo mWalletChartInfo;

    public SmChartInfo getWalletChartInfo() {
        if (null == mDestoryChartInfo && null != destory_data) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd");
            mDestoryChartInfo = new SmChartInfo();
            mDestoryChartInfo.chatX = new ArrayList<>();
            mDestoryChartInfo.chatY = new ArrayList<>();
            for (Map.Entry<String, String> entry : destory_data.entrySet()) {
                String key = entry.getKey();
                try {
                    mDestoryChartInfo.chatX.add(dateFormat2.format(dateFormat.parse(key)));
                } catch (Exception e) {
                    mDestoryChartInfo.chatX.add(key);
                }
                String value = entry.getValue();
                try {
                    String s = new BigDecimal(value).divide(BigDecimal.valueOf(Math.pow(10, 18)), 6, RoundingMode.HALF_DOWN).toPlainString();
                    mDestoryChartInfo.chatY.add(Float.parseFloat(s));
                } catch (Exception e) {
                    mDestoryChartInfo.chatY.add(0f);
                }
            }
        }
        if (null == mDestoryChartInfo) {
            return new SmChartInfo();
        }
        return mDestoryChartInfo;
    }

    private String mDestoryAdress;

    public String getDestoryAdress() {
        if (null == mDestoryWallet && null != wallet_data) {
            BigDecimal sum = new BigDecimal("0");
            for (Map.Entry<String, String> entry : wallet_data.entrySet()) {
                sum = sum.add(new BigDecimal(entry.getValue()));
            }
            mDestoryWallet = getBigDecimalValue(sum);
        }
        return mDestoryWallet;
    }

    public String getBigDecimalValue(BigDecimal divide) {
        BigDecimal zero = BigDecimal.ZERO;
        if (divide.compareTo(zero) == 0) {
            return zero.toPlainString();
        } else {
            return divide.stripTrailingZeros().toPlainString();
        }
    }

    private String mDestoryWallet;

    public String getDestoryWallet() {
        if (null == mDestoryAdress && null != destory_data) {
            BigDecimal sum = new BigDecimal("0");
            for (Map.Entry<String, String> entry : destory_data.entrySet()) {
                sum = sum.add(new BigDecimal(entry.getValue()).divide(BigDecimal.valueOf(Math.pow(10, 18)), 6, RoundingMode.HALF_DOWN));
            }
            mDestoryAdress = getBigDecimalValue(sum);
        }
        return mDestoryAdress;

    }


    public SmChartInfo getDestoryChartInfo() {
        if (null == mWalletChartInfo && null != wallet_data) {
            mWalletChartInfo = new SmChartInfo();
            mWalletChartInfo.chatX = new ArrayList<>();
            mWalletChartInfo.chatY = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd");
            for (Map.Entry<String, String> entry : wallet_data.entrySet()) {
                String key = entry.getKey();
                try {
                    mWalletChartInfo.chatX.add(dateFormat2.format(dateFormat.parse(key)));
                } catch (ParseException e) {
                    mWalletChartInfo.chatX.add(key);
                }
                String value = entry.getValue();
                try {
                    mWalletChartInfo.chatY.add(Float.parseFloat(value));
                } catch (Exception e) {
                    mWalletChartInfo.chatY.add(0f);
                }
            }
        }
        if (null == mWalletChartInfo) {
            return new SmChartInfo();
        }

        return mWalletChartInfo;


    }
}
