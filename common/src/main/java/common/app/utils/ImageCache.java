
package common.app.utils;

import android.graphics.Bitmap;

import androidx.collection.LruCache;

public class ImageCache {
	
	private ImageCache() {
		
		cache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 8)) {
              @Override
              protected int sizeOf(String key, Bitmap value) {
                  return value.getRowBytes() * value.getHeight();
              }
          };
	}

	private static ImageCache imageCache = null;

	public static synchronized ImageCache getInstance() {
		if (imageCache == null) {
			imageCache = new ImageCache();
		}
		return imageCache;

	}
	private LruCache<String, Bitmap> cache = null;
	
	
	public Bitmap put(String key, Bitmap value){
		return cache.put(key, value);
	}
	
	
	public Bitmap get(String key){
		return cache.get(key);
	}
}
