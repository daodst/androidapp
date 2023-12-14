

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public class TrxAssetBean {


    

    private OwnerPermissionBean owner_permission;
    private AccountResourceBean account_resource;
    private String address;
    private BigDecimal balance;
    private long create_time;
    private List<ActivePermissionBean> active_permission;
    private List<Map<String,String>> trc20;

    public OwnerPermissionBean getOwner_permission() {
        return owner_permission;
    }

    public void setOwner_permission(OwnerPermissionBean owner_permission) {
        this.owner_permission = owner_permission;
    }

    public AccountResourceBean getAccount_resource() {
        return account_resource;
    }

    public void setAccount_resource(AccountResourceBean account_resource) {
        this.account_resource = account_resource;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getBalance() {
        if(null==balance){
            balance=new BigDecimal("0");
        }
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public List<ActivePermissionBean> getActive_permission() {
        return active_permission;
    }

    public void setActive_permission(List<ActivePermissionBean> active_permission) {
        this.active_permission = active_permission;
    }

    public List<Map<String, String>> getTrc20() {
        return trc20;
    }

    public void setTrc20(List<Map<String, String>> trc20) {
        this.trc20 = trc20;
    }

    public static class OwnerPermissionBean {
        

        private int threshold;
        private String permission_name;
        private List<KeysBean> keys;

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public String getPermission_name() {
            return permission_name;
        }

        public void setPermission_name(String permission_name) {
            this.permission_name = permission_name;
        }

        public List<KeysBean> getKeys() {
            return keys;
        }

        public void setKeys(List<KeysBean> keys) {
            this.keys = keys;
        }

        public static class KeysBean {
            

            private String address;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }
    }

    public static class AccountResourceBean {
    }

    public static class ActivePermissionBean {
        

        private String operations;
        private int threshold;
        private int id;
        private String type;
        private String permission_name;
        private List<KeysBeanX> keys;

        public String getOperations() {
            return operations;
        }

        public void setOperations(String operations) {
            this.operations = operations;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPermission_name() {
            return permission_name;
        }

        public void setPermission_name(String permission_name) {
            this.permission_name = permission_name;
        }

        public List<KeysBeanX> getKeys() {
            return keys;
        }

        public void setKeys(List<KeysBeanX> keys) {
            this.keys = keys;
        }

        public static class KeysBeanX {
            

            private String address;
            private int weight;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public int getWeight() {
                return weight;
            }

            public void setWeight(int weight) {
                this.weight = weight;
            }
        }
    }

    @Override
    public String toString() {
        return "TrxAssetBean{" +
                "owner_permission=" + owner_permission +
                ", account_resource=" + account_resource +
                ", address='" + address + '\'' +
                ", balance=" + balance +
                ", create_time=" + create_time +
                ", active_permission=" + active_permission +
                ", trc20=" + trc20 +
                '}';
    }
}
