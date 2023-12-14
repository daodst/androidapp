package com.app.note.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityEditNoteBinding;
import com.app.note.db.entity.NoteEntity;

import common.app.base.BaseActivity;
import common.app.ui.view.TitleBarView;
import common.app.utils.TimeUtil;


public class EditNoteActivity extends BaseActivity<EditNoteVM> {

    ActivityEditNoteBinding mVBinding;
    private static final String KEY_ID = "noteId";
    private int mNoteId;

    public static Intent getIntent(Context from, int noteId){
        Intent intent = new Intent(from, EditNoteActivity.class);
        intent.putExtra(KEY_ID, noteId);
        return intent;
    }

    @Override
    public void initParam() {
        mNoteId = getIntent().getIntExtra(KEY_ID, 0);
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mVBinding = ActivityEditNoteBinding.inflate(LayoutInflater.from(this));
        return mVBinding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        mVBinding.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
                insertOrUpdateNote(true);
            }
        });
    }


    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().dataLD, data->{
            showData(data);
        });

        
        getViewModel().observe(getViewModel().deleteLD, deleteId->{
            showToast(R.string.delete_success);
        });

        
        getViewModel().observe(getViewModel().updateLD, updateData->{
            if(null != updateData && mNoteId != updateData.id){
                
                mNoteId = updateData.id;
            }
            if(null != updateData){
                mVBinding.timeTv.setText(TimeUtil.getYYYYMMddHHMM(updateData.getUpdateTime()));
            }
        });


        
        getViewModel().getNote(mNoteId);
    }


    private void showData(NoteEntity data){
        if(null == data){
            return;
        }
        mVBinding.titleEdit.setText(data.getTitle());
        mVBinding.bodyEdit.setText(data.getBody());
        mVBinding.timeTv.setText(TimeUtil.getYYYYMMddHHMM(data.getUpdateTime()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        insertOrUpdateNote(false);
    }

    
    private void insertOrUpdateNote(boolean mustSave) {
        String title = mVBinding.titleEdit.getText().toString().trim();
        String body = mVBinding.bodyEdit.getText().toString().trim();
        if (mNoteId <=0) {
            
            if(TextUtils.isEmpty(title) && TextUtils.isEmpty(body)){
                return;
            }
            getViewModel().insertOrUpdateNote(mNoteId, title, body, mustSave);
        } else {
            if(TextUtils.isEmpty(title) && TextUtils.isEmpty(body)){
                
                if(mustSave) {
                    getViewModel().deleteNote(mNoteId);
                }
            } else {
                getViewModel().insertOrUpdateNote(mNoteId, title, body, mustSave);
            }
        }
    }
}
