package com.app.pojo;

import android.net.Uri;
import android.text.TextUtils;

import common.app.utils.AllUtils;


public class NodeConfigBean {
    public String node_address;
    public String number_index;
    public String node_name;
    public String chain_id;

    public String ws_url;
    
    public String tts_url;
    
    public String chatCall;
    public String im_url;
    public String node_smart_url;
    public String node_info_url;

    public NodeConfigBean() {

    }

    public NodeConfigBean(String nodeAddr, String numberIndex, String nodeName) {
        this.node_address = nodeAddr;
        this.number_index = numberIndex;
        this.node_name = nodeName;
    }

    
    public boolean isValidate() {
        if (TextUtils.isEmpty(node_address) || TextUtils.isEmpty(number_index)) {
            return false;
        } else {
            return true;
        }
    }

    
    public static NodeConfigBean prase(String nodeUrl, String numberIndex, String nodeName) {
        NodeConfigBean nodeConfigBean = new NodeConfigBean(nodeUrl, numberIndex, nodeName);
        return NodeConfigBean.prase(nodeConfigBean);
    }

    
    public static NodeConfigBean prase(NodeConfigBean nodeConfig) {
        if (null == nodeConfig || !nodeConfig.isValidate()) {
            return null;
        }
        String configNodeUrl = nodeConfig.node_address;
        Uri uri = null;
        String host = "";
        String scheme = "http";
        if (!configNodeUrl.contains("://")) {
            configNodeUrl = "http://" + configNodeUrl;
            try {
                uri = Uri.parse(configNodeUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (uri == null || TextUtils.isEmpty(uri.getHost())) {
                return null;
            }
            host = uri.getHost();
            if (AllUtils.isIp(host)) {
                scheme = "http";
                configNodeUrl = "http://" + nodeConfig.node_address;
            } else {
                scheme = "https";
                configNodeUrl = "https://" + nodeConfig.node_address;
            }
        } else {
            try {
                uri = Uri.parse(configNodeUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (uri == null || TextUtils.isEmpty(uri.getHost())) {
                return null;
            }
            scheme = uri.getScheme();
            host = uri.getHost();
        }
        if (!configNodeUrl.endsWith("/")) {
            configNodeUrl = configNodeUrl + "/";
        }
        
        String imUrl = scheme + "://" + host + ":28008";
        String nodeSmartUrl = scheme + "://" + host + ":8545";
        String nodeInfoUrl = scheme + "://" + host + ":1317/";
        String chat23478 = scheme + "://" + host + ":23478/";
        String tts_url = scheme + "://" + host + ":25690/";

        NodeConfigBean newNodeConfig = new NodeConfigBean();
        newNodeConfig.node_address = configNodeUrl;
        newNodeConfig.im_url = imUrl;
        newNodeConfig.tts_url = tts_url;
        newNodeConfig.ws_url = host + ":28008";
        newNodeConfig.chatCall = chat23478;
        newNodeConfig.node_smart_url = nodeSmartUrl;
        newNodeConfig.node_info_url = nodeInfoUrl;

        newNodeConfig.number_index = nodeConfig.number_index;
        newNodeConfig.node_name = nodeConfig.node_name;
        newNodeConfig.chain_id = nodeConfig.chain_id;
        return newNodeConfig;
    }


    @Override
    public String toString() {
        return "NodeConfigBean{" +
                "node_address='" + node_address + '\'' +
                ", number_index='" + number_index + '\'' +
                ", node_name='" + node_name + '\'' +
                ", im_url='" + im_url + '\'' +
                '}';
    }
}
