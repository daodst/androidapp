package com.app.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.app.databinding.ActivityNoteListBinding;
import com.app.note.db.entity.NoteEntity;
import com.app.note.edit.EditNoteActivity;

import java.util.List;

import common.app.base.BaseActivity;
import common.app.ui.view.TitleBarView;

public class NoteListActivity extends BaseActivity<NoteListVM> {

    ActivityNoteListBinding mVBinding;

    private NoteListAdapter mAdapter;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mVBinding = ActivityNoteListBinding.inflate(LayoutInflater.from(this));
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
            }
        });

        mVBinding.addNoteBtn.setOnClickListener(v -> {
            startActivity(new Intent(NoteListActivity.this, EditNoteActivity.class));
        });

        StaggeredGridLayoutManager slm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        slm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mVBinding.recyclerVeiw.setLayoutManager(slm);
        mAdapter = new NoteListAdapter();
        mVBinding.recyclerVeiw.setAdapter(mAdapter);
    }


    @Override
    public void initData() {
        getViewModel().observe(getViewModel().listLD, datas->{
            showDatas(datas);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDatas();
    }

    private void getDatas() {
        getViewModel().getNotes();
    }

    private void showDatas(List<NoteEntity> notes) {
        mAdapter.bindDatas(notes);
        if(notes == null || notes.size() == 0){
            mVBinding.emptyInclude.emptyNotesView.setVisibility(View.VISIBLE);
        } else {
            mVBinding.emptyInclude.emptyNotesView.setVisibility(View.GONE);
            mAdapter.bindDatas(notes);
        }
    }
}
