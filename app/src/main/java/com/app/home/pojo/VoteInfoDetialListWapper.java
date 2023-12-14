package com.app.home.pojo;

import android.content.Context;
import android.text.TextUtils;

import com.app.R;

import java.util.List;


public class VoteInfoDetialListWapper {
    public int page;
    public boolean isEnd = false;
    public List<VoteInfoDetialList> result;


    public class VoteInfoDetialList {
        
        public String proposal_id;
        
        public String voter;
        
        public List<Options> options;
    }

    
    
    public class Options {
        
        
        public String option;


        
        public String getOption(Context context) {
            if (TextUtils.equals("1", option)) {
                return context.getString(R.string.vote_detial_approve);
            }
            if (TextUtils.equals("2", option)) {
                return context.getString(R.string.vote_detial_give_up);
            }
            if (TextUtils.equals("3", option)) {
                return context.getString(R.string.vote_detial_against);
            }
            if (TextUtils.equals("4", option)) {
                return context.getString(R.string.vote_detial_disapprove);
            }
            return "";
        }

        public String weight;
    }
}
