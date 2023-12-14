

package common.app.base.share.qr;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.R;
import common.app.R2;
import common.app.mall.BaseActivity;
import common.app.ui.view.TitleBarView;



public class QrResultActivity extends BaseActivity {
    @BindView(R2.id.title_bar)
    TitleBarView mTitleBar;
    @BindView(R2.id.qr_result)
    TextView mQrResult;
    @BindView(R2.id.copy_text)
    Button mCopyText;
    private Unbinder mUnbinder;

    public static String KEY_QR_CONTENT = "qrContent";
    private String mResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResult = getIntent().getStringExtra(KEY_QR_CONTENT);
        setView(R.layout.activity_qr_result);

    }

    @Override
    protected void initView() {
        super.initView();
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mTitleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
            }
        });
        if (!TextUtils.isEmpty(mResult)) {
            mQrResult.setText(mResult);
        } else {
            showResult(getString(R.string.qr_result_null));
        }

        mCopyText.setOnClickListener(v -> {
            clipboardText(mResult);
        });
    }

    protected void clipboardText(String content) {
        
        
        ClipboardManager cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        
        cm.setText(content);
        Toast.makeText(this, getString(R.string.clip_over), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }
}
