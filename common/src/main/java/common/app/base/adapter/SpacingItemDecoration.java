package common.app.base.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spacing;
    private boolean includeEdge;

    public SpacingItemDecoration(int spacing, boolean includeEdge) {
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        
        int position = parent.getChildAdapterPosition(view); 

        if (includeEdge && position == 0) {
            outRect.top = spacing;
        }
        outRect.bottom = spacing;
    }

}
