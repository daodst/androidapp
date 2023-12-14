package com.app.home.ui.vote.create.params;

import android.content.Context;

import com.app.R;
import com.app.pojo.VoteParamsInfoBean;

import java.util.ArrayList;
import java.util.List;

import common.app.AppApplication;
import common.app.pojo.NameIdBean;


public class VoteParamsData {

    
    public static List<NameIdBean> getDatas() {
        List<NameIdBean> datas = new ArrayList<>();
        datas.add(new NameIdBean("mint", getString(R.string.vote_params_minting), ""));
        datas.add(new NameIdBean("staking", getString(R.string.vote_params_staking), ""));
        datas.add(new NameIdBean("chat", getString(R.string.vote_params_chat), ""));
        datas.add(new NameIdBean("gateway", getString(R.string.vote_params_gateway), ""));
        datas.add(new NameIdBean("slashing", getString(R.string.vote_params_slashing), ""));
        return datas;
    }

    public static List<VoteParamsInfoBean> getKeys(final String subSpace) {
        switch (subSpace) {
            case "mint" :
                return getMintingKeys(subSpace);
            case "staking" :
                return getStakingKeys(subSpace);
            case "chat" :
                return getChatKeys(subSpace);
            case "gateway" :
                return getGatewayKeys(subSpace);
            case "pledge" :
                return getPledgeKeys(subSpace);
            case "slashing" :
                return getslashingKeys(subSpace);
            default:
                return new ArrayList<>();
        }
    }

