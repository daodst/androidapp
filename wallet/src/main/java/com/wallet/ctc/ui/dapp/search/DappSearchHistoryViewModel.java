package com.wallet.ctc.ui.dapp.search;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.DappHistoryEntity;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

import common.app.base.BaseViewModel;
import common.app.utils.WebSiteUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DappSearchHistoryViewModel extends BaseViewModel {
    MutableLiveData<List<DappHistoryEntity>> getBrowseHistoryListLiveData = null;
    MutableLiveData<Boolean> deleteBrowseHistoryLiveData = null;

    
    public DappSearchHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
        getBrowseHistoryListLiveData = new MutableLiveData<>();
        deleteBrowseHistoryLiveData = new MutableLiveData<>();
    }

    @SuppressLint("CheckResult")
    public void getHistoryList() {
        DBManager.getInstance(getApplication()).getBrowseHistoryList().subscribe(browseHistoryEntities -> {
            getBrowseHistoryListLiveData.setValue(browseHistoryEntities);
        });
    }

    @SuppressLint("CheckResult")
    public void addHistoryList(String url) {
        DappHistoryEntity en = new DappHistoryEntity();
        en.setTime(System.currentTimeMillis());
        en.setUrl(url);
        
        DBManager.getInstance(getApplication()).insertBrowseHistory(en).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (TextUtils.isEmpty(en.getIconPath())) {
                    loadWebsiteInfo(en);
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    public void deleteHistory() {
        DBManager.getInstance(getApplication()).deleteBrowseHistory().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                deleteBrowseHistoryLiveData.setValue(true);
            }
        });
    }

    @SuppressLint("CheckResult")
    public void loadWebsiteInfo(DappHistoryEntity en) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                try {
                    Document doc = Jsoup.connect(en.url).get();
                    en.title = doc.head().getElementsByTag("title").text();
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
                                String logoPath = WebSiteUtil.getDomain(en.url) + path;
                                Log.i("dappHis", "logoPath="+logoPath);
                                en.iconPath = logoPath;
                                DBManager.getInstance(getApplication()).updateBrowseHistory(en).subscribe();
                                return;
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        });

    }
}
