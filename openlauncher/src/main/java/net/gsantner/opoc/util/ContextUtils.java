
package net.gsantner.opoc.util;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.graphics.Bitmap.CompressFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.text.TextUtilsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import net.gsantner.opoc.format.markdown.SimpleMarkdownParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "ObsoleteSdkInt", "deprecation", "SpellCheckingInspection", "TryFinallyCanBeTryWithResources", "UnusedAssignment", "UnusedReturnValue"})
public class ContextUtils {
    
    
    
    protected Context _context;

    public ContextUtils(Context context) {
        _context = context;
    }

    public Context context() {
        return _context;
    }

    public void freeContextRef() {
        _context = null;
    }

    
    
    
    public enum ResType {
        ID, BOOL, INTEGER, COLOR, STRING, ARRAY, DRAWABLE, PLURALS,
        ANIM, ATTR, DIMEN, LAYOUT, MENU, RAW, STYLE, XML,
    }

    
    public int getResId(final ResType resType, final String name) {
        try {
            return _context.getResources().getIdentifier(name, resType.name().toLowerCase(), _context.getPackageName());
        } catch (Exception e) {
            return 0;
        }
    }

    
    public String rstr(@StringRes final int strResId) {
        if (strResId == 0) {
            return "";
        }
        try {
            return _context.getString(strResId);
        } catch (Exception e) {
            return null;
        }
    }

    
    public String rstr(final String strResKey, Object... a0getResKeyAsFallback) {
        try {
            return rstr(getResId(ResType.STRING, strResKey));
        } catch (Resources.NotFoundException e) {
            return a0getResKeyAsFallback != null && a0getResKeyAsFallback.length > 0 ? strResKey : null;
        }
    }

    
    public Drawable rdrawable(@DrawableRes final int resId) {
        try {
            return ContextCompat.getDrawable(_context, resId);
        } catch (Exception e) {
            return null;
        }
    }

    
    public int rcolor(@ColorRes final int resId) {
        if (resId == 0) {
            Log.e(getClass().getName(), "ContextUtils::rcolor: resId is 0!");
            return Color.BLACK;
        }
        return ContextCompat.getColor(_context, resId);
    }

    
    public boolean areRessourcesAvailable(final ResType resType, final String... resIdsTextual) {
        for (String name : resIdsTextual) {
            if (getResId(resType, name) == 0) {
                return false;
            }
        }
        return true;
    }

    
    public static String colorToHexString(final int intColor, final boolean... withAlpha) {
        boolean a = withAlpha != null && withAlpha.length >= 1 && withAlpha[0];
        return String.format(a ? "#%08X" : "#%06X", (a ? 0xFFFFFFFF : 0xFFFFFF) & intColor);
    }

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")";
    }

    public String getAppVersionName() {
        PackageManager manager = _context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageIdManifest(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            try {
                PackageInfo info = manager.getPackageInfo(getPackageIdReal(), 0);
                return info.versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return "?";
    }

    public String getAppInstallationSource() {
        String src = null;
        try {
            src = _context.getPackageManager().getInstallerPackageName(getPackageIdManifest());
        } catch (Exception ignored) {
        }
        if (src == null || src.trim().isEmpty()) {
            return "Sideloaded";
        } else if (src.toLowerCase().contains(".amazon.")) {
            return "Amazon Appstore";
        }
        switch (src) {
            case "com.android.vending":
            case "com.google.android.feedback": {
                return "Google Play";
            }
            case "org.fdroid.fdroid.privileged":
            case "org.fdroid.fdroid": {
                return "F-Droid";
            }
            case "com.github.yeriomin.yalpstore": {
                return "Yalp Store";
            }
            case "cm.aptoide.pt": {
                return "Aptoide";
            }
            case "com.android.packageinstaller": {
                return "Package Installer";
            }
        }
        return src;
    }

    
    public ContextUtils openWebpageInExternalBrowser(final String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    
    public String getPackageIdManifest() {
        String pkg = rstr("manifest_package_id");
        String applicationId = !TextUtils.isEmpty(pkg) ? pkg : _context.getPackageName();
        return applicationId;
    }

    
    public String getPackageIdReal() {
        return _context.getPackageName();
    }

    
    public Object getBuildConfigValue(final String fieldName) {
        final String pkg = getPackageIdManifest() + ".BuildConfig";
        try {
            Class<?> c = Class.forName(pkg);
            return c.getField(fieldName).get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getBuildConfigFields() {
        final String pkg = getPackageIdManifest() + ".BuildConfig";
        final List<String> fields = new ArrayList<>();
        try {
            for (Field f : Class.forName(pkg).getFields()) {
                fields.add(f.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fields;
    }

    
    public Boolean bcbool(final String fieldName, final Boolean defaultValue) {
        Object field = getBuildConfigValue(fieldName);
        if (field instanceof Boolean) {
            return (Boolean) field;
        }
        return defaultValue;
    }

    
    public String bcstr(final String fieldName, final String defaultValue) {
        Object field = getBuildConfigValue(fieldName);
        if (field instanceof String) {
            return (String) field;
        }
        return defaultValue;
    }

    
    public Integer bcint(final String fieldName, final int defaultValue) {
        Object field = getBuildConfigValue(fieldName);
        if (field instanceof Integer) {
            return (Integer) field;
        }
        return defaultValue;
    }

    
    public boolean isGooglePlayBuild() {
        return bcbool("IS_GPLAY_BUILD", true);
    }

    
    public boolean isFossBuild() {
        return bcbool("IS_FOSS_BUILD", false);
    }

    public String readTextfileFromRawRes(@RawRes int rawResId, String linePrefix, String linePostfix) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        String line;

        linePrefix = linePrefix == null ? "" : linePrefix;
        linePostfix = linePostfix == null ? "" : linePostfix;

        try {
            br = new BufferedReader(new InputStreamReader(_context.getResources().openRawResource(rawResId)));
            while ((line = br.readLine()) != null) {
                sb.append(linePrefix);
                sb.append(line);
                sb.append(linePostfix);
                sb.append("\n");
            }
        } catch (Exception ignored) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        return sb.toString();
    }

    
    public boolean isConnectedToInternet() {
        try {
            ConnectivityManager con = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo activeNetInfo =
                    con == null ? null : con.getActiveNetworkInfo();
            return activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        } catch (Exception ignored) {
            throw new RuntimeException("Error: Developer forgot to declare a permission");
        }
    }

    
    public boolean isAppInstalled(String packageName) {
        try {
            PackageManager pm = _context.getApplicationContext().getPackageManager();
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    
    public void restartApp(Class classToStart) {
        Intent intent = new Intent(_context, classToStart);
        PendingIntent pendi = PendingIntent.getActivity(_context, 555, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        if (_context instanceof Activity) {
            ((Activity) _context).finish();
        }
        if (mgr != null) {
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendi);
        } else {
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
        }
        Runtime.getRuntime().exit(0);
    }

    
    public String loadMarkdownForTextViewFromRaw(@RawRes int rawMdFile, String prepend) {
        try {
            return new SimpleMarkdownParser()
                    .parse(_context.getResources().openRawResource(rawMdFile),
                            prepend, SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW)
                    .replaceColor("#000001", rcolor(getResId(ResType.COLOR, "accent")))
                    .removeMultiNewlines().replaceBulletCharacter("*").getHtml();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    
    public void setHtmlToTextView(TextView textView, String html) {
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(new SpannableString(htmlToSpanned(html)));
    }

    
    public double getEstimatedScreenSizeInches() {
        DisplayMetrics dm = _context.getResources().getDisplayMetrics();

        double calc = dm.density * 160d;
        double x = Math.pow(dm.widthPixels / calc, 2);
        double y = Math.pow(dm.heightPixels / calc, 2);
        calc = Math.sqrt(x + y) * 1.16;  
        return Math.min(12, Math.max(4, calc));
    }

    
    public boolean isInPortraitMode() {
        return _context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    
    public Locale getLocaleByAndroidCode(String androidLC) {
        if (!TextUtils.isEmpty(androidLC)) {
            return androidLC.contains("-r")
                    ? new Locale(androidLC.substring(0, 2), androidLC.substring(4, 6)) 
                    : new Locale(androidLC); 
        }
        return Resources.getSystem().getConfiguration().locale;
    }

    
    public void setAppLanguage(final String androidLC) {
        Locale locale = getLocaleByAndroidCode(androidLC);
        locale = (locale != null && !androidLC.isEmpty()) ? locale : Resources.getSystem().getConfiguration().locale;
        setLocale(locale);
    }

    public ContextUtils setLocale(final Locale locale) {
        Configuration config = _context.getResources().getConfiguration();
        config.locale = (locale != null ? locale : Resources.getSystem().getConfiguration().locale);
        _context.getResources().updateConfiguration(config, null);
        Locale.setDefault(locale);
        return this;
    }

    
    public boolean shouldColorOnTopBeLight(@ColorInt final int colorOnBottomInt) {
        return 186 > (((0.299 * Color.red(colorOnBottomInt))
                + ((0.587 * Color.green(colorOnBottomInt))
                + (0.114 * Color.blue(colorOnBottomInt)))));
    }

    
    public Spanned htmlToSpanned(final String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    
    public float convertPxToDp(final float px) {
        return px / _context.getResources().getDisplayMetrics().density;
    }

    
    public float convertDpToPx(final float dp) {
        return dp * _context.getResources().getDisplayMetrics().density;
    }

    
    @SuppressWarnings("StatementWithEmptyBody")
    public File getAppDataPrivateDir() {
        File filesDir;
        try {
            filesDir = new File(new File(_context.getPackageManager().getPackageInfo(getPackageIdReal(), 0).applicationInfo.dataDir), "files");
        } catch (PackageManager.NameNotFoundException e) {
            filesDir = _context.getFilesDir();
        }
        if (!filesDir.exists() && filesDir.mkdirs()) ;
        return filesDir;
    }

    
    @SuppressWarnings("StatementWithEmptyBody")
    public List<Pair<File, String>> getAppDataPublicDirs(boolean internalStorageFolder, boolean sdcardFolders, boolean storageNameWithoutType) {
        List<Pair<File, String>> dirs = new ArrayList<>();
        for (File externalFileDir : ContextCompat.getExternalFilesDirs(_context, null)) {
            if (externalFileDir == null || Environment.getExternalStorageDirectory() == null) {
                continue;
            }
            boolean isInt = externalFileDir.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().getAbsolutePath());
            boolean add = (internalStorageFolder && isInt) || (sdcardFolders && !isInt);
            if (add) {
                dirs.add(new Pair<>(externalFileDir, getStorageName(externalFileDir, storageNameWithoutType)));
                if (!externalFileDir.exists() && externalFileDir.mkdirs()) ;
            }
        }
        return dirs;
    }

    public String getStorageName(final File externalFileDir, final boolean storageNameWithoutType) {
        boolean isInt = externalFileDir.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().getAbsolutePath());

        String[] split = externalFileDir.getAbsolutePath().split("/");
        if (split.length > 2) {
            return isInt ? (storageNameWithoutType ? "Internal Storage" : "") : (storageNameWithoutType ? split[2] : ("SD Card (" + split[2] + ")"));
        } else {
            return "Storage";
        }
    }

    public List<Pair<File, String>> getStorages(final boolean internalStorageFolder, final boolean sdcardFolders) {
        List<Pair<File, String>> storages = new ArrayList<>();
        for (Pair<File, String> pair : getAppDataPublicDirs(internalStorageFolder, sdcardFolders, true)) {
            if (pair.first != null && pair.first.getAbsolutePath().lastIndexOf("/Android/data") > 0) {
                try {
                    storages.add(new Pair<>(new File(pair.first.getCanonicalPath().replaceFirst("/Android/data.*", "")), pair.second));
                } catch (IOException ignored) {
                }
            }
        }
        return storages;
    }

    public File getStorageRootFolder(final File file) {
        String filepath;
        try {
            filepath = file.getCanonicalPath();
        } catch (Exception ignored) {
            return null;
        }
        for (Pair<File, String> storage : getStorages(false, true)) {
            
            if (filepath.startsWith(storage.first.getAbsolutePath())) {
                return storage.first;
            }
        }
        return null;
    }

    
    public void mediaScannerScanFile(final File... files) {
        if (android.os.Build.VERSION.SDK_INT > 19) {
            String[] paths = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                paths[i] = files[i].getAbsolutePath();
            }
            MediaScannerConnection.scanFile(_context, paths, null, null);
        } else {
            for (File file : files) {
                _context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            }
        }
    }

    
    public static void setDrawableWithColorToImageView(ImageView imageView, @DrawableRes int drawableResId, @ColorRes int colorResId) {
        imageView.setImageResource(drawableResId);
        imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), colorResId));
    }

    
    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof VectorDrawableCompat
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && drawable instanceof VectorDrawable)
                || ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable instanceof AdaptiveIconDrawable))) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = (DrawableCompat.wrap(drawable)).mutate();
            }

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        return bitmap;
    }

    
    public Bitmap drawableToBitmap(@DrawableRes final int drawableId) {
        try {
            return drawableToBitmap(ContextCompat.getDrawable(_context, drawableId));
        } catch (Exception e) {
            return null;
        }
    }

    
    public Bitmap loadImageFromFilesystem(final File imagePath, final int maxDimen) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, maxDimen);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath.getAbsolutePath(), options);
    }

    
    public int calculateInSampleSize(final BitmapFactory.Options options, final int maxDimen) {
        
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (Math.max(height, width) > maxDimen) {
            inSampleSize = Math.round(1f * Math.max(height, width) / maxDimen);
        }
        return inSampleSize;
    }

    
    public Bitmap scaleBitmap(final Bitmap bitmap, final int maxDimen) {
        int picSize = Math.min(bitmap.getHeight(), bitmap.getWidth());
        float scale = 1.f * maxDimen / picSize;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    
    public boolean writeImageToFile(final File targetFile, final Bitmap image, Integer... a0quality) {
        final int quality = (a0quality != null && a0quality.length > 0 && a0quality[0] >= 0 && a0quality[0] <= 100) ? a0quality[0] : 70;
        final String lc = targetFile.getAbsolutePath().toLowerCase(Locale.ROOT);
        final CompressFormat format = lc.endsWith(".webp") ? CompressFormat.WEBP : (lc.endsWith(".png") ? CompressFormat.PNG : CompressFormat.JPEG);

        boolean ok = false;
        File folder = new File(targetFile.getParent());
        if (folder.exists() || folder.mkdirs()) {
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(targetFile);
                image.compress(format, quality, stream);
                ok = true;
            } catch (Exception ignored) {
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }
        try {
            image.recycle();
        } catch (Exception ignored) {
        }
        return ok;
    }

    
    public Bitmap drawTextOnDrawable(@DrawableRes final int drawableRes, final String text, final int textSize) {
        Resources resources = _context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = drawableToBitmap(drawableRes);

        bitmap = bitmap.copy(bitmap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(61, 61, 61));
        paint.setTextSize((int) (textSize * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;
        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    
    @SuppressWarnings("ConstantConditions")
    public void tintMenuItems(final Menu menu, final boolean recurse, @ColorInt final int iconColor) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            try {
                tintDrawable(item.getIcon(), iconColor);
                if (item.hasSubMenu() && recurse) {
                    tintMenuItems(item.getSubMenu(), recurse, iconColor);
                }
            } catch (Exception ignored) {
                
            }
        }
    }

    
    public Drawable tintDrawable(@DrawableRes final int drawableRes, @ColorInt final int color) {
        return tintDrawable(rdrawable(drawableRes), color);
    }

    
    public Drawable tintDrawable(@Nullable Drawable drawable, @ColorInt final int color) {
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), color);
        }
        return drawable;
    }

    
    public void setSubMenuIconsVisiblity(final Menu menu, final boolean visible) {
        if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            return;
        }
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                @SuppressLint("PrivateApi") Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, visible);
            } catch (Exception ignored) {
                Log.d(getClass().getName(), "Error: 'setSubMenuIconsVisiblity' not supported on this device");
            }
        }
    }


    public String getLocalizedDateFormat() {
        return ((SimpleDateFormat) android.text.format.DateFormat.getDateFormat(_context)).toPattern();
    }

    public String getLocalizedTimeFormat() {
        return ((SimpleDateFormat) android.text.format.DateFormat.getTimeFormat(_context)).toPattern();
    }

    public String getLocalizedDateTimeFormat() {
        return getLocalizedDateFormat() + " " + getLocalizedTimeFormat();
    }

    
    @SuppressWarnings("Convert2Lambda")
    public static final InputFilter INPUTFILTER_FILENAME = new InputFilter() {
        public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
            if (src.length() < 1) return null;
            char last = src.charAt(src.length() - 1);
            String illegal = "|\\?*<\":>[]/'";
            if (illegal.indexOf(last) > -1) return src.subSequence(0, src.length() - 1);
            return null;
        }
    };

    
    public static class DoTouchView implements Runnable {
        View _view;

        public DoTouchView(View view) {
            _view = view;
        }

        @Override
        public void run() {
            _view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
            _view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        }
    }


    public String getMimeType(final File file) {
        return getMimeType(Uri.fromFile(file));
    }

    
    public String getMimeType(final Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = _context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String filename = uri.toString();
            if (filename.endsWith(".jenc")) {
                filename = filename.replace(".jenc", "");
            }
            String ext = MimeTypeMap.getFileExtensionFromUrl(filename);
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());

            
            if (TextUtils.isEmpty(mimeType)) {
                switch (ext) {
                    case "md":
                    case "markdown":
                    case "mkd":
                    case "mdown":
                    case "mkdn":
                    case "mdwn":
                    case "rmd":
                        mimeType = "text/markdown";
                        break;
                    case "yaml":
                    case "yml":
                        mimeType = "text/yaml";
                        break;
                    case "json":
                        mimeType = "text/json";
                        break;
                    case "txt":
                        mimeType = "text/plain";
                        break;
                }
            }
        }

        if (TextUtils.isEmpty(mimeType)) {
            mimeType = "*
    @SuppressLint("MissingPermission")
    public boolean isWifiConnected(boolean... enabledOnly) {
        final boolean doEnabledCheckOnly = enabledOnly != null && enabledOnly.length > 0 && enabledOnly[0];
        final ConnectivityManager connectivityManager = (ConnectivityManager) _context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo != null && (doEnabledCheckOnly ? wifiInfo.isAvailable() : wifiInfo.isConnected());
    }

    
    public boolean isDeviceOrientationPortrait() {
        final int rotation = ((WindowManager) _context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        return (rotation == Surface.ROTATION_0) || (rotation == Surface.ROTATION_180);
    }
}


