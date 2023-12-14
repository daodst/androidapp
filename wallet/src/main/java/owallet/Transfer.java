

package owallet;

import java.util.Arrays;


public class Transfer {
    private final int refnum;

    public final int incRefnum() {
        return this.refnum;
    }

    Transfer(int var1) {
        this.refnum = var1;
    }

    public Transfer() {
        this.refnum = __New();
    }

    private static native int __New();

    public native SignOut sign(String var1, String var2, String var3, long var4) throws Exception;

    public native String submit(String var1) throws Exception;

    public boolean equals(Object var1) {
        if (var1 != null && var1 instanceof Transfer) {
            Transfer var2 = (Transfer)var1;
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[0]);
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("Transfer").append("{");
        return var1.append("}").toString();
    }
}
