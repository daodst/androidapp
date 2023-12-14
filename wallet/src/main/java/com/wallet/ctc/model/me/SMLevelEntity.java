

package com.wallet.ctc.model.me;


import android.text.TextUtils;


public class SMLevelEntity {
    
    public String facc;
    
    public String all_pledge;
    
    public String servername;

    
    public String ranking;
    
    
    public int pledge_level;


    


    @Override
    public String toString() {
        return "SMLevelEntity{" +
                "facc='" + facc + '\'' +
                ", all_pledge='" + all_pledge + '\'' +
                ", servername='" + servername + '\'' +
                ", ranking='" + ranking + '\'' +
                ", pledge_level=" + pledge_level +
                ", userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", idi='" + idi + '\'' +
                '}';
    }

    public String userId;
    public String displayName;
    public String avatarUrl;
    public String idi;

    public String getMiniAddr() {
        if (!TextUtils.isEmpty(facc) && facc.length() > 7) {
            String minAddr = facc.substring(0,3)+"..."+facc.substring(facc.length()-3);
            return minAddr;
        }
        return facc;
    }


    public SMLevelEntity() {
    }

}
