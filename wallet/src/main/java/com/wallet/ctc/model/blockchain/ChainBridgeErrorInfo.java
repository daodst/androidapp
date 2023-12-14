package com.wallet.ctc.model.blockchain;

import android.content.Context;
import android.text.TextUtils;

import com.wallet.ctc.R;

import java.util.Map;


public class ChainBridgeErrorInfo {

    
    public static final int ERROR_NODE_CONNECT_FAIL = 2;


    
    public static final int ERROR_SEND_TRANSACTION_FAIL = 3;


    
    public static final int ERROR_INIT_CLIENT_FAIL = 4;


    
    public static final int ERROR_GET_NONCE_FAIL = 5;

    
    public static final int ERROR_GET_GAS_PRICE_FAIL = 6;

    
    public static final int ERROR_SIGN_FAIL = 7;

    
    public static final int ERROR_GET_GAS_FAIL = 8;

    
    public static final int ERROR_START_DB_FAIL = 9;

    
    public static final int ERROR_INIT_DB_FAIL = 10;

    
    public static final int ERROR_NO_FOUND_ORDER = 11;

    
    public static final int ERROR_NO_FOUND_PRIVATEKEY = 12;

    
    public static final int ERROR_REQUEST_TIMEOUT = 13;


    
    public static boolean isNodeConnectError(Integer errorCode){
        return errorCode != null && errorCode == ERROR_NODE_CONNECT_FAIL;
    }

    public static boolean isNoPrivateKeyError(Integer errorCode){
        return errorCode != null && errorCode == ERROR_NO_FOUND_PRIVATEKEY;
    }


    
    public static boolean hasNoPrivateKeyError(Map<Integer,String> errorMap){
        if (errorMap == null || errorMap.isEmpty()){
            return false;
        }
        for(Map.Entry<Integer, String> entry : errorMap.entrySet()){
            Integer errorCode = entry.getKey();
            if (isNoPrivateKeyError(errorCode)){
                return true;
            }
        }
        return false;
    }


    
    public static boolean hasNodeConnectError(Map<Integer,String> errorMap){
        if (errorMap == null || errorMap.isEmpty()){
            return false;
        }
        for(Map.Entry<Integer, String> entry : errorMap.entrySet()){
            Integer errorCode = entry.getKey();
            if (isNodeConnectError(errorCode)){
                return true;
            }
        }
        return false;
    }


    public static String getErrorInfo(Context context, Map<Integer,String> errorMap){
        StringBuilder stringBuilder = new StringBuilder();
        if (null != errorMap && !errorMap.isEmpty()){
            int index = 0;
            for(Map.Entry<Integer, String> entry : errorMap.entrySet()){
                index++;
                if(!TextUtils.isEmpty(stringBuilder)){
                    stringBuilder.append("\n");
                }
                int errorCode = entry.getKey();
                String errorMsg = entry.getValue();
                String errorInfo = getErrorText(context, errorCode, errorMsg);
                if(errorMap.size() > 1){
                    stringBuilder.append(index+".");
                }
                stringBuilder.append(errorInfo);
            }
        }
        return stringBuilder.toString();
    }

    
    public static String getErrorText(Context context, int errorCode, String errorMsg){
        String errorInfo = "";
        String chainName = "";
        if(!TextUtils.isEmpty(errorMsg)) {
            if(errorMsg.contains("bsc") || errorMsg.contains("BSC")){
                chainName = "BSC";
            }
            if(errorMsg.contains("dst") || errorMsg.contains("DST")){
                chainName = "DST";
            }
            if(errorMsg.contains("eth") || errorMsg.contains("ETH")){
                chainName = "ETH";
            }
        }
        switch (errorCode){
            case ERROR_NODE_CONNECT_FAIL://%s chain node connect fail  %s--> bsc,dst,eth
                errorInfo = String.format(context.getString(R.string.chain_b_error_node_connect), chainName);
                break;
            case ERROR_SEND_TRANSACTION_FAIL://%s send transaction fail 
                errorInfo = String.format(context.getString(R.string.chain_b_error_send_fail), chainName);
                break;
            case ERROR_INIT_CLIENT_FAIL://not call %s ClientInit 
                errorInfo = String.format(context.getString(R.string.chain_b_error_init_client), chainName);
                break;
            case ERROR_GET_NONCE_FAIL://%s get nonce error 
                String[] words = errorMsg.split(" ");
                String addr = words != null && words.length > 0 ? words[0] : "";
                errorInfo = String.format(context.getString(R.string.chain_b_error_get_nonce), addr);
                break;
            case ERROR_GET_GAS_PRICE_FAIL://%s client gas price get error 
                errorInfo = String.format(context.getString(R.string.chain_b_error_get_gas_price), chainName);
                break;
            case ERROR_SIGN_FAIL://%s client signTx error 
                errorInfo = String.format(context.getString(R.string.chain_b_error_sign), chainName);
                break;
            case ERROR_GET_GAS_FAIL://%s client EstimateGas error 
                errorInfo = String.format(context.getString(R.string.chain_b_error_get_gas), chainName);
                break;
            case ERROR_START_DB_FAIL://initDB NewEngine error 
                errorInfo = context.getString(R.string.chain_b_error_start_db);
                break;
            case ERROR_INIT_DB_FAIL://initDB Sync error 
                errorInfo = context.getString(R.string.chain_b_error_init_db);
                break;
            case ERROR_NO_FOUND_ORDER://get Order error 
                errorInfo = context.getString(R.string.chain_b_error_found_order);
                break;
            case ERROR_NO_FOUND_PRIVATEKEY://private key not found 
                errorInfo = context.getString(R.string.chain_b_error_no_privatekey);
                break;
            case ERROR_REQUEST_TIMEOUT://requery time out 
                errorInfo = context.getString(R.string.chain_b_error_timeout);
                break;
            default:
                errorInfo = errorCode+":"+context.getString(R.string.error_unknow);
                break;
        }
        return errorInfo;
    }


    public static String getOtherMainError(Context context, int errorType, String msg){
        String errorInfo = "";
        if (errorType == -1){
            
            errorInfo = String.format(context.getString(R.string.chain_bridge_main_error_tip), "DST")+msg;
        } else if(errorType == -2){
            
            errorInfo = String.format(context.getString(R.string.chain_bridge_main_error_tip), "ETH")+msg;
        } else if(errorType == -3){
            
            errorInfo = String.format(context.getString(R.string.chain_bridge_main_error_tip), "BSC")+msg;
        } else {
            
            errorInfo = String.format(context.getString(R.string.chain_bridge_main_error_tip), "System")+msg;
        }
        return errorInfo;
    }




}