    public static String getString(int strRes) {
        Context context = AppApplication.getContext();
        if (null == context || strRes == 0) {
            return "";
        }
        return context.getString(strRes);
    }

    
    private static List<VoteParamsInfoBean> getMintingKeys(String subSpace) {
        List<VoteParamsInfoBean> list = new ArrayList<>();
        list.add(new VoteParamsInfoBean(subSpace, "InflationRateChange", "inflation_rate_change", getString(R.string.minting_inflation_rate_change), getString(R.string.minting_inflation_rate_change_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "InflationMax", "inflation_max", getString(R.string.minting_inflation_max), getString(R.string.minting_inflation_max_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "InflationMin", "inflation_min", getString(R.string.minting_inflation_min), getString(R.string.minting_inflation_min_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "GoalBonded", "goal_bonded", getString(R.string.minting_goal_bonded), getString(R.string.minting_goal_bonded_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "BlocksPerYear", "blocks_per_year", getString(R.string.minting_blocks_per_year), getString(R.string.minting_blocks_per_year_limit)));
        return list;
    }


    
    private static List<VoteParamsInfoBean> getStakingKeys(String subSpace) {
        List<VoteParamsInfoBean> list = new ArrayList<>();
        list.add(new VoteParamsInfoBean(subSpace, "UnbondingTime", "unbonding_time", getString(R.string.staking_unbonding_time), getString(R.string.staking_unbonding_time_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "MaxValidators", "max_validators", getString(R.string.staking_max_validators), getString(R.string.staking_max_validators_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "MaxEntries", "max_entries", getString(R.string.staking_max_entries), getString(R.string.staking_max_entries_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "HistoricalEntries", "historical_entries", getString(R.string.staking_historical_entries), getString(R.string.staking_historical_entries_limit)));
        return list;
    }

    
    private static List<VoteParamsInfoBean> getChatKeys(String subSpace) {
        List<VoteParamsInfoBean> list = new ArrayList<>();
        list.add(new VoteParamsInfoBean(subSpace, "MaxPhoneNumber", "maxPhoneNumber", getString(R.string.chat_max_phone_number), getString(R.string.chat_max_phone_number_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "DestroyPhoneNumberCoin", "destroyPhoneNumberCoin", getString(R.string.chat_destroy_phone_number_coin), getString(R.string.chat_destroy_phone_number_coin_limit), true));
        list.add(new VoteParamsInfoBean(subSpace, "ChatFee", "chatFee", getString(R.string.chat_chat_fee), getString(R.string.chat_chat_fee_limit), true));
        list.add(new VoteParamsInfoBean(subSpace, "MinRegisterBurnAmount", "min_register_burn_amount", getString(R.string.chat_min_register_burn_amount), getString(R.string.chat_min_register_burn_amount_limit), true));
        return list;
    }


    
    private static List<VoteParamsInfoBean> getGatewayKeys(String subSpace) {
        List<VoteParamsInfoBean> list = new ArrayList<>();
        list.add(new VoteParamsInfoBean(subSpace, "IndexNumHeight", "index_num_height", getString(R.string.gateway_index_num_height), getString(R.string.gateway_index_num_height_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "RedeemFeeHeight", "redeem_fee_height", getString(R.string.gateway_redeem_fee_height), getString(R.string.gateway_redeem_fee_height_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "RedeemFee", "redeem_fee", getString(R.string.gateway_redeem_fee), getString(R.string.gateway_redeem_fee_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "MinDelegate", "min_delegate", getString(R.string.gateway_min_delegate), getString(R.string.gateway_min_delegate_limit), true));
        list.add(new VoteParamsInfoBean(subSpace, "Validity", "validity", getString(R.string.gateway_validity), getString(R.string.gateway_validity_limit)));
        return list;
    }


    
    private static List<VoteParamsInfoBean> getPledgeKeys(String subSpace) {
        List<VoteParamsInfoBean> list = new ArrayList<>();
        list.add(new VoteParamsInfoBean(subSpace, "InflationDays", "inflation_days", getString(R.string.pledge_inflation_days), getString(R.string.pledge_inflation_days_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "InflationMax", "inflation_max", getString(R.string.pledge_inflation_max), getString(R.string.pledge_inflation_max_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "InflationMin", "inflation_min", getString(R.string.pledge_inflation_min), getString(R.string.pledge_inflation_min_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "GoalBonded", "goal_bonded", getString(R.string.pledge_goal_bonded), getString(R.string.pledge_goal_bonded_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "BlocksPerYear", "blocks_per_year", getString(R.string.pledge_blocks_per_year), getString(R.string.pledge_blocks_per_year_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "UnbondingHeight", "unbonding_height", getString(R.string.pledge_unbonding_height), getString(R.string.pledge_unbonding_height_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "MinBurnCoin", "minBurnCoin", getString(R.string.pledge_min_burn_coin), getString(R.string.pledge_min_burn_coin_limit), true));
        list.add(new VoteParamsInfoBean(subSpace, "BurnCurrentGatePercent", "burn_current_gate_percent", getString(R.string.pledge_burn_current_gate_percent), getString(R.string.pledge_burn_current_gate_percent_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "BurnRegisterGatePercent", "burn_register_gate_percent", getString(R.string.pledge_burn_register_gate_percent), getString(R.string.pledge_burn_register_gate_percent_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "BurnDposPercent", "burn_dpos_percent", getString(R.string.pledge_burn_dpos_percent), getString(R.string.pledge_burn_dpos_percent_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "BurnReturnDays", "burn_return_days", getString(R.string.pledge_burn_return_days2), getString(R.string.pledge_burn_return_days_limit)));
        return list;
    }


    
    private static List<VoteParamsInfoBean> getslashingKeys(String subSpace) {
        List<VoteParamsInfoBean> list = new ArrayList<>();
        list.add(new VoteParamsInfoBean(subSpace, "SignedBlocksWindow", "signed_blocks_window", getString(R.string.slashing_signed_blocks_window), getString(R.string.slashing_signed_blocks_window_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "MinSignedPerWindow", "min_signed_per_window", getString(R.string.slashing_min_signed_per_window), getString(R.string.slashing_min_signed_per_window_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "DowntimeJailDuration", "downtime_jail_duration", getString(R.string.slashing_downtime_jail_duration), getString(R.string.slashing_downtime_jail_duration_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "SlashFractionDoubleSign", "slash_fraction_double_sign", getString(R.string.slashing_slash_fraction_double_sign), getString(R.string.slashing_slash_fraction_double_sign_limit)));
        list.add(new VoteParamsInfoBean(subSpace, "SlashFractionDowntime", "slash_fraction_downtime", getString(R.string.slashing_slash_fraction_downtime), getString(R.string.slashing_slash_fraction_downtime_limit)));
        return list;
    }



}
