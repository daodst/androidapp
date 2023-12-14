package com.app.note.edit;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.app.note.db.NoteDb;
import com.app.note.db.entity.NoteEntity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import common.app.base.BaseViewModel;


public class EditNoteVM extends BaseViewModel {

    private Executor mExecutor;

    
    MutableLiveData<NoteEntity> updateLD;
    MutableLiveData<Integer> deleteLD;
    MutableLiveData<NoteEntity> dataLD;

    
    public EditNoteVM(@NonNull Application application) {
        super(application);
        mExecutor = Executors.newSingleThreadExecutor();
        updateLD = new MutableLiveData<>();
        deleteLD = new MutableLiveData<>();
        dataLD = new MutableLiveData<>();
    }


    
    public void insertOrUpdateNote(int noteId, String title, String body, boolean showResult){
        mExecutor.execute(()->{
            NoteEntity oldNote = null;
            if(noteId > 0){
                oldNote = NoteDb.getInstance().noteDao().getNote(noteId);
            }
            long id = NoteDb.getInstance().noteDao().insert(createNote(title, body, oldNote));
            NoteEntity note = NoteDb.getInstance().noteDao().getNote((int)id);
            updateLD.postValue(note);
            if(showResult){
                showToast(getApplication().getString(R.string.save_success));
            }
        });
    }

    public boolean equals(String str1, String str2) {
        if (!TextUtils.isEmpty(str1)){
            return str1.equals(str2);
        } else if(!TextUtils.isEmpty(str2)){
            return str2.equals(str1);
        } else {
            return true;
        }
    }

    public NoteEntity createNote(String title, String body, NoteEntity oldNote){
        NoteEntity note = null;
        if (null != oldNote && oldNote.id > 0){
            
            note = oldNote;
            if(!equals(oldNote.getTitle(), title) || !equals(oldNote.getBody(), body)){
                note.setUpdateTime(System.currentTimeMillis());
            }
        } else {
            note = new NoteEntity();
            
            note.setUpdateTime(System.currentTimeMillis());
            note.setCreateTime(System.currentTimeMillis());
        }
        note.setTitle(title);
        note.setBody(body);
        return note;
    }


    public void deleteNote(int noteId){
        mExecutor.execute(()->{
            NoteDb.getInstance().noteDao().deleteWhere(noteId);
            deleteLD.postValue(noteId);
        });
    }


    public void getNote(int noteId) {
        mExecutor.execute(()->{
            NoteEntity note = NoteDb.getInstance().noteDao().getNote(noteId);
            dataLD.postValue(note);
        });
    }
}
