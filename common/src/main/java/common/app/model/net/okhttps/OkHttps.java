

package common.app.model.net.okhttps;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import common.app.R;
import common.app.base.model.http.HttpMethods;
import common.app.base.model.http.NetworkResponseInterceptor;
import common.app.ui.view.MyProgressDialog;
import common.app.ui.view.ToastView;
import common.app.ui.view.ToastViewError;
import common.app.utils.LogUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class OkHttps extends BaseModel {
    private Context context;
    private OkHttpClient mOkHttpClient;
    private Gson gson;
    private MyProgressDialog mydialog;
    private ToastView toast;
    private ToastViewError toasts;
    private String APPAUTH;
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mydialog.isShowing()) {
                mydialog.dismiss();
            }
            LogUtil.i("zmh",msg.obj + "");
            switch (msg.what) {
                case 0:
                    toast = new ToastView(mContext, (String) msg.obj);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    OkHttps.this.OnMessageResponse(msg.arg1, null);
                    break;
                case 1:
                    
                    
                        OkHttps.this.OnMessageResponse(msg.arg1, (String) msg.obj);
                    

                    break;
                case 2:
                    toasts = new ToastViewError(mContext, "！");
                    toasts.setGravity(Gravity.CENTER, 0, 0);
                    toasts.show();
                    OkHttps.this.OnMessageResponse(msg.arg1, null);
                    break;
                case 3:
                    toasts = new ToastViewError(mContext, "！");
                    toasts.setGravity(Gravity.CENTER, 0, 0);
                    toasts.show();
                    OkHttps.this.OnMessageResponse(msg.arg1, null);
                    break;
            }
        }

    };

    public OkHttps(Context context) {
        super(context);
        this.context = context;
        gson  = new GsonBuilder()
                .disableHtmlEscaping() 
                .create();
        mydialog = new MyProgressDialog(context, context.getResources().getString(R.string.hold_on));
        
        mOkHttpClient = new OkHttpClient.Builder()
                
                .connectTimeout(1000, TimeUnit.MINUTES)
                .readTimeout(1000, TimeUnit.MINUTES)
                .writeTimeout(1000, TimeUnit.MINUTES)
                .addInterceptor(new NetworkResponseInterceptor())
                .build();

    }

    public Gson getGson() {
        return gson;
    }


    
    public String getCanshuPaixu(String[] keys, String[] values) {
        String json = gson.toJson(HttpMethods.getInstance().getRequest(keys,values).map);
        return json;
    }



    
    public void httppost(final String url, String json, final boolean pd, final int id) {
        if (pd) {
            mydialog.show();
        }
        
        Request request = new Request.Builder()
                .url(url)
                .post(getCanshu(json))
                .build();
        Call call = mOkHttpClient.newCall(request);
        
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Message msg = new Message();
                msg.what = 2;
                msg.arg1 = id;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback(response.body().string(), id);
                } else {
                    Message msg = new Message();
                    msg.what = 3;
                    msg.arg1 = id;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }





    
    private RequestBody getCanshu(String json) {

        
        Map<String, Object> map = gson.fromJson(json, Map.class);
        FormBody.Builder builder = new FormBody.Builder();
        if (null != map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {

                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        return builder.build();
    }

    
    public void callback(String data, int id) {
        Log.i("zmh", data);
        JSONObject object = null;
        Message msg = new Message();
        String status = "";
        try {
            
            object = new JSONObject(data);
            status = object.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != object) {
            if ("1".equals(status)) {
                
                msg.what = 1;
                msg.arg1 = id;
                try {
                    msg.obj = object.getJSONArray("data").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        msg.obj = object.getJSONObject("data").toString();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        try {
                            msg.obj = object.getString("data").toString();
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            } else {
                msg.what = 0;
                msg.arg1 = id;
                try {
                    msg.obj = object.getString("info").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            msg.what = 3;
        }
        mHandler.sendMessage(msg);

    }
}
