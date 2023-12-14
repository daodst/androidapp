

package com.app.lg4e.pojo;

import com.google.gson.annotations.SerializedName;



public class AppVersion {
    @SerializedName("client_download_version")
    private String vesion;

    @SerializedName("client_download_androidurl")
    private String url;

    private String content; 

    public String getVesion() {
        return vesion;
    }

    public void setVesion(String vesion) {
        this.vesion = vesion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
