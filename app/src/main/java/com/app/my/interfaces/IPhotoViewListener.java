

package com.app.my.interfaces;

import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;

import com.app.my.zoom.PhotoViewAttacher;


public interface IPhotoViewListener {
    
    boolean canZoom();

    
    RectF getDisplayRect();

    
    float getMinScale();

    
    float getMidScale();

    
    float getMaxScale();

    
    float getScale();

    
    ImageView.ScaleType getScaleType();

    
    void setAllowParentInterceptOnEdge(boolean allow);

    
    void setMinScale(float minScale);

    
    void setMidScale(float midScale);

    
    void setMaxScale(float maxScale);

    
    void setOnLongClickListener(View.OnLongClickListener listener);

    
    void setOnMatrixChangeListener(PhotoViewAttacher.OnMatrixChangedListener listener);

    
    void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener listener);

    
    void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener listener);

    
    void setScaleType(ImageView.ScaleType scaleType);

    
    void setZoomable(boolean zoomable);

    
    void zoomTo(float scale, float focalX, float focalY);
}
