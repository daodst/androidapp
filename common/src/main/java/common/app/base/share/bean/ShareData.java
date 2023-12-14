

package common.app.base.share.bean;

import android.text.TextUtils;



public class ShareData {
    private String link;
    public String qrcode;
    public String logo;
    public String title;
    public String content;
    public int type = 0;
    public String bg_app_share;
    private String url = "";
    public String data_id;
    public String data_table;

    @Override
    public String toString() {
        return "ShareData{" +
                "link='" + link + '\'' +
                ", qrcode='" + qrcode + '\'' +
                ", logo='" + logo + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", bg_app_share='" + bg_app_share + '\'' +
                ", url='" + url + '\'' +
                ", data_id='" + data_id + '\'' +
                ", data_table='" + data_table + '\'' +
                '}';
    }

    public String getLink() {
        if (null == link || TextUtils.isEmpty(link)) {
            if (null != url) {
                link = url;
                return url;
            }
            return "";
        }
        return link;
    }

    public String getUrl() {
        if (null == url || TextUtils.isEmpty(url)) {
            if (null != link) {
                url = link;
                return link;
            }
            return "";
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
