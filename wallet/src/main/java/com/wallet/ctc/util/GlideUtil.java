

package com.wallet.ctc.util;

import android.content.Context;
import android.widget.ImageView;

import com.wallet.ctc.R;




public class GlideUtil extends common.app.utils.GlideUtil {

    
    public static void showFindImg(Context context, String url, ImageView imageView){
        showImg(context, url, imageView, R.mipmap.find_zhanwei, null);
    }
    
    public static void showDmImg(Context context, String url, ImageView imageView){
        showImg(context, url, imageView, R.mipmap.dm_logo, null);
    }

    public static void showMccImg(Context context, String url, ImageView imageView){
        showImg(context, url, imageView, R.mipmap.tt_logo, null);
    }

    public static void showOtherImg(Context context, String url, ImageView imageView){
        showImg(context, url, imageView, R.mipmap.llq_other, null);
    }

    public static void showEthImg(Context context, String url, ImageView imageView){
        showImg(context, url, imageView, R.mipmap.eth_logo, null);
    }

}
