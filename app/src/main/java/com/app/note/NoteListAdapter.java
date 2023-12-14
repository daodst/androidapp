package com.app.note;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.note.db.entity.NoteEntity;
import com.app.note.edit.EditNoteActivity;
import com.app.note.view.NoteCardView;

import java.util.ArrayList;
import java.util.List;


public class NoteListAdapter extends RecyclerView.Adapter {

    List<NoteEntity> mDatas;
    public NoteListAdapter() {
        mDatas = new ArrayList<>();
    }
    View.OnClickListener noteOnClickListener = new View.OnClickListener(){
        @Override public void onClick(View v){
            if (v instanceof NoteCardView){
                NoteCardView noteCardView = (NoteCardView) v;
                NoteEntity noteBean = noteCardView.getNote();
                v.getContext().startActivity(EditNoteActivity.getIntent(v.getContext(), noteBean.id));
            }
        }
    };


    public void bindDatas(List<NoteEntity> nodes){
        mDatas.clear();
        if(nodes != null){
            mDatas.addAll(nodes);
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = new NoteCardView(parent.getContext());
        view.setOnClickListener(noteOnClickListener);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.itemView instanceof NoteCardView){
            NoteCardView noteCardView = (NoteCardView) holder.itemView;
            noteCardView.bindModel(mDatas.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public class SimpleViewHolder extends RecyclerView.ViewHolder{
        public SimpleViewHolder(View itemView){
            super(itemView);
        }
    }
}
