

package common.app.im.event;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class Notice {
    public static final int REDPACKET_LOST = 0;
    
    public static final int LG_LOGIN_SCUESS = 1;

    
    
    public static final int LG_REST_SCUESS = 2;
    
    public static final int LG_PHONE_SCUESS = 3;
    
    public static final int LOGIN_EASE_OK = 4;

    
    public static final int EM_NETWORK_ERROR = 5;
    
    public static final int EM_NETWORK_LINK_ERROR = 6;

    
    public static final int LOGIN_EASE_INVALID = 7;
    public static final int EM_FORWARD_SUCCESS = 8;

    public static final int EM_GROUP_CREATE_INVITATE = 9;
    public static final int UPDATE_GROUP_NOTICES = 10;
    public static final int UPDATE_GROUP_NOTICES_DEL = 11;
    public static final int UPDATE_GROUP_MANAGERS = 12;
    public static final int UPDATE_GROUP_OWNER = 13;
    public static final int UPDATE_GROUP_NICKENAME = 14;
    public static final int UPDATE_GROUP_NAME = 15;

    
    public static final int ALI_LOGIN_SUCCESS = 1001;
    public static final int UPDATE_MSG_COUNT = 105;
    public static final int PING_FAIL = 10086;
    
    public static final int TEST_NET_STATUS = 10087;

    public int mType;

    @IntDef({TEST_NET_STATUS,PING_FAIL, UPDATE_GROUP_NAME, UPDATE_GROUP_NICKENAME, UPDATE_GROUP_OWNER, ALI_LOGIN_SUCCESS, EM_GROUP_CREATE_INVITATE, REDPACKET_LOST,
            EM_FORWARD_SUCCESS, LG_LOGIN_SCUESS, LG_REST_SCUESS, LG_PHONE_SCUESS, LOGIN_EASE_INVALID, LOGIN_EASE_OK, EM_NETWORK_ERROR,
            EM_NETWORK_LINK_ERROR, UPDATE_GROUP_NOTICES, UPDATE_GROUP_NOTICES_DEL, UPDATE_GROUP_MANAGERS, UPDATE_MSG_COUNT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NoticeType {

    }

    public Notice(@NoticeType int type) {
        mType = type;
    }


}
