package com.app.note.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.note.db.entity.NoteEntity;

import common.app.utils.DisplayUtils;
import common.app.utils.TimeUtil;


public class NoteCardView extends CardView{

    private static final String TAG = "NoteCardView";
    TextView title;
    TextView body;
    TextView time;
    ImageView drawingImage;
    private NoteEntity note;

    public NoteCardView(Context context){
        this(context, null);
    }

    public NoteCardView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public NoteCardView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_note_card, this, true);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int noteGap = (int) DisplayUtils.dp2px(context, 10);
        lp.setMargins(noteGap, noteGap, 0, 0);
        view.setLayoutParams(lp);
        title = view.findViewById(R.id.titleTv);
        body = view.findViewById(R.id.bodyTv);
        time = view.findViewById(R.id.timeTv);
        drawingImage = view.findViewById(R.id.drawingImage);

    }

    public NoteEntity getNote(){
        return note;
    }

    public void bindModel(NoteEntity note){
        this.note = note;
        title.setText(note.getTitle());
        body.setText(note.getBody());
        time.setText(TimeUtil.getRecentTime(note.getUpdateTime()));
    }
}
