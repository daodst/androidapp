

package common.app.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.R;
import common.app.R2;



public class StepProgressView extends SimpleLinearLayout {

    private ViewHolder mViewHolder;

    private String mStep1Str,mStep2Str,mStep3Str,mStep4Str;

    public StepProgressView(Context context) {
        super(context);
    }

    public StepProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.stepProgressStyle);
        this.mStep1Str = typedArray.getString(R.styleable.stepProgressStyle_setp1text);
        this.mStep2Str = typedArray.getString(R.styleable.stepProgressStyle_setp2text);
        this.mStep3Str = typedArray.getString(R.styleable.stepProgressStyle_setp3text);
        this.mStep4Str = typedArray.getString(R.styleable.stepProgressStyle_setp4text);
        typedArray.recycle();
        init();
    }

    private void init() {
        if (null != mViewHolder) {
            if (!TextUtils.isEmpty(mStep1Str)) {
                mViewHolder.progress_text1.setText(mStep1Str);
            }
            if (!TextUtils.isEmpty(mStep2Str)) {
                mViewHolder.progress_text2.setText(mStep2Str);
            }
            if (!TextUtils.isEmpty(mStep3Str)) {
                mViewHolder.progress_text3.setText(mStep3Str);
            }
            if (!TextUtils.isEmpty(mStep4Str)) {
                mViewHolder.progress_text4.setText(mStep4Str);
            }
        }
    }

    
    public void initProgress(int mNowStep){
        if (null != mViewHolder) {
            switch (mNowStep) {
                case 1:
                    mViewHolder.view_weight.setLayoutParams(new LinearLayout.LayoutParams(0, 5, 4.0f));
                    mViewHolder.progress_text3.setVisibility(View.GONE);
                    mViewHolder.round_4.setVisibility(View.GONE);
                    mViewHolder.round_1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.green_round));
                    mViewHolder.round_2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gray_round));
                    mViewHolder.round_3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gray_round));
                    break;
                case 2:
                    mViewHolder.view_weight.setLayoutParams(new LinearLayout.LayoutParams(0, 5, 4.0f));
                    mViewHolder.progress_text3.setVisibility(View.GONE);
                    mViewHolder.round_4.setVisibility(View.GONE);
                    mViewHolder.round_1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ok_round));
                    mViewHolder.round_2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.green_round));
                    mViewHolder.round_3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gray_round));
                    break;
                case 3:
                    mViewHolder.view_weight.setLayoutParams(new LinearLayout.LayoutParams(0, 5, 4.0f));
                    mViewHolder.progress_text3.setVisibility(View.GONE);
                    mViewHolder.round_4.setVisibility(View.GONE);
                    mViewHolder.round_1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ok_round));
                    mViewHolder.round_2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ok_round));
                    mViewHolder.round_3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ok_round));
                    break;
                case 4:
                    mViewHolder.view_weight.setLayoutParams(new LinearLayout.LayoutParams(0, 5, 6.0f));
                    mViewHolder.progress_text3.setVisibility(View.VISIBLE);
                    mViewHolder.round_4.setVisibility(View.VISIBLE);
                    mViewHolder.round_1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ok_round));
                    mViewHolder.round_2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.error_round));
                    mViewHolder.round_3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.green_round));
                    mViewHolder.round_4.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gray_round));
                    break;
            }

        }
    }

    @Override
    protected void initViews() {
        this.contentView = View.inflate(mContext, R.layout.step_progress_view, this);
        mViewHolder = new ViewHolder(contentView);
    }

    static class ViewHolder {
        @BindView(R2.id.progress_text1)
        TextView progress_text1;
        @BindView(R2.id.progress_text2)
        TextView progress_text2;
        @BindView(R2.id.progress_text3)
        TextView progress_text3;
        @BindView(R2.id.progress_text4)
        TextView progress_text4;
        @BindView(R2.id.round_1)
        ImageView round_1;
        @BindView(R2.id.round_2)
        ImageView round_2;
        @BindView(R2.id.round_3)
        ImageView round_3;
        @BindView(R2.id.round_4)
        ImageView round_4;
        @BindView(R2.id.progress_weight)
        View view_weight;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
