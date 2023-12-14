

package common.app.my.localalbum.bean;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.Serializable;

import common.app.my.localalbum.utils.LocalImageHelper;



public class LocalFile implements Serializable {
    private boolean ishttp = false;
    private String size;
    private String originalUri;
    private String thumbnailUri;
    private int orientation;
    private Bitmap bitmap = null;
    public Bitmap getBitmap() {
        if(bitmap == null){
            try {
                bitmap = LocalImageHelper.revitionImageSize(getOriginalUri());
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean ishttp() {
        return ishttp;
    }

    public void setIshttp(boolean ishttp) {
        this.ishttp = ishttp;
    }
    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public void setOriginalUri(String originalUri) {
        this.originalUri = originalUri;
    }


    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int exifOrientation) {
        orientation =  exifOrientation;
    }

}
