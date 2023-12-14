

package im.vector.app.features.call.phone.logs;

import static im.vector.app.features.pay4invite.Pay4InviteFragmentKt.RESULT_VALUE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.matrix.android.sdk.internal.database.model.ChatPhoneLog;

import java.util.List;

import im.vector.app.R;
import im.vector.app.databinding.SmActivityDialHistoryBinding;
import im.vector.app.features.call.phone.logs.adapter.SMDialHistoryAdapter;
import im.vector.app.provide.ChatStatusProvide;


public class SMDialHistoryActivity extends AppCompatActivity {
    SmActivityDialHistoryBinding mBinding;

    private SMDialHistoryAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = SmActivityDialHistoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);

        initView();
    }


    public void initView() {
        

        mBinding.ivBack.setOnClickListener(v -> finish());

        mAdapter = new SMDialHistoryAdapter();
        mBinding.rvList.setAdapter(mAdapter);

        mBinding.tvTitleAll.setOnClickListener(v -> switchTitleBar(1));
        mBinding.tvTitleMissed.setOnClickListener(v -> switchTitleBar(2));

        initData();
        mAdapter.setConsumer(chatPhoneLog -> {

            Intent intent = new Intent();
            intent.putExtra(RESULT_VALUE, chatPhoneLog.getPhone());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }

    public void initData() {
        switchTitleBar(1);
    }

    
    private void switchTitleBar(int type) {
        Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.sm_icon_call_history_switch_1);
        Drawable drawable1 = ContextCompat.getDrawable(this, R.mipmap.sm_icon_call_history_switch_2);
        if (type == 1) {
            mBinding.llTitleBar.setBackground(drawable1);
        } else {
            mBinding.llTitleBar.setBackground(drawable);
        }
        List<ChatPhoneLog> chatLog = ChatStatusProvide.getChatLog(this, 1 == type ? 0 : 2);
        mAdapter.setChatPhoneLogs(chatLog);
        if (chatLog.isEmpty()) {
            mBinding.phoneCallEmptyParent.setVisibility(View.VISIBLE);
        } else {
            mBinding.phoneCallEmptyParent.setVisibility(View.GONE);
        }
    }
}
