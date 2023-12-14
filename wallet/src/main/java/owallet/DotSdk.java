

package owallet;

import java.util.Arrays;


public class DotSdk {
    private final int refnum;

    public final int incRefnum() {
        return this.refnum;
    }

    public DotSdk(String var1, long var2) {
        this.refnum = __NewDotSdk(var1, var2);
    }

    private static native int __NewDotSdk(String var0, long var1);

    DotSdk(int var1) {
        this.refnum = var1;
    }

    public final native Account getAccount();

    public final native void setAccount(Account var1);

    public final native Transfer getTransfer();

    public final native void setTransfer(Transfer var1);

    public boolean equals(Object var1) {
        if (var1 != null && var1 instanceof DotSdk) {
            DotSdk var2 = (DotSdk)var1;
            Account var3 = this.getAccount();
            Account var4 = var2.getAccount();
            if (var3 == null) {
                if (var4 != null) {
                    return false;
                }
            } else if (!var3.equals(var4)) {
                return false;
            }

            Transfer var5 = this.getTransfer();
            Transfer var6 = var2.getTransfer();
            if (var5 == null) {
                if (var6 != null) {
                    return false;
                }
            } else if (!var5.equals(var6)) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.getAccount(), this.getTransfer()});
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("DotSdk").append("{");
        var1.append("Account:").append(this.getAccount()).append(",");
        var1.append("Transfer:").append(this.getTransfer()).append(",");
        return var1.append("}").toString();
    }
}
