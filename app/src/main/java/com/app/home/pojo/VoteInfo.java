package com.app.home.pojo;

import android.content.Context;
import android.text.TextUtils;

import com.app.R;

public class VoteInfo {

    
    public String proposal_id;
    
    private String status;

    public boolean isVoting() {
        return TextUtils.equals(status, "PROPOSAL_STATUS_VOTING_PERIOD");
    }

    public String getStatus(Context context) {
        if (TextUtils.equals(status, "PROPOSAL_STATUS_UNSPECIFIED")) {
            return context.getString(R.string.proposal_status_unspecified);
        } else if (TextUtils.equals(status, "PROPOSAL_STATUS_DEPOSIT_PERIOD")) {
            return context.getString(R.string.proposal_status_deposit_period);
        } else if (TextUtils.equals(status, "PROPOSAL_STATUS_VOTING_PERIOD")) {
            return context.getString(R.string.proposal_status_voting_period);
        } else if (TextUtils.equals(status, "PROPOSAL_STATUS_PASSED")) {
            return context.getString(R.string.proposal_status_passed);
        } else if (TextUtils.equals(status, "PROPOSAL_STATUS_REJECTED")) {
            return context.getString(R.string.proposal_status_rejected);
        } else if (TextUtils.equals(status, "PROPOSAL_STATUS_FAILED")) {
            return context.getString(R.string.proposal_status_failed);
        }
        return "";
    }

    public Boolean isSuccess() {
        return TextUtils.equals(status, "PROPOSAL_STATUS_PASSED");
    }

    
    public String voting_start_time;
    
    public String voting_end_time;
    
    private Content content;

    public String getContent() {
        if (null != content && !TextUtils.isEmpty(content.title)) {
            return content.title;
        }
        return "";
    }


    public static class Content {

        public String title;
    }
}




