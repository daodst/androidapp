

package owallet;

import java.util.Arrays;


public class AccountInfo {
    private final int refnum;

    public final int incRefnum() {
        return this.refnum;
    }

    AccountInfo(int var1) {
        this.refnum = var1;
    }

    public AccountInfo() {
        this.refnum = __New();
    }

    private static native int __New();

    public final native long getNonce();

    public final native void setNonce(long var1);

    public final native long getConsumers();

    public final native void setConsumers(long var1);

    public final native long getProviders();

    public final native void setProviders(long var1);

    public final native String getFree();

    public final native void setFree(String var1);

    public final native String getReserved();

    public final native void setReserved(String var1);

    public final native String getMiscFrozen();

    public final native void setMiscFrozen(String var1);

    public final native String getFreeFrozen();

    public final native void setFreeFrozen(String var1);

    public boolean equals(Object var1) {
        if (var1 != null && var1 instanceof AccountInfo) {
            AccountInfo var2 = (AccountInfo)var1;
            long var3 = this.getNonce();
            long var5 = var2.getNonce();
            if (var3 != var5) {
                return false;
            } else {
                long var7 = this.getConsumers();
                long var9 = var2.getConsumers();
                if (var7 != var9) {
                    return false;
                } else {
                    long var11 = this.getProviders();
                    long var13 = var2.getProviders();
                    if (var11 != var13) {
                        return false;
                    } else {
                        String var15 = this.getFree();
                        String var16 = var2.getFree();
                        if (var15 == null) {
                            if (var16 != null) {
                                return false;
                            }
                        } else if (!var15.equals(var16)) {
                            return false;
                        }

                        String var17 = this.getReserved();
                        String var18 = var2.getReserved();
                        if (var17 == null) {
                            if (var18 != null) {
                                return false;
                            }
                        } else if (!var17.equals(var18)) {
                            return false;
                        }

                        String var19 = this.getMiscFrozen();
                        String var20 = var2.getMiscFrozen();
                        if (var19 == null) {
                            if (var20 != null) {
                                return false;
                            }
                        } else if (!var19.equals(var20)) {
                            return false;
                        }

                        String var21 = this.getFreeFrozen();
                        String var22 = var2.getFreeFrozen();
                        if (var21 == null) {
                            if (var22 != null) {
                                return false;
                            }
                        } else if (!var21.equals(var22)) {
                            return false;
                        }

                        return true;
                    }
                }
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.getNonce(), this.getConsumers(), this.getProviders(), this.getFree(), this.getReserved(), this.getMiscFrozen(), this.getFreeFrozen()});
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("AccountInfo").append("{");
        var1.append("Nonce:").append(this.getNonce()).append(",");
        var1.append("Consumers:").append(this.getConsumers()).append(",");
        var1.append("Providers:").append(this.getProviders()).append(",");
        var1.append("Free:").append(this.getFree()).append(",");
        var1.append("Reserved:").append(this.getReserved()).append(",");
        var1.append("MiscFrozen:").append(this.getMiscFrozen()).append(",");
        var1.append("FreeFrozen:").append(this.getFreeFrozen()).append(",");
        return var1.append("}").toString();
    }
}
