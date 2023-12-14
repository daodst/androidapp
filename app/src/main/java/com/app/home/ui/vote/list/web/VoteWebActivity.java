package com.app.home.ui.vote.list.web;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;

import com.app.R;
import com.app.databinding.ActivityVoteWebBinding;

import java.util.Locale;

import common.app.base.BaseActivity;
import common.app.utils.LanguageUtil;

public class VoteWebActivity extends BaseActivity {

    public final String HTML_STYLE = "<style type=\"text/css\">\n" +
            "img,iframe,video,table,div {height:auto; max-width:100%; width:100% !important; word-break:break-all;} \n</style>\n";


    ActivityVoteWebBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = ActivityVoteWebBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(mBinding.getRoot());
    }

    @Override
    public void initData() {
        mBinding.voteTopbar.setLeftTv(v -> {
            finish();
        }).setMiddleTv(R.string.vote_list_topbar_title, R.color.default_titlebar_title_color);

        WebSettings settings = mBinding.voteWeb.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        String data = HTML_STYLE + getDataEn();
        Locale local = LanguageUtil.getNowLocal(this);
        String language = local.getLanguage();
        if (language.equals(Locale.SIMPLIFIED_CHINESE.getLanguage()) || language.equals(Locale.CHINESE.getLanguage())) {
            data = HTML_STYLE + getDatazh();
        }

        mBinding.voteWeb.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public static final String getDataEn() {
        StringBuilder builder = new StringBuilder();
        builder.append("<h4 style=\"text-align: center;\">How to Initiate a Governance Vote</h4>");
        builder.append("The first step is to download the PC client from the official website,https://www.freemasonry.cc/");
        builder.append("<br>");
        builder.append("<br>");
        builder.append(" The second step is to install and run the PC client. After opening, click the Blockchain Browser button to open the blockchain browser");
        
        builder.append("<br>");
        builder.append("<br>");
        builder.append("<img src='file:
        builder.append("<br>");
        builder.append("<br>");
        builder.append(" The third step is to import the wallet plug-in in the browser and import the corresponding wallet.");
        
        builder.append("<br>");
        builder.append("<br>");
        builder.append("<img src='file:
        builder.append("<br>");
        builder.append("<br>");
        builder.append(" The fourth step is to click Governance, and then click the Initiate Proposal button.");
        
        builder.append("<br>");
        builder.append("<br>");
        builder.append("<img src='file:
        return builder.toString();
    }

    public static final String getDatazh() {
        StringBuilder builder = new StringBuilder();
        builder.append("<h4 style=\"text-align: center;\"></h4>");
        builder.append("，PC，https://www.freemasonry.cc/");
        builder.append("<br>");
        builder.append("<br>");
        builder.append("，PC，，Blockchain Browser，");
        
        builder.append("<br>");
        builder.append("<br>");
        builder.append("<img src='file:
        builder.append("<br>");
        builder.append("<br>");
        builder.append(" ，，。");
        
        builder.append("<br>");
        builder.append("<br>");
        builder.append("<img src='file:
        builder.append("<br>");
        builder.append("<br>");
        builder.append("，，。");
        
        builder.append("<br>");
        builder.append("<br>");
        builder.append("<img src='file:

        return builder.toString();
    }
}
