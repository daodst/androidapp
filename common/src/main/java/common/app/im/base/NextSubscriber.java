

package common.app.im.base;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import common.app.Injection;
import common.app.R;
import common.app.base.base.BaseFragmentView;
import common.app.base.model.OtherDeviceLoginCheck;
import common.app.im.exception.DbException;
import common.app.im.exception.RemoteServerException;
import common.app.im.model.base.RequstData;
import common.app.utils.ActivityContainer;
import common.app.utils.LogUtil;
import common.app.utils.NetUtils;
import io.reactivex.observers.DisposableObserver;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.HttpException;
import retrofit2.Response;


public abstract class NextSubscriber<T> extends DisposableObserver<T> {

    private static final String TAG = "NextSubscriber";
    private Context mContext;
    private Activity mActivity;
    private String mTitle;

    private BaseFragmentView mBaseView;

    public NextSubscriber(BaseFragmentView baseView, String title) {
        mTitle = title;
        mBaseView = baseView;
        if (null != mBaseView) {
            mBaseView.setTitle(title);
        }
        init();
    }


    
    private void init() {
        if ((null == ActivityContainer.getInstance().getList() || ActivityContainer.getInstance().getList().size() == 0)) {
            return;
        }
        ArrayList<Activity> list = ActivityContainer.getInstance().getList();
        mActivity = list.get(list.size() - 1);
    }


    public NextSubscriber() {
        init();
    }


    @Override
    protected void onStart() {
        super.onStart();
        
        if (null != mBaseView) {
            mBaseView.showLoading();
        }
    }

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public void onError(Throwable e) {
        if (null != mBaseView) {
            mBaseView.hindeLoading();
        }
        String url = "";
        String param = "";
        String result = "";

        
        try {
            if (e instanceof JsonSyntaxException) {
                String message = e.getMessage();
                if (!TextUtils.isEmpty(message)) {
                    String[] split = message.split(RequstData.REQUST_SPIT);
                    if (split.length == 3) {
                        param = split[0];
                        url = split[1];
                        result = split[2];
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Throwable throwable = e.getCause();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            Response<?> response = httpException.response();
            if (response != null) {
                okhttp3.Response raw = response.raw();
                if (null != raw) {
                    Request request = raw.request();
                    if (null != request) {
                        String method = request.method();
                        HttpUrl httpUrl = request.url();
                        if (null != httpUrl) {
                            url = httpUrl.toString();
                        }
                        if ("GET".equalsIgnoreCase(method)) {

                        } else if ("POST".equalsIgnoreCase(method)) {
                            RequestBody body = request.body();
                            if (null != body) {
                                
                                try {

                                    Buffer buffer = new Buffer();
                                    body.writeTo(buffer);
                                    Charset charset = UTF8;
                                    MediaType contentType = body.contentType();
                                    if (contentType != null) {
                                        charset = contentType.charset(UTF8);
                                    }
                                    if (isPlaintext(buffer)) {
                                        param = buffer.readString(charset);
                                    }
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        LogUtil.i(TAG, code + "==========" + url + "==============" + param);
                        
                    }
                }
            }
        } else if (e instanceof RemoteServerException || throwable instanceof RemoteServerException) {
            
            Log.i(TAG, (e instanceof RemoteServerException) + "======");
            Log.i(TAG, (throwable instanceof RemoteServerException) + "======");
        } else {
            
            
        }

        dealError(e);
    }


    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; 
        }
    }

    @Override
    public void onComplete() {
        if (null != mBaseView) {
            mBaseView.hindeLoading();
        }

    }


    @Override
    public void onNext(T value) {
        try {
            dealData(value);
            onComplete();
        } catch (Exception e) {

            onError(e);
            LogUtil.i(TAG, Log.getStackTraceString(e));
        }
    }

    
    public abstract void dealData(T value);

    protected void dealError(Throwable e) {
        Throwable throwable = e.getCause();
        if (null == mActivity) {
            return;
        }
        mActivity.runOnUiThread(() -> {
            if (dealmsg(e.getMessage())) {
                
            } else if (!NetUtils.isNetworkConnected(Injection.provideContext())) {
                Toast.makeText(mActivity, Injection.provideContext().getString(R.string.net_work_unconnected), Toast.LENGTH_SHORT).show();
            } else if (e instanceof SocketTimeoutException) {
                Toast.makeText(mActivity, Injection.provideContext().getString(R.string.error_net_timeout), Toast.LENGTH_SHORT).show();
            } else if (e instanceof ConnectException) {
                Toast.makeText(mActivity, Injection.provideContext().getString(R.string.error_net_connect_ex), Toast.LENGTH_SHORT).show();
            } else if (e instanceof DbException || throwable instanceof DbException || throwable instanceof SocketTimeoutException || throwable instanceof SocketException) {
                
            } else if ((e instanceof JsonSyntaxException)) {
                Toast.makeText(mActivity, Injection.provideContext().getString(R.string.ex_parse_error), Toast.LENGTH_SHORT).show();
            } else if (throwable instanceof RemoteServerException) {
                Toast.makeText(mActivity, throwable.getMessage() + " ", Toast.LENGTH_SHORT).show();
            } else {
                if (!TextUtils.isEmpty(e.getMessage()) && OtherDeviceLoginCheck.check(e.getMessage())) {
                    return;
                }
                if (!("null".equals(e.getMessage()) || TextUtils.isEmpty(e.getMessage()))) {

                    Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private boolean dealmsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return false;
        }
        String realMsgName = "error_" + msg.toLowerCase()
                .replace("'", "")
                .replace("%", "")
                .replace(";", "")
                .replace("(", "")
                .replace(",", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "_");

        int id = mActivity.getResources().getIdentifier(realMsgName, "string", mActivity.getPackageName());
        String realMsg = "";
        if (0 != id) {
            realMsg = mActivity.getString(id);
        } else {
            if (msg.contains("insufficient funds")) {
                realMsg = mActivity.getString(R.string.balance_no_enough);
            } else if (msg.contains("insufficient level to transfer")) {
                int strid = mActivity.getResources().getIdentifier("error_insufficient_level_to_transfer", "string", mActivity.getPackageName());
                realMsg = mActivity.getString(strid);
            }
        }
        if (!TextUtils.isEmpty(realMsg)) {
            String finalRealMsg = realMsg;
            mActivity.runOnUiThread(() -> {
                Toast.makeText(mActivity, finalRealMsg, Toast.LENGTH_SHORT).show();
            });
            return true;
        }
        return false;
    }


    public String getDealmsg(String msg) {
        if (mActivity == null) {
            return msg;
        }
        if (TextUtils.isEmpty(msg)) {
            return "";
        }
        String realMsgName = "error_" + msg.toLowerCase()
                .replace("'", "")
                .replace("%", "")
                .replace(";", "")
                .replace("(", "")
                .replace(",", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "_");

        int id = mActivity.getResources().getIdentifier(realMsgName, "string", mActivity.getPackageName());
        String realMsg = "";
        if (0 != id) {
            realMsg = mActivity.getString(id);
        } else {
            if (msg.contains("insufficient funds")) {
                realMsg = mActivity.getString(R.string.balance_no_enough);
            } else if (msg.contains("insufficient level to transfer")) {
                int strid = mActivity.getResources().getIdentifier("error_insufficient_level_to_transfer", "string", mActivity.getPackageName());
                realMsg = mActivity.getString(strid);
            }
        }
        if (!TextUtils.isEmpty(realMsg)) {

            return realMsg;
        }
        return msg;
    }
}
