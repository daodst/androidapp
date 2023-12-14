package com.app.chat_engine.notice;

import com.app.pojo.ChatCStateBean;


public class InvitedCreateGroupNotice extends BaseChatNotice{

    private static final String GO_CHAT_KEY = "goChat";
    public InvitedCreateGroupNotice() {
        super("invitedCreateGroup");
    }

    @Override
    ChatCStateBean doCheckState() {
        return null;
    }
}
