

package owallet;

import java.util.Arrays;


public class PrivateOut {
    private final int refnum;

    public final int incRefnum() {
        return this.refnum;
    }

    PrivateOut(int var1) {
        this.refnum = var1;
    }

    public PrivateOut() {
        this.refnum = __New();
    }

    private static native int __New();

    public final native String getAddress();

    public final native void setAddress(String var1);

    public final native String getPrivateKey();

    public final native void setPrivateKey(String var1);

    public final native String getPublicKey();

    public final native void setPublicKey(String var1);

    public boolean equals(Object var1) {
        if (var1 != null && var1 instanceof PrivateOut) {
            PrivateOut var2 = (PrivateOut)var1;
            String var3 = this.getAddress();
            String var4 = var2.getAddress();
            if (var3 == null) {
                if (var4 != null) {
                    return false;
                }
            } else if (!var3.equals(var4)) {
                return false;
            }

            String var5 = this.getPrivateKey();
            String var6 = var2.getPrivateKey();
            if (var5 == null) {
                if (var6 != null) {
                    return false;
                }
            } else if (!var5.equals(var6)) {
                return false;
            }

            String var7 = this.getPublicKey();
            String var8 = var2.getPublicKey();
            if (var7 == null) {
                if (var8 != null) {
                    return false;
                }
            } else if (!var7.equals(var8)) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.getAddress(), this.getPrivateKey(), this.getPublicKey()});
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("PrivateOut").append("{");
        var1.append("Address:").append(this.getAddress()).append(",");
        var1.append("PrivateKey:").append(this.getPrivateKey()).append(",");
        var1.append("PublicKey:").append(this.getPublicKey()).append(",");
        return var1.append("}").toString();
    }
}
