package com.app.chat_engine.notice;

import android.text.SpannableString;

import com.app.R;
import com.app.pojo.ChatCStateBean;

import java.util.Random;


public class RandomActiveNotice extends BaseChatNotice {

    public RandomActiveNotice() {
        super("RandomActive");
    }

    
    @Override
    ChatCStateBean doCheckState() {
        if (nowDayHasNoticed("")) {
            return null;
        }

        int reachState = isReachNoticeTime("");
        if (reachState == STATE_NO_ACTIVE) {
            
            return null;
        }

        
        SpannableString content = new SpannableString(getString(R.string.cn_need30_mins_active));
        ChatCStateBean stateBean = createSmallNoticeState(content, null, null, null);
        return stateBean;
    }


    
    @Override
    protected void afterProcess(int processResult) {
        super.afterProcess(processResult);
        if (processResult == STATE_ACTIVE) {
            randomUpdateNextShowTime();
        }
    }

    
    private void randomUpdateNextShowTime() {
        
        Random random = new Random();
        int value = random.nextInt(3);
        long delayTime = 0L;
        if (value == 0) {
            
            delayTime = System.currentTimeMillis() + 20*86400000;
        } else if(value == 1) {
            
            delayTime = System.currentTimeMillis() + 25*86400000;
        } else {
            
            delayTime = System.currentTimeMillis() + 30*86400000;
        }

        ioUpdateNextNoticeTime(delayTime, "");
    }




}
