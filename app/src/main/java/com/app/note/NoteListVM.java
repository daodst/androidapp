package com.app.note;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.note.db.NoteDb;
import com.app.note.db.entity.NoteEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import common.app.base.BaseViewModel;


public class NoteListVM extends BaseViewModel {

    MutableLiveData<List<NoteEntity>> listLD;
    Executor mExecutor;

    
    public NoteListVM(@NonNull Application application) {
        super(application);
        listLD = new MutableLiveData<>();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    
    public void getNotes(){
        mExecutor.execute(()->{
           List<NoteEntity> notes = NoteDb.getInstance().noteDao().getAllNotes();
           listLD.postValue(notes);
        });
    }
}
