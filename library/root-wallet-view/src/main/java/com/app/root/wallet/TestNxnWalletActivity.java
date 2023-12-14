package com.app.root.wallet;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TestNxnWalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_rootw);

        NxnWalletLayoutView view = findViewById(R.id.nxnLayoutView);
        view.updateWalletCount(1,2,100);

        view.setOnClickDetectorListener(new OnClickDetectorListener() {
            @Override
            public void onClick(String type) {
                showToast("onClick-"+type);
            }

            @Override
            public void onLongClick(String type) {
                showToast("onLongClick-"+type);
            }

            @Override
            public void onHit(String fromType, String toType) {
                showToast("onHit-"+fromType+":"+toType);
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
