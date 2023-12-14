

package owallet;

import java.util.Arrays;


public class MnemonicOut {
    private final int refnum;

    public final int incRefnum() {
        return this.refnum;
    }

    MnemonicOut(int var1) {
        this.refnum = var1;
    }

    public MnemonicOut() {
        this.refnum = __New();
    }

    private static native int __New();

    public final native String getAddress();

    public final native void setAddress(String var1);

    public final native String getMnemonic();

    public final native void setMnemonic(String var1);

    public final native String getPrivateKey();

    public final native void setPrivateKey(String var1);

    public final native String getPublicKey();

    public final native void setPublicKey(String var1);

    public boolean equals(Object var1) {
        if (var1 != null && var1 instanceof MnemonicOut) {
            MnemonicOut var2 = (MnemonicOut)var1;
            String var3 = this.getAddress();
            String var4 = var2.getAddress();
            if (var3 == null) {
                if (var4 != null) {
                    return false;
                }
            } else if (!var3.equals(var4)) {
                return false;
            }

            String var5 = this.getMnemonic();
            String var6 = var2.getMnemonic();
            if (var5 == null) {
                if (var6 != null) {
                    return false;
                }
            } else if (!var5.equals(var6)) {
                return false;
            }

            String var7 = this.getPrivateKey();
            String var8 = var2.getPrivateKey();
            if (var7 == null) {
                if (var8 != null) {
                    return false;
                }
            } else if (!var7.equals(var8)) {
                return false;
            }

            String var9 = this.getPublicKey();
            String var10 = var2.getPublicKey();
            if (var9 == null) {
                if (var10 != null) {
                    return false;
                }
            } else if (!var9.equals(var10)) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.getAddress(), this.getMnemonic(), this.getPrivateKey(), this.getPublicKey()});
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("MnemonicOut").append("{");
        var1.append("Address:").append(this.getAddress()).append(",");
        var1.append("Mnemonic:").append(this.getMnemonic()).append(",");
        var1.append("PrivateKey:").append(this.getPrivateKey()).append(",");
        var1.append("PublicKey:").append(this.getPublicKey()).append(",");
        return var1.append("}").toString();
    }
}
