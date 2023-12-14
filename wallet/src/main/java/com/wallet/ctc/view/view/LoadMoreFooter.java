package com.wallet.ctc.view.view;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.IntDef;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoadMoreFooter {

    public static final int STATE_DISABLED = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_FINISHED = 2;
    public static final int STATE_ENDLESS = 3;
    public static final int STATE_FAILED = 4;

    @IntDef({STATE_DISABLED, STATE_LOADING, STATE_FINISHED, STATE_ENDLESS, STATE_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public interface OnLoadMoreListener {

        void onLoadMore();

    }

    @BindView(R2.id.progress_wheel)
    ProgressWheel progressWheel;

    @BindView(R2.id.tv_text)
    android.widget.TextView tvText;

    @State
    private int state = STATE_DISABLED;

    private final OnLoadMoreListener loadMoreListener;

    public LoadMoreFooter(@androidx.annotation.NonNull Context context, @androidx.annotation.NonNull HeaderAndFooterRecyclerView recyclerView, @androidx.annotation.NonNull OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        android.view.View footerView = LayoutInflater.from(context).inflate(R.layout.footer_load_more, recyclerView.getFooterContainer(), false);
        recyclerView.addFooterView(footerView);
        ButterKnife.bind(this, footerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                    checkLoadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                    checkLoadMore();
                }
            }

        });
    }


    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        if (this.state != state) {
            this.state = state;
            switch (state) {
                case STATE_DISABLED:
                    progressWheel.setVisibility(android.view.View.INVISIBLE);
                    progressWheel.stopSpinning();
                    tvText.setVisibility(android.view.View.INVISIBLE);
                    tvText.setText(null);
                    tvText.setClickable(false);
                    break;
                case STATE_LOADING:
                    progressWheel.setVisibility(android.view.View.VISIBLE);
                    progressWheel.spin();
                    tvText.setVisibility(android.view.View.INVISIBLE);
                    tvText.setText(null);
                    tvText.setClickable(false);
                    break;
                case STATE_FINISHED:
                    progressWheel.setVisibility(android.view.View.INVISIBLE);
                    progressWheel.stopSpinning();
                    tvText.setVisibility(android.view.View.VISIBLE);
                    tvText.setText(R.string.load_more_finished);
                    tvText.setClickable(false);
                    progressWheel.setVisibility(android.view.View.GONE);
                    tvText.setVisibility(android.view.View.GONE);
                    break;
                case STATE_ENDLESS:
                    progressWheel.setVisibility(android.view.View.INVISIBLE);
                    progressWheel.stopSpinning();
                    tvText.setVisibility(android.view.View.VISIBLE);
                    tvText.setText(null);
                    tvText.setClickable(true);
                    break;
                case STATE_FAILED:
                    progressWheel.setVisibility(android.view.View.INVISIBLE);
                    progressWheel.stopSpinning();
                    tvText.setVisibility(android.view.View.VISIBLE);
                    tvText.setText(R.string.load_more_failed);
                    tvText.setClickable(true);
                    break;
                default:
                    throw new AssertionError("Unknow load more state.");
            }
        }
    }

    private void checkLoadMore() {
        if (getState() == STATE_ENDLESS || getState() == STATE_FAILED) {
            setState(STATE_LOADING);
            loadMoreListener.onLoadMore();
        }
    }

    @OnClick(R2.id.tv_text)
    void onBtnTextClick() {
        checkLoadMore();
    }

}
