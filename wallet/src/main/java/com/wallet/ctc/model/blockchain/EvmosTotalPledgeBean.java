

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class EvmosTotalPledgeBean extends EvmosHttpBean{
    public Data data;

    public static class Data {
        public EvmosAmountsBean pre_pledge_amount;
        public EvmosAmountsBean all_pledge_amount;
        public EvmosAmountsBean remain_pledge_amount;

        
        public EvmosAmountsBean all_can_withdraw;

        public List<Delegation> delegations;
    }

    
    public static class Delegation {
        public DelegattionValue delegation;
        public EvmosAmountsBean balance;

    }

    public static class DelegattionValue {
        public String delegator_address;
        public String validator_address;
        public String validator_name;
    }
}
