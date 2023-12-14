package com.app.chat_engine.notice;

import com.app.pojo.ChatCStateBean;


public class BuyDvmNotice extends BaseChatNotice {

    public static final String EVENT_BUY_DVM = "buyDvmEvent";
    public static final int FLAG_FIRST = 1;
    public static final int FLAG_CONTINUE = 2;

    public BuyDvmNotice() {
        super("BuyDvm");
    }

    @Override
    ChatCStateBean doCheckState() {

        
        
        return null;
    }


}
