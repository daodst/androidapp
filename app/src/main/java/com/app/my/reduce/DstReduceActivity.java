package com.app.my.reduce;

import static common.app.utils.LanguageUtil.TYPE_LAGUAGE_ENGLISH;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.databinding.ActivityDstReduceBinding;
import com.app.me.computing.Spanny;

import common.app.base.BaseActivity;
import common.app.utils.LanguageUtil;


public class DstReduceActivity extends BaseActivity {

    private ActivityDstReduceBinding mViewB;


    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mViewB = ActivityDstReduceBinding.inflate(LayoutInflater.from(this));
        return mViewB.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {

        String local = LanguageUtil.getNowLocalStr(this);


        if (TYPE_LAGUAGE_ENGLISH.equals(local)) {
            
            mViewB.dstReduceTipsPlan1.setTextSize(14);
            mViewB.dstReduceTipsPlan1.setText(new Spanny("Completed community construction in")
                    .append(" 4 ", new StyleSpan(Typeface.BOLD), new AbsoluteSizeSpan(28, true), new ForegroundColorSpan(Color.parseColor("#2CDD8A")))
                    .append("years"));

            mViewB.dstReduceTipsPlan2.setTextSize(14);
            mViewB.dstReduceTipsPlan2.setText(new Spanny("Entering stage")
                    .append(" 2.0 ", new StyleSpan(Typeface.BOLD), new AbsoluteSizeSpan(28, true), new ForegroundColorSpan(Color.parseColor("#F9D540")))
                    .append("through voting"));

            mViewB.dstReduceTipsContent1.setTextSize(13);
            mViewB.dstReduceTipsContent1.setText(new Spanny("Community Voting ", new ForegroundColorSpan(Color.parseColor("#2CDD8A")))
                    .append("to determine whether this plan agreement is effective"));

            mViewB.dstReduceTipsContent2.setTextSize(13);
            
            mViewB.dstReduceTipsContent2.setText(new Spanny("Reduce DST production by")
                    .append(" 50 ", new ForegroundColorSpan(Color.parseColor("#F9D540")), new AbsoluteSizeSpan(18, true))
                    .append("% ", new ForegroundColorSpan(Color.parseColor("#F9D540")))
                    .append("every")
                    .append(" 4 ", new ForegroundColorSpan(Color.parseColor("#F9D540")), new AbsoluteSizeSpan(18, true))
                    .append("years")
            );

            mViewB.dstReduceTips.setText(new Spanny("We are committed to building a decentralized ").append("autonomous organization (DAO)", new ForegroundColorSpan(Color.parseColor("#2CDD8A"))));


        } else {

            mViewB.dstReduceTipsPlan1.setText(new Spanny("4", new StyleSpan(Typeface.BOLD), new AbsoluteSizeSpan(32, true), new ForegroundColorSpan(Color.parseColor("#2CDD8A"))).append("", new ForegroundColorSpan(Color.parseColor("#2CDD8A"))).append(""));
            mViewB.dstReduceTipsPlan2.setText(new Spanny("").append("2.0", new StyleSpan(Typeface.BOLD), new AbsoluteSizeSpan(32, true), new ForegroundColorSpan(Color.parseColor("#F9D540"))).append(""));

            mViewB.dstReduceTipsContent1.setText(new Spanny("").append("", new ForegroundColorSpan(Color.parseColor("#2CDD8A"))).append(""));

            mViewB.dstReduceTipsContent2.setText(new Spanny("", new ForegroundColorSpan(Color.parseColor("#2CDD8A")))
                    .append("4", new ForegroundColorSpan(Color.parseColor("#2CDD8A")), new AbsoluteSizeSpan(22, true))
                    .append("", new ForegroundColorSpan(Color.parseColor("#2CDD8A")))
                    .append("DST")
                    .append("50 ", new ForegroundColorSpan(Color.parseColor("#F9D540")), new AbsoluteSizeSpan(22, true))
                    .append("% ", new ForegroundColorSpan(Color.parseColor("#F9D540"))));

            mViewB.dstReduceTips.setText(new Spanny("").append("(DAO) ", new ForegroundColorSpan(Color.parseColor("#2CDD8A"))));

        }

        
        mViewB.dstReduceBtYes.setOnClickListener(v -> {

        });
        
        mViewB.dstReduceBtNo.setOnClickListener(v -> {

        });
        
        mViewB.dstReduceBt.setOnClickListener(v -> {

        });
    }
}
