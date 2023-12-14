package com.app.home.pojo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.app.R;
import com.app.home.ui.utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class VoteDetial {
    
    @SerializedName(value = "proposer", alternate = {"from"})
    public String proposer;
    
    @SerializedName(value = "deposit_amount", alternate = {"deposit"})
    public DepositAmount deposit_amount;

    public static class DepositAmount implements Parcelable {
        
        private String amount;

        public String getAmount(int decimal) {
            try {
                return new BigDecimal(amount).divide(BigDecimal.valueOf(Math.pow(10, decimal)), 6, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return amount;
        }

        
        public String denom;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.amount);
            dest.writeString(this.denom);
        }

        public void readFromParcel(Parcel source) {
            this.amount = source.readString();
            this.denom = source.readString();
        }

        public DepositAmount() {
        }

        protected DepositAmount(Parcel in) {
            this.amount = in.readString();
            this.denom = in.readString();
        }

        public static final Creator<DepositAmount> CREATOR = new Creator<DepositAmount>() {
            @Override
            public DepositAmount createFromParcel(Parcel source) {
                return new DepositAmount(source);
            }

            @Override
            public DepositAmount[] newArray(int size) {
                return new DepositAmount[size];
            }
        };
    }

    
    public List<Deposits> deposits;

    public class Deposits {
        
        public String proposal_id;
        
        public String depositor;
        public List<DepositAmount> amount;
    }

    public Tally tally;

    public static class Tally {
        
        
        
        

        
        
        
        

        @SerializedName(value = "yes", alternate = {"yes_count"})
        public String yes;
        @SerializedName(value = "abstain", alternate = {"abstain_count"})
        public String abstain;
        @SerializedName(value = "no", alternate = {"no_count"})
        public String no;
        @SerializedName(value = "no_with_veto", alternate = {"no_with_veto_count"})
        public String no_with_veto;
    }

    @SerializedName(value = "proposal_detail", alternate = {"detail"})
    public String proposal_detail;
    
    private ProposalDetail proposalContent;

    Gson mGson = new Gson();

    public ProposalDetail getProposalContent() {
        if (null == proposalContent) {
            
            proposalContent = mGson.fromJson(proposal_detail, ProposalDetail.class);
        }
        return proposalContent;
    }

    public class ProposalDetail {

        
        private List<ProposalDetailResultContent> messages;

        public ProposalDetailResultContent getMessages() {
            if (null != messages && messages.size() > 0) {
                return messages.get(0);
            }
            return null;
        }

        
        public int id;
        
        public String status;
        
        public String voting_start_time;
        
        public String voting_end_time;
        
        public String submit_time;

    }


    public class ProposalDetailResultContent {

        private String type;

        public String getRealType() {
            return type;
        }

        public String getType() {
            if (TextUtils.equals(type, "cosmos-sdk/v1/MsgExecLegacyContent")) {
                if (null != value.content) {
                    if (TextUtils.equals(value.content.type, "cosmos-sdk/CommunityPoolSpendProposal")) {
                        return "CommunityPoolSpendProposal";
                    } else if (TextUtils.equals(value.content.type, "cosmos-sdk/ParameterChangeProposal")) {
                        
                        return "ParameterChangeProposal";
                    } else if (TextUtils.equals(value.content.type, "cosmos-sdk/SoftwareUpgradeProposal")) {
                        
                        return "SoftwareUpgradeProposal";
                    }
                }
            }
            return type;
        }

        public Value value;

        private List<IUpdateInfo> mInfos;

        public List<IUpdateInfo> getInfos() {
            if (null == mInfos) {
                mInfos = new ArrayList<>();
            }
            if (TextUtils.equals(type, "cosmos-sdk/v1/MsgExecLegacyContent")) {


                if (null != value.content) {

                    if (TextUtils.equals(value.content.type, "cosmos-sdk/CommunityPoolSpendProposal")) {
                        
                        mInfos.add(new Community() {

                            @Override
                            public String getKey() {
                                return value.content.value.recipient;
                            }

                            @Override
                            public String getValue() {
                                if (null == value.content.value.amount || value.content.value.amount.size() == 0) {
                                    return "--";
                                }
                                return value.content.value.amount.get(0).getAmount(18);
                            }
                        });
                    } else if (TextUtils.equals(value.content.type, "cosmos-sdk/ParameterChangeProposal")) {
                        
                        if (null != value.content.value.changes) {
                            mInfos.addAll(value.content.value.changes);
                        }
                    } else if (TextUtils.equals(type, "cosmos-sdk/SoftwareUpgradeProposal")) {
                        
                        if (null != value.plan) {
                            mInfos.add(value.plan);
                        }
                    }
                }
            }
            return mInfos;
        }
    }

    public static class Value2 {
        public String title = "";
        public String description = "";

        
        
        public String recipient;
        
        public List<DepositAmount> amount;

        private List<Changes> changes;
        private List<Delegate> delegate;


    }

    public static class ValueContent {

        private String type;
        private Value2 value;

    }

    public static class Value {
        public String getTitle(String type) {


            if (null != content && null != content.value) {
                return content.value.title;
            }
            return "";
        }

        public String getDescription(String type) {

            if (null != content && null != content.value) {
                return content.value.description;
            }
            return "";
        }

        private ValueContent content;
        private Plan plan;

    }

    public class Community implements IUpdateInfo {

        @Override
        public String getName() {
            return "";
        }

        @Override
        public String getKeyName(Context context) {
            return context.getString(R.string.community_key_name);
        }

        @Override
        public String getKey() {
            return null;
        }

        @Override
        public String getValueName(Context context) {
            return context.getString(R.string.community_value_name);
        }

        @Override
        public String getValue() {
            return null;
        }
    }

    
    public class Changes implements IUpdateInfo {
        
        public String subspace;
        
        public String key;
        
        public String value;

        @Override
        public String getName() {
            return subspace;
        }

        @Override
        public String getKeyName(Context context) {
            return context.getString(R.string.changes_key_name);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValueName(Context context) {
            return context.getString(R.string.changes_value_name);
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    
    public class Delegate implements IUpdateInfo {
        
        public String address;
        
        public String gateway_address;
        
        public String amount;

        @Override
        public String getName() {
            return address;
        }

        @Override
        public String getKeyName(Context context) {
            return context.getString(R.string.delegate_key_name);
        }

        @Override
        public String getKey() {
            return gateway_address;
        }

        @Override
        public String getValueName(Context context) {
            return context.getString(R.string.delegate_value_name);
        }

        @Override
        public String getValue() {
            return amount;
        }
    }

    
    public class Plan implements IUpdateInfo {
        
        public String name;
        
        public String time;
        
        public String height;
        
        public String info;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getKeyName(Context context) {
            if (!TextUtils.isEmpty(time) && !TextUtils.equals(time, "0001-01-01T00:00:00Z")) {
                return context.getString(R.string.plan_key_time);
            } else {
                return context.getString(R.string.plan_key_height);
            }

        }

        @Override
        public String getKey() {
            if (!TextUtils.isEmpty(time) && !TextUtils.equals(time, "0001-01-01T00:00:00Z")) {
                return TimeUtils.format3(time);
            } else {
                return height;
            }
        }

        @Override
        public String getValueName(Context context) {
            return context.getString(R.string.plan_value_name);
        }

        @Override
        public String getValue() {
            return info;
        }
    }
}
