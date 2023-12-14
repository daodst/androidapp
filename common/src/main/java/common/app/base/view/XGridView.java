

package common.app.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;



public class XGridView extends GridView {
    private Context context;
    public XGridView(Context context) {
        super(context);
        this.context = context;
    }

    public XGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public XGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }




    
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        

    }


}
