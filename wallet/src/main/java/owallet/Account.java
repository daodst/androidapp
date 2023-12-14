

package owallet;

import java.util.Arrays;


public class Account {
    private final int refnum;

    public final int incRefnum() {
        return this.refnum;
    }

    Account(int var1) {
        this.refnum = var1;
    }

    public Account() {
        this.refnum = __New();
    }

    private static native int __New();

    public final native long getNetwork();

    public final native void setNetwork(long var1);

    public native MnemonicOut generateByMnemonic(String var1) throws Exception;

    public native PrivateOut generateByPriv(String var1) throws Exception;

    public native AccountInfo getAccountInfo(String var1) throws Exception;

    public boolean equals(Object var1) {
        if (var1 != null && var1 instanceof Account) {
            Account var2 = (Account)var1;
            long var3 = this.getNetwork();
            long var5 = var2.getNetwork();
            return var3 == var5;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.getNetwork()});
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("Account").append("{");
        var1.append("Network:").append(this.getNetwork()).append(",");
        return var1.append("}").toString();
    }

}
