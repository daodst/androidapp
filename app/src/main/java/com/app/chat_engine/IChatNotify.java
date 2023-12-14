package com.app.chat_engine;

import com.app.pojo.ChatCStateBean;


public interface IChatNotify {

    ChatCStateBean checkState();
    int process();

}
