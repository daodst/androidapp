package com.wallet.ctc.ui.realize;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.wallet.ctc.R;
import com.wallet.ctc.databinding.ActivityUsdtrealizeBinding;

import common.app.base.BaseActivity;
import common.app.base.them.Eyes;


public class USDTRealizeActivity extends BaseActivity<USDTRealizeVM> {

    ActivityUsdtrealizeBinding binding;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        binding = ActivityUsdtrealizeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        Eyes.setTranslucent(this);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.usdtRealizeRoot.getLayoutParams();
        layoutParams.topMargin = Eyes.getStatusBarHeight(this);


        getViewModel().dst.observe(this, s -> {
            SpannableString spannableString = new SpannableString("DST: " + s);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor(R.color.color_14bf9d));
            spannableString.setSpan(colorSpan, "DST:".length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            binding.usdtRealizeOneDst.setText(spannableString);
        });
        getViewModel().eth.observe(this, s -> {
            SpannableString spannableString = new SpannableString("ETH: " + s);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor(R.color.color_14bf9d));
            spannableString.setSpan(colorSpan, "ETH:".length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            binding.usdtRealizeOneEth.setText(spannableString);
            binding.usdtRealizeThreeEth.setText(spannableString);
        });
        getViewModel().dstUsdt.observe(this, s -> {
            SpannableString spannableString = new SpannableString("USDT(DST): " + s);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor(R.color.color_14bf9d));
            spannableString.setSpan(colorSpan, "USDT(DST):".length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            binding.usdtRealizeOneUsdt.setText(spannableString);
        });
        getViewModel().ethUsdt.observe(this, s -> {
            SpannableString spannableString = new SpannableString("USDT(ETH): " + s);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor(R.color.color_14bf9d));
            spannableString.setSpan(colorSpan, "USDT(ETH):".length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            binding.usdtRealizeThreeUsdt.setText(spannableString);
        });

        getViewModel().getData();

        binding.usdtRealizeBack.setOnClickListener(v -> {
            finish();
        });
        binding.usdtRealizeOneBt.setOnClickListener(v -> {
            finish();
        });
        binding.usdtRealizeTwoBt.setOnClickListener(v -> {
            try {
                Uri uri = Uri.parse("https://app.tether.to/app/login");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.usdtRealizeThreeBt.setOnClickListener(v -> {
            try {
                Uri uri = Uri.parse("https://app.tether.to/app/deposit");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
