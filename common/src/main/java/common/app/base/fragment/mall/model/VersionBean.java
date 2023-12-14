

package common.app.base.fragment.mall.model;

import android.text.TextUtils;



public class VersionBean {

    private int client_download_status;
    private int client_force_update;
    private String client_version;
    private String client_file;
    private String client_tips;
    private String client_version_force;

    public int getClient_download_status() {
        return client_download_status;
    }

    public void setClient_download_status(int client_download_status) {
        this.client_download_status = client_download_status;
    }

    public int getClient_force_update() {
        return client_force_update;
    }

    public void setClient_force_update(int client_force_update) {
        this.client_force_update = client_force_update;
    }

    public String getClient_version() {
        return client_version;
    }

    public void setClient_version(String client_version) {
        this.client_version = client_version;
    }

    public String getClient_file() {
        return client_file;
    }

    public void setClient_file(String client_file) {
        this.client_file = client_file;
    }

    public String getClient_tips() {
        if(null==client_tips|| TextUtils.isEmpty(client_tips)){
            return "#";
        }
        return client_tips;
    }

    public void setClient_tips(String client_tips) {
        this.client_tips = client_tips;
    }

    public String getClient_version_force() {
        if(null==client_version_force){
            return "0.0";
        }
        return client_version_force;
    }

    public void setClient_version_force(String client_version_force) {
        this.client_version_force = client_version_force;
    }
}
