

package common.app.wallet;

import android.content.Context;

import java.util.Map;



public interface WalletBridge {
    int getWalletSize(Context context);
    Map<String,String> getGroupPayParams(Context context,Map<String,String> map,String pwd,String authType);
    String getJsonRpc(int type);
}
