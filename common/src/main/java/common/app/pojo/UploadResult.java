

package common.app.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;



public class UploadResult {
    
    public final static String TYPE_AVATAR = "avatar";
    
    public final static String TYPE_EVALUATE = "evaluation";


    public final static String TYPE_MALL_LOGO = "logo";
    public final static String TYPE_MALL_BACKGROUND = "background";
    public final static String TYPE_MALL_BANNER_PC = "banner_pc";
    public final static String TYPE_MALL_BANNER_WAP = "banner_wap";


    public final static String TYPE_IDCARD = "idcard";
    public final static String TYPE_USERCARD = "usercard";
    public final static String TYPE_ORGLICENSE = "orglicense";


    public final static String TYPE_BANKCARD = "bankcard";

    public final static String TYPE_PRODUCT = "product";

    public final static String TYPE_ARTICLE = "article";

    public final static String TYPE_ALBUM = "album";
    public final static String TYPE_GUG_SUPPLY = "gug_supply";


    public final static String TYPE_COLLECT = "collect";
    public final static String TYPE_COLLECT_DETAIL = "collect_detail";
    public final static String TYPE_COLLECT_LEVEL = "collect_level";
    public final static String TYPE_COLLECT_PROGRESS = "collect_progress";


    public final static String TYPE_SECONDBUY = "secondbuy";
    public final static String TYPE_GROUPBUY = "groupbuy";
    public final static String TYPE_LOGISTICS_SHEET = "logistics_sheet";
    public final static String TYPE_REJECT = "reject";
    public final static String TYPE_FEEDBACK = "feedback";


    private String file;

    @SerializedName("base_url")
    private String baseUrl;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    
    public static class UploadFiles {
        @SerializedName("file")
        public List<String> files;

        @SerializedName("base_url")
        public String baseUrl;
    }
}
