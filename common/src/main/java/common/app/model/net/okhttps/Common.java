

package common.app.model.net.okhttps;

import common.app.base.model.http.HttpMethods;



public class Common {
    public final static String SDCARD_MNT = "/mnt/sdcard";
    public final static String SDCARD = "/sdcard";

    public static final String HTTPURL = HttpMethods.BASE_URL;
    public static final String HTTPURL2 = HttpMethods.BASE_URL2;
    public static final String IMGURl = HttpMethods.BASE_SITE;
    
    public static final String LOGO = HTTPURL+"user/login/Login";
    
    public static final String FENSIQUANLIEBIAO = HTTPURL+"user/fandom/Lists";

    
    public static final String FENSIQUANFABIAOSHIPIN = HTTPURL+"user/fandom/Insert";
    
    public static final String MYFANS = HTTPURL+"app/fans/Lists";
    
    public static final String DELETEMYDONGTAI = HTTPURL+"user/fandom/Delete";
    
    public static final String FENSIQUANDIANZAN = HTTPURL+"user/fandom/ThumbsUp";
    
    public static final String FENSIQUANQUXIAODIANZAN = HTTPURL+"user/fandom/DeleteThumbsUp";
    
    public static final String FENSIQUANTOPBG = HTTPURL+"user/fandom/EditFandomInfo";
    
    public static final String FENSIQUANHUIFUPINGLUN = HTTPURL+"user/fandom/Reply";
    
    public static final String FENSIQUANDELETEPINGLUN = HTTPURL+"user/fandom/DeleteReply";
    
    public static final String GOUWUCHELIST = HTTPURL+"buyer/cart/lists";
    
    public static final String ADDGOUWUCHE = HTTPURL+"buyer/cart/Add";
    
    public static final String DELETEGOUWUCHE = HTTPURL+"buyer/cart/Delete";
    
    public static final String EDITGOUWUCHE = HTTPURL+"buyer/cart/Edit";
    
    public static final String LISHILIST = HTTPURL+"user/history/getHistoryList";
    
    public static final String DELETELISHI = HTTPURL+"user/history/DelHistory";
    
    public static final String SHOUCANGLIST = HTTPURL+"user/favorite/lists";
    
    public static final String SHOUCANGLIST_OFFLINE = HTTPURL2+"user/favorite/lists";
    
    public static final String DELETESHOUCANG_OFFLINE = HTTPURL2+"user/favorite/Delete";
    
    public static final String DELETESHOUCANG = HTTPURL+"user/favorite/Delete";
    
    public static final String DINGDAN = HTTPURL+"buyer/order/Preview";
    
    public static final String DINGDANTIJIAO = HTTPURL+"buyer/order/Create";
    
    public static final String GETUSERDATA = HTTPURL+"user/user/GetUserInfo";
    
    public static final String GETDIZHI = HTTPURL+"user/address/Info";
    
    public static final String GETMORENDIZHI = HTTPURL+"user/address/DefaultAddress";
    
    public static final String GETSHANGPINGUIGE = HTTPURL+"basic/product_ext/Info";
    
    public static final String MYHAOYOU = HTTPURL+"user/fans/FriendListsMy";
    
    public static final String SHANGMENZITI = HTTPURL+"buyer/order/GetValidDelivery";
    
    public static final String NOTICE_DETAIL = HTTPURL+"basic/notice/Info";
    
    public static final String PRODUCT_DETAIL = HTTPURL+"basic/product/Desc";
    
    public static final String RAPIDPAY = HTTPURL + "buyer/order/GetQuickOrderList";
    
    public static final String UPLOADVIDEO = HTTPURL + "user/file/UploadVideo";
    
    public static final String GETOTHERINFO = HTTPURL+"user/user/GetOtherInfo";

    
    public static final String GETDEALLIST = HTTPURL + "trade/trade/GetDealOrderList";
    
    public static final String TRADCANCEL = HTTPURL + "trade/trade/Cancel";

    
    public static final String GETUSERINFO = HTTPURL+"user/user/GetUserInfo";

    
    public static final String GETXIEYIURL = HTTPURL+"user/user/GetAgreement";


}
