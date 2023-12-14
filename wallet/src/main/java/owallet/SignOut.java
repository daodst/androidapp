

package owallet;

import java.util.Arrays;


public class SignOut {
    SignOut(int var1) {
    }

    public SignOut() {
    }

    private static native int __New();

    public final native String getSign();

    public final native void setSign(String var1);

    public final native String getFee();

    public final native void setFee(String var1);

    public boolean equals(Object var1) {
        if (var1 != null && var1 instanceof SignOut) {
            SignOut var2 = (SignOut)var1;
            String var3 = this.getSign();
            String var4 = var2.getSign();
            if (var3 == null) {
                if (var4 != null) {
                    return false;
                }
            } else if (!var3.equals(var4)) {
                return false;
            }

            String var5 = this.getFee();
            String var6 = var2.getFee();
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
        return Arrays.hashCode(new Object[]{this.getSign(), this.getFee()});
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("SignOut").append("{");
        var1.append("Sign:").append(this.getSign()).append(",");
        var1.append("Fee:").append(this.getFee()).append(",");
        return var1.append("}").toString();
    }
}
