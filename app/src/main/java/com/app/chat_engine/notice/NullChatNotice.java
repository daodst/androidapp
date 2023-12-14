package com.app.chat_engine.notice;

import com.app.chat_engine.IChatNotify;
import com.app.pojo.ChatCStateBean;


public class NullChatNotice implements IChatNotify {
    @Override
    public ChatCStateBean checkState() {
        return null;
    }

    @Override
    public int process() {
        return 0;
    }
}
