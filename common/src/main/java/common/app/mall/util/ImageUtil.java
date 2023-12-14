

package common.app.mall.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.WindowManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageUtil {

	static private final String TAG = "ImageUtil";

	
	public static Bitmap getBitmapFromRes(Context context, int drawableId) {
		Resources res = context.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, drawableId);
		return bmp;
	}

	
	public static Bitmap getBmpFromCamera(Intent data, int i) {
		Bundle bundle = data.getExtras();
		Bitmap bitmap = (Bitmap) bundle.get("data");
		bitmap = ImageUtil.resizeBitmap(bitmap, 0.5f);

		return bitmap;
	}

	
	public static Bitmap getBmpFromCamera(Context context, Uri uri) {
		Bitmap bitmap = ImageUtil.compressImage2(context, uri);
		return bitmap;
	}

	
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	
	public static Bitmap bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	
	public static InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}

	
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	
	public static Bitmap drawableToBitmap(Drawable drawable) {
		
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		
		Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
				: Config.RGB_565;
		
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		
		drawable.draw(canvas);
		return bitmap;
	}

	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		int r = 0;
		
		if (width > height) {
			r = height;
		} else {
			r = width;
		}
		
		Bitmap backgroundBmp = Bitmap.createBitmap(r, r,
				Config.ARGB_8888);
		
		Canvas canvas = new Canvas(backgroundBmp);
		Paint paint = new Paint();
		
		paint.setAntiAlias(true);
		
		RectF rect = new RectF(0, 0, r, r);
		
		
		canvas.drawRoundRect(rect, r / 2, r / 2, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		
		canvas.drawBitmap(bitmap, null, rect, paint);
		
		return backgroundBmp;

	}

	
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
				h / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	
	public static Drawable bitmap2Drawable(Context context, Bitmap bmp) {
		BitmapDrawable bd = new BitmapDrawable(context.getResources(), bmp);
		return bd;
	}

	
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		
		Bitmap oldbmp = drawableToBitmap(drawable);
		
		Matrix matrix = new Matrix();
		
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		
		matrix.postScale(sx, sy);
		
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(newbmp);
	}

	
	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {

		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {

				}
			}
		}
		return file;
	}

	
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) {    
			baos.reset();
			try {
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);
				options -= 10;
			} catch (Exception ex) {

			}
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}

	
	public static Bitmap getimage(String srcPath) {
		Options newOpts = new Options();
		
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		
		float hh = 800f;
		float ww = 480f;
		
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) {
            be = 1;
        }
		newOpts.inSampleSize = be;
		
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);
	}

	
	public static Bitmap getBitmapFromPath(String path, Options mBitmapOption) {
		Bitmap iBitMap = null;
		try {
			FileInputStream is = new FileInputStream(path);
			if (path != null ) {
				iBitMap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, mBitmapOption);
			}
		} catch (Exception e) {
			iBitMap = null;
		} catch (Error er) {
			iBitMap = null;
		}
		return iBitMap;
	}

	
	public static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 200) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, 40, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Options newOpts = new Options();
		
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		
		float hh = 800f;
		float ww = 480f;
		
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) {
            be = 1;
        }
		newOpts.inSampleSize = be;
		
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);
	}

	private static long getBitmapsize(Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	
	public static Bitmap resizeBitmap(Bitmap bitmap, float pc) {
		Bitmap resizeBmp = bitmap;
		long len = getBitmapsize(bitmap);
		if (len > 2 * 1024 * 1024) {
			Matrix matrix = new Matrix();

			matrix.postScale(pc, pc); 
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);

			bitmap.recycle();
			bitmap = null;
			System.gc();

			int width = resizeBmp.getWidth();
			int height = resizeBmp.getHeight();

		}
		return resizeBmp;
	}

	
	public static Bitmap compressImage2(Context context, Uri uri) {
		ContentResolver cr = context.getContentResolver();
		InputStream in = null;
		try {
			in = cr.openInputStream(uri);
			Options options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, options);
			try {
				in.close();
			} catch (IOException e) {
			}
			int degree = readPictureDegree(uri.toString());
			int mWidth = options.outWidth;
			int mHeight = options.outHeight;
			
			float hh = 800f;
			float ww = 480f;
			
			int be = 1;
			if (mWidth > mHeight && mWidth > ww) {
				be = (int) (options.outWidth / ww);
			} else if (mWidth < mHeight && mHeight > hh) {
				be = (int) (options.outHeight / hh);
			}
			if (be <= 0) {
                be = 1;
            }
			options = new Options();
			options.inSampleSize = be;
			in = cr.openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
			return bitmap;
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	
	public static File getSmallBitmap(Activity context, String filePath) {

		final Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		WindowManager wm = context.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		
		options.inSampleSize = calculateInSampleSize(options, width, height);

		
		options.inJustDecodeBounds = false;

		Bitmap bm = BitmapFactory.decodeFile(filePath, options);
		if (bm == null) {
			return null;
		}
		int degree = readPictureDegree(filePath);
		bm = rotateBitmap(bm, degree);
		BufferedOutputStream bos = null;
		
		String path = filePath.substring(filePath.lastIndexOf("/") + 1);
		String address = context.getFilesDir().getPath() + path + ".jpg";
		File file = new File(address);
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bm.compress(Bitmap.CompressFormat.JPEG, 30, bos);

		} catch (Exception e) {

		} finally {
			try {
				if (bos != null) {
                    bos.close();
                }
			} catch (IOException e) {

			}
		}
		return file;

	}

	
	public static Bitmap getSmallBitmap(String filePath, Activity context) {
		final Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		WindowManager wm = context.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		
		options.inSampleSize = calculateInSampleSize(options, width, height);

		
		options.inJustDecodeBounds = false;

		Bitmap bm = BitmapFactory.decodeFile(filePath, options);
		if (bm == null) {
			return null;
		}
		int degree = readPictureDegree(filePath);
		bm = rotateBitmap(bm, degree);
		return bm;
	}

	
	private static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {

		}
		return degree;
	}

	
	private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
		if (bitmap == null) {
            return null;
        }

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	
	private static int calculateInSampleSize(Options options,
											 int reqWidth, int reqHeight) {
		
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
		}

		return inSampleSize;
	}

	public static Bitmap getThumbnail(Context context, Uri uri, int size)
			throws FileNotFoundException, IOException {
		InputStream input = context.getContentResolver().openInputStream(uri);
		Options onlyBoundsOptions = new Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;
		onlyBoundsOptions.inPreferredConfig = Config.ARGB_8888;
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1)
				|| (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }
		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;
		double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
		Options bitmapOptions = new Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true;
		bitmapOptions.inPreferredConfig = Config.ARGB_8888;
		input = context.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	public static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0) {
            return 1;
        } else {
            return k;
        }
	}
}
