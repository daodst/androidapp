

package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.DBManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;
import common.app.pojo.NameIdBean;
import common.app.ui.view.ContentDialogUtil;
import common.app.utils.AllUtils;
import common.app.utils.WebSiteUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;



public class DappMoreDialog {
    private final String TAG = "ShareDialog";
    @BindView(R2.id.dapp_copy_url)
    TextView dappCopyUrl;
    @BindView(R2.id.dapp_open_web)
    TextView dappOpenWeb;
    @BindView(R2.id.dapp_reflse)
    TextView dappReflse;
    @BindView(R2.id.dapp_share)
    TextView dappShare;
    @BindView(R2.id.dapp_cancel)
    TextView dappCancel;
    @BindView(R2.id.dapp_collect)
    TextView dappCollect;
    @BindView(R2.id.url_tip_tv)
    TextView urlTipTv;

    private Context mContext;
    private Dialog mDialog;
    private String url;

    
    public DappMoreDialog(Context context) {
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.dialog_dappmore, null);
        ButterKnife.bind(this, layout);

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(layout, layoutParams);

        Window win = dialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.dialogAnim;
        win.setAttributes(lp);
        mDialog = dialog;
    }


    public void show(String u) {
        url=u;
        mDialog.show();
        init();
    }

    
    private boolean hasCollect = false;
    private void init() {
        String host = AllUtils.urlToHost(url);
        urlTipTv.setText(String.format(mContext.getString(R.string.dapp_servir_submit), host));

        
        if (!TextUtils.isEmpty(url)) {
            DBManager.getInstance(mContext).getLikeBrowseHistoryList(url).subscribe(lists->{
                if (lists != null && lists.size() > 0) {
                    logoPath = lists.get(0).iconPath;
                    titleStr = lists.get(0).title;
                    setCollect(true);
                } else {
                    setCollect(false);
                }
            });

        }

    }

    
    private void setCollect(boolean collected) {
        hasCollect = collected;
        if (hasCollect) {
            
            dappCollect.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                    ContextCompat.getDrawable(mContext, R.mipmap.dapp_cancel_collect),
                    null, null);
            dappCollect.setText(R.string.dapp_cancle_collect);
        } else {
            
            dappCollect.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                    ContextCompat.getDrawable(mContext, R.mipmap.dapp_collect),
                    null, null);
            dappCollect.setText(R.string.dapp_collect);
        }
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    @OnClick({R2.id.dapp_copy_url, R2.id.dapp_open_web,R2.id.dapp_reflse,R2.id.dapp_share,R2.id.dapp_cancel, R2.id.dapp_collect,
                R2.id.dapp_switch_wallet})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.dapp_copy_url) {
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(url);
            ToastUtil.showToast(mContext.getResources().getString(R.string.copyed));
            mDialog.dismiss();
        } else if(i == R.id.dapp_switch_wallet) {
            mDialog.dismiss();
            if(null!=onClick) {
                onClick.onSwitchWallet();
            }
        } else if (i == R.id.dapp_open_web) {

        }else if (i == R.id.dapp_reflse) {
            if(null!=onClick) {
                onClick.reflse();
            }
        }else if (i == R.id.dapp_share) {
            mDialog.dismiss();
        }else if (i == R.id.dapp_cancel) {
            mDialog.dismiss();
        } else if(i == R.id.dapp_collect) {
            
            if (hasCollect) {
                
                DBManager.getInstance(mContext).cancelLikeBrowserHistory(url).subscribe(success->{
                    setCollect(false);
                    ToastUtil.showToast(mContext.getString(R.string.has_cancle_collect));
                });
            } else {
                
                collectAlert();
            }
            mDialog.dismiss();
        } else {
        }
    }
    private OnClicks onClick;
    public interface OnClicks{
        void reflse();
        void onSwitchWallet();
    }
    public void SetOnClick(OnClicks click){
        onClick=click;
    }


    String logoPath = "";
    String titleStr = "";
    private void collectAlert() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_dapp_collect, null);
        ContentDialogUtil dialogUtil = new ContentDialogUtil(mContext, rootView, true);
        dialogUtil.show();
        dialogUtil.findViewById(R.id.close_iv).setOnClickListener(v -> {
            dialogUtil.dismiss();
        });
        ImageView logo = (ImageView) dialogUtil.findViewById(R.id.logo_iv);
        EditText nameEdit = (EditText) dialogUtil.findViewById(R.id.name_edit);
        TextView urlTv = (TextView) dialogUtil.findViewById(R.id.url_tv);
        ProgressBar progressBar = (ProgressBar) dialogUtil.findViewById(R.id.progress_bar);
        urlTv.setText(url+"");
        if (!TextUtils.isEmpty(titleStr)) {
            nameEdit.setText(titleStr);
        }
        if (!TextUtils.isEmpty(logoPath)) {
            Glide.with(mContext).load(logoPath).
                    placeholder(R.mipmap.js_ic_bowse_web_icon_default)
                    .error(R.mipmap.js_ic_bowse_web_icon_default).into(logo);
            progressBar.setVisibility(View.GONE);
            logo.setVisibility(View.VISIBLE);
        }
        
        dialogUtil.findViewById(R.id.add_btn).setOnClickListener(v -> {
            String title = nameEdit.getText().toString().trim();
            if (TextUtils.isEmpty(url)) {
                ToastUtil.showToast("url can not be empty");
                return;
            }
            if (TextUtils.isEmpty(title)) {
                ToastUtil.showToast(mContext.getString(R.string.dapp_name_input_hint));
                return;
            }

            DBManager.getInstance(mContext).setLikeBrowseHistory(url, title, logoPath).subscribe(success->{
                ToastUtil.showToast(mContext.getString(R.string.has_collected));
                dialogUtil.dismiss();
            });
        });
        dialogUtil.setOnDismissListener(dialog -> {
            if (null != mDisposable) {
                mDisposable.dispose();
            }
        });

        if (TextUtils.isEmpty(logoPath)) {
            loadWebsiteInfo(url, data -> {
                if (null != progressBar && null != logo) {
                    progressBar.setVisibility(View.GONE);
                    logo.setVisibility(View.VISIBLE);
                }
                if (null == data) {
                    return;
                }
                logoPath = data.logo;
                if (null != nameEdit && !TextUtils.isEmpty(data.name)) {
                    nameEdit.setText(data.name);
                }
                if (null != logo && !TextUtils.isEmpty(data.logo)) {
                    Glide.with(mContext).load(data.logo).
                            placeholder(R.mipmap.js_ic_bowse_web_icon_default)
                            .error(R.mipmap.js_ic_bowse_web_icon_default).into(logo);
                }
            });
        }

    }

    
    private Disposable mDisposable;
    interface GetInfoCallBack{
        void onGet(NameIdBean data);
    }
    public void loadWebsiteInfo(String url, GetInfoCallBack callBack) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mDisposable = Observable.create(new ObservableOnSubscribe<NameIdBean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<NameIdBean> emitter) throws Exception {
                NameIdBean data = new NameIdBean();
                try {
                    Document doc = Jsoup.connect(url).timeout(3000).get();
                    String title = doc.head().getElementsByTag("title").text();
                    data.name = title;
                    Elements elements = doc.head().getElementsByTag("link");
                    for (int i = 0; i < elements.size(); i++) {
                        Element et = elements.get(i);
                        Elements temp = et.getElementsByAttributeValue("rel", "icon");
                        if (temp.size() == 0) {
                            temp = et.getElementsByAttributeValue("rel", "shortcut icon");
                        }
                        if (temp.size() == 0) {
                            continue;
                        }
                        for (Element e : temp) {
                            String path = e.attr("href");
                            if (!TextUtils.isEmpty(path)) {
                                Log.i("dappHis", "logoPath="+logoPath);
                                String iconPath = WebSiteUtil.getDomain(url) + path;
                                data.logo = iconPath;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                emitter.onNext(data);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<NameIdBean>() {
            @Override
            public void accept(NameIdBean nameIdData) throws Exception {
                if (null != callBack) {
                    callBack.onGet(nameIdData);
                }
            }
        });
    }


}
