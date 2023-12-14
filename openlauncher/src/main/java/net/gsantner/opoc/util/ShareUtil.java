
package net.gsantner.opoc.util;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Pair;
import androidx.documentfile.provider.DocumentFile;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "SameParameterValue", "unused", "deprecation", "ConstantConditions", "ObsoleteSdkInt", "SpellCheckingInspection", "JavadocReference", "ConstantLocale"})
public class ShareUtil {
    public final static String EXTRA_FILEPATH = "real_file_path_2";
    public final static SimpleDateFormat SDF_RFC3339_ISH = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss", Locale.getDefault());
    public final static SimpleDateFormat SDF_SHORT = new SimpleDateFormat("yyMMdd-HHmmss", Locale.getDefault());
    public final static SimpleDateFormat SDF_IMAGES = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()); 
    public final static String MIME_TEXT_PLAIN = "text/plain";
    public final static String PREF_KEY__SAF_TREE_URI = "pref_key__saf_tree_uri";

    public final static int REQUEST_CAMERA_PICTURE = 50001;
    public final static int REQUEST_PICK_PICTURE = 50002;
    public final static int REQUEST_SAF = 50003;

    public final static int MIN_OVERWRITE_LENGTH = 5;

    protected static String _lastCameraPictureFilepath;
    protected static String _fileProviderAuthority;

    protected Context _context;
    protected String _chooserTitle;

    public ShareUtil(final Context context) {
        _context = context;
        _chooserTitle = "➥";
    }

    public void setContext(final Context c) {
        _context = c;
    }

    public void freeContextRef() {
        _context = null;
    }

    public String getFileProviderAuthority() {
        if (TextUtils.isEmpty(_fileProviderAuthority)) {
            throw new RuntimeException("Error at ShareUtil.getFileProviderAuthority(): No FileProvider authority provided");
        }
        return _fileProviderAuthority;
    }

    public static void setFileProviderAuthority(final String fileProviderAuthority) {
        _fileProviderAuthority = fileProviderAuthority;
    }


    public ShareUtil setChooserTitle(final String title) {
        _chooserTitle = title;
        return this;
    }

    
    public Uri getUriByFileProviderAuthority(final File file) {
        return FileProvider.getUriForFile(_context, getFileProviderAuthority(), file);
    }

    
    public void showChooser(final Intent intent, final String chooserText) {
        try {
            _context.startActivity(Intent.createChooser(intent, chooserText != null ? chooserText : _chooserTitle));
        } catch (Exception ignored) {
        }
    }

    
    public void createLauncherDesktopShortcut(final Intent intent, @DrawableRes final int iconRes, final String title) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (intent.getAction() == null) {
            intent.setAction(Intent.ACTION_VIEW);
        }

        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(_context, Long.toString(new Random().nextLong()))
                .setIntent(intent)
                .setIcon(IconCompat.createWithResource(_context, iconRes))
                .setShortLabel(title)
                .setLongLabel(title)
                .build();
        ShortcutManagerCompat.requestPinShortcut(_context, shortcut, null);
    }

    
    public void createLauncherDesktopShortcutLegacy(final Intent intent, @DrawableRes final int iconRes, final String title) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (intent.getAction() == null) {
            intent.setAction(Intent.ACTION_VIEW);
        }

        Intent creationIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        creationIntent.putExtra("duplicate", true);
        creationIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        creationIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        creationIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(_context, iconRes));
        _context.sendBroadcast(creationIntent);
    }

    
    public void shareText(final String text, @Nullable final String mimeType) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType(mimeType != null ? mimeType : MIME_TEXT_PLAIN);
        showChooser(intent, null);
    }

    
    public boolean shareStream(final File file, final String mimeType) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(EXTRA_FILEPATH, file.getAbsolutePath());
        intent.setType(mimeType);

        try {
            Uri fileUri = FileProvider.getUriForFile(_context, getFileProviderAuthority(), file);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            showChooser(intent, null);
            return true;
        } catch (Exception ignored) { 
        }
        return false;
    }

    
    public boolean shareStreamMultiple(final Collection<File> files, final String mimeType) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (File file : files) {
            File uri = new File(file.toString());
            uris.add(FileProvider.getUriForFile(_context, getFileProviderAuthority(), file));
        }

        try {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType(mimeType);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            showChooser(intent, null);
            return true;
        } catch (Exception e) { 
            return false;
        }
    }

    
    public boolean createCalendarAppointment(@Nullable final String title, @Nullable final String description, @Nullable final String location, @Nullable final Long... startAndEndTime) {
        Intent intent = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);
        if (title != null) {
            intent.putExtra(CalendarContract.Events.TITLE, title);
        }
        if (description != null) {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, (description.length() > 800 ? description.substring(0, 800) : description));
        }
        if (location != null) {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        }
        if (startAndEndTime != null) {
            if (startAndEndTime.length > 0 && startAndEndTime[0] > 0) {
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startAndEndTime[0]);
            }
            if (startAndEndTime.length > 1 && startAndEndTime[1] > 0) {
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startAndEndTime[1]);
            }
        }

        try {
            _context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    
    public boolean viewFileInOtherApp(final File file, @Nullable final String type) {
        
        Uri fileUri = null;
        try {
            fileUri = FileProvider.getUriForFile(_context, getFileProviderAuthority(), file);
        } catch (Exception ignored) {
            try {
                fileUri = Uri.fromFile(file);
            } catch (Exception ignored2) {
            }
        }

        if (fileUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.setData(fileUri);
            intent.putExtra(EXTRA_FILEPATH, file.getAbsolutePath());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(fileUri, type);
            showChooser(intent, null);
            return true;
        }
        return false;
    }

    
    public boolean shareImage(final Bitmap bitmap, final Integer... quality) {
        try {
            File file = new File(_context.getCacheDir(), getFilenameWithTimestamp());
            if (bitmap != null && new ContextUtils(_context).writeImageToFile(file, bitmap, quality)) {
                String x = FileUtils.getMimeType(file);
                shareStream(file, FileUtils.getMimeType(file));
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    
    public static String getFilenameWithTimestamp(String... A0prefixA1postfixA2ext) {
        final String prefix = (((A0prefixA1postfixA2ext != null && A0prefixA1postfixA2ext.length > 0 && !TextUtils.isEmpty(A0prefixA1postfixA2ext[0])) ? A0prefixA1postfixA2ext[0] : "Screenshot") + "_").trim().replaceFirst("^_$", "");
        final String postfix = ("_" + ((A0prefixA1postfixA2ext != null && A0prefixA1postfixA2ext.length > 1 && !TextUtils.isEmpty(A0prefixA1postfixA2ext[1])) ? A0prefixA1postfixA2ext[1] : "")).trim().replaceFirst("^_$", "");
        final String ext = (A0prefixA1postfixA2ext != null && A0prefixA1postfixA2ext.length > 2 && !TextUtils.isEmpty(A0prefixA1postfixA2ext[2])) ? A0prefixA1postfixA2ext[2] : "jpg";
        return String.format("%s%s%s.%s", prefix.trim(), SDF_IMAGES.format(new Date()), postfix.trim(), ext.toLowerCase().replace(".", "").replace("jpeg", "jpg"));
    }

    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public PrintJob print(final WebView webview, final String jobName, final boolean... landscape) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final PrintDocumentAdapter printAdapter;
            final PrintManager printManager = (PrintManager) _context.getSystemService(Context.PRINT_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                printAdapter = webview.createPrintDocumentAdapter(jobName);
            } else {
                printAdapter = webview.createPrintDocumentAdapter();
            }
            final PrintAttributes.Builder attrib = new PrintAttributes.Builder();
            if (landscape != null && landscape.length > 0 && landscape[0]) {
                attrib.setMediaSize(new PrintAttributes.MediaSize("ISO_A4", "android", 11690, 8270));
                attrib.setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0));
            }
            if (printManager != null) {
                try {
                    return printManager.print(jobName, printAdapter, attrib.build());
                } catch (Exception ignored) {
                }
            }
        } else {
            Log.e(getClass().getName(), "ERROR: Method called on too low Android API version");
        }
        return null;
    }


    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public PrintJob createPdf(final WebView webview, final String jobName) {
        return print(webview, jobName);
    }


    
    @Nullable
    public static Bitmap getBitmapFromWebView(final WebView webView, final boolean... a0fullpage) {
        try {
            
            if (a0fullpage != null && a0fullpage.length > 0 && a0fullpage[0]) {
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                webView.measure(widthMeasureSpec, heightMeasureSpec);
                webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());
            }

            
            webView.buildDrawingCache();

            
            Bitmap bitmap = Bitmap.createBitmap(webView.getMeasuredWidth(), webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, 0, bitmap.getHeight(), new Paint());

            webView.draw(canvas);
            webView.destroyDrawingCache();

            return bitmap;
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    
    public boolean setClipboard(final CharSequence text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager cm = ((android.text.ClipboardManager) _context.getSystemService(Context.CLIPBOARD_SERVICE));
            if (cm != null) {
                cm.setText(text);
                return true;
            }
        } else {
            android.content.ClipboardManager cm = ((android.content.ClipboardManager) _context.getSystemService(Context.CLIPBOARD_SERVICE));
            if (cm != null) {
                ClipData clip = ClipData.newPlainText(_context.getPackageName(), text);
                try {
                    cm.setPrimaryClip(clip);
                } catch (Exception ignored) {
                }
                return true;
            }
        }
        return false;
    }

    
    public List<String> getClipboard() {
        List<String> clipper = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager cm = ((android.text.ClipboardManager) _context.getSystemService(Context.CLIPBOARD_SERVICE));
            if (cm != null && !TextUtils.isEmpty(cm.getText())) {
                clipper.add(cm.getText().toString());
            }
        } else {
            android.content.ClipboardManager cm = ((android.content.ClipboardManager) _context.getSystemService(Context.CLIPBOARD_SERVICE));
            if (cm != null && cm.hasPrimaryClip()) {
                ClipData data = cm.getPrimaryClip();
                for (int i = 0; data != null && i < data.getItemCount() && i < data.getItemCount(); i++) {
                    ClipData.Item item = data.getItemAt(i);
                    if (item != null && !TextUtils.isEmpty(item.getText())) {
                        clipper.add(data.getItemAt(i).getText().toString());
                    }
                }
            }
        }
        return clipper;
    }

    
    public void pasteOnHastebin(final String text, final Callback.a2<Boolean, String> callback, final String... serverOrNothing) {
        final Handler handler = new Handler();
        final String server = (serverOrNothing != null && serverOrNothing.length > 0 && serverOrNothing[0] != null)
                ? serverOrNothing[0] : "https://hastebin.com";
        new Thread() {
            public void run() {
                
                String ret = NetworkUtils.performCall(server + "/documents", NetworkUtils.POST, text);
                final String key = (ret.length() > 15) ? ret.split("\"")[3] : "";
                handler.post(() -> callback.callback(!key.isEmpty(), server + "/" + key));
            }
        }.start();
    }

    
    public void draftEmail(final String subject, final String body, final String... to) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        if (subject != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (body != null) {
            intent.putExtra(Intent.EXTRA_TEXT, body);
        }
        if (to != null && to.length > 0 && to[0] != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, to);
        }
        showChooser(intent, null);
    }

    
    public File extractFileFromIntent(final Intent receivingIntent) {
        String action = receivingIntent.getAction();
        String type = receivingIntent.getType();
        File tmpf;
        String tmps;
        String fileStr;

        if ((Intent.ACTION_VIEW.equals(action) || Intent.ACTION_EDIT.equals(action)) || Intent.ACTION_SEND.equals(action)) {
            
            if (receivingIntent.hasExtra((tmps = EXTRA_FILEPATH))) {
                return new File(receivingIntent.getStringExtra(tmps));
            }

            
            Uri fileUri = receivingIntent.getData();
            if (fileUri != null && (fileStr = fileUri.toString()) != null) {
                
                if (fileStr.startsWith("file://")) {
                    return new File(fileUri.getPath());
                }
                if (fileStr.startsWith((tmps = "content://"))) {
                    fileStr = fileStr.substring(tmps.length());
                    String fileProvider = fileStr.substring(0, fileStr.indexOf("/"));
                    fileStr = fileStr.substring(fileProvider.length() + 1);

                    
                    if (fileStr.startsWith("storage/")) {
                        fileStr = "/" + fileStr;
                    }
                    
                    for (String prefix : new String[]{"file", "document", "root_files", "name"}) {
                        if (fileStr.startsWith(prefix)) {
                            fileStr = fileStr.substring(prefix.length());
                        }
                    }

                    
                    for (String prefix : new String[]{"external/", "media/", "storage_root/"}) {
                        if (fileStr.startsWith((tmps = prefix))) {
                            File f = new File(Uri.decode(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileStr.substring(tmps.length())));
                            if (f.exists()) {
                                return f;
                            }
                        }
                    }

                    
                    for (String fp : new String[]{"org.nextcloud.files", "org.nextcloud.beta.files", "org.owncloud.files"}) {
                        if (fileProvider.equals(fp) && fileStr.startsWith(tmps = "external_files/")) {
                            return new File(Uri.decode("/storage/" + fileStr.substring(tmps.length())));
                        }
                    }
                    
                    if (fileProvider.equals("com.android.externalstorage.documents") && fileStr.startsWith(tmps = "/primary%3A")) {
                        return new File(Uri.decode(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileStr.substring(tmps.length())));
                    }
                    
                    if (fileProvider.equals("com.mi.android.globalFileexplorer.myprovider") && fileStr.startsWith(tmps = "external_files")) {
                        return new File(Uri.decode(Environment.getExternalStorageDirectory().getAbsolutePath() + fileStr.substring(tmps.length())));
                    }

                    if (fileStr.startsWith(tmps = "external_files/")) {
                        for (String prefix : new String[]{Environment.getExternalStorageDirectory().getAbsolutePath(), "/storage", ""}) {
                            File f = new File(Uri.decode(prefix + "/" + fileStr.substring(tmps.length())));
                            if (f.exists()) {
                                return f;
                            }
                        }

                    }

                    
                    if (fileStr.startsWith("/") || fileStr.startsWith("%2F")) {
                        tmpf = new File(Uri.decode(fileStr));
                        if (tmpf.exists()) {
                            return tmpf;
                        } else if ((tmpf = new File(fileStr)).exists()) {
                            return tmpf;
                        }
                    }
                }
            }
            fileUri = receivingIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (fileUri != null && !TextUtils.isEmpty(tmps = fileUri.getPath()) && tmps.startsWith("/") && (tmpf = new File(tmps)).exists()) {
                return tmpf;
            }
        }
        return null;
    }

    
    public void requestGalleryPicture() {
        if (!(_context instanceof Activity)) {
            throw new RuntimeException("Error: ShareUtil.requestGalleryPicture needs an Activity Context.");
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            ((Activity) _context).startActivityForResult(intent, REQUEST_PICK_PICTURE);
        } catch (Exception ex) {
            Toast.makeText(_context, "No gallery app installed!", Toast.LENGTH_SHORT).show();
        }
    }

    public String extractFileFromIntentStr(final Intent receivingIntent) {
        File f = extractFileFromIntent(receivingIntent);
        return f != null ? f.getAbsolutePath() : null;
    }

    
    @SuppressWarnings("RegExpRedundantEscape")
    public String requestCameraPicture(final File target) {
        if (!(_context instanceof Activity)) {
            throw new RuntimeException("Error: ShareUtil.requestCameraPicture needs an Activity Context.");
        }
        String cameraPictureFilepath = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(_context.getPackageManager()) != null) {
            File photoFile;
            try {
                
                if (target != null && !target.isDirectory()) {
                    photoFile = target;
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss", Locale.ENGLISH);
                    File storageDir = target != null ? target : new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
                    String imageFileName = ((new ContextUtils(_context).rstr("app_name")).replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + "_").replace("__", "_") + sdf.format(new Date());
                    photoFile = new File(storageDir, imageFileName + ".jpg");
                    if (!photoFile.getParentFile().exists() && !photoFile.getParentFile().mkdirs()) {
                        photoFile = File.createTempFile(imageFileName + "_", ".jpg", storageDir);
                    }
                }

                
                if (!photoFile.getParentFile().exists() && photoFile.getParentFile().mkdirs()) ;

                
                cameraPictureFilepath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                return null;
            }

            
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri uri = FileProvider.getUriForFile(_context, getFileProviderAuthority(), photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                } else {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                ((Activity) _context).startActivityForResult(takePictureIntent, REQUEST_CAMERA_PICTURE);
            }
        }
        _lastCameraPictureFilepath = cameraPictureFilepath;
        return cameraPictureFilepath;
    }

    
    @SuppressLint("ApplySharedPref")
    public Object extractResultFromActivityResult(final int requestCode, final int resultCode, final Intent data, final Activity... activityOrNull) {
        Activity activity = greedyGetActivity(activityOrNull);
        switch (requestCode) {
            case REQUEST_CAMERA_PICTURE: {
                String picturePath = (resultCode == RESULT_OK) ? _lastCameraPictureFilepath : null;
                if (picturePath != null) {
                    sendLocalBroadcastWithStringExtra(REQUEST_CAMERA_PICTURE + "", EXTRA_FILEPATH, picturePath);
                }
                return picturePath;
            }
            case REQUEST_PICK_PICTURE: {
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    String picturePath = null;

                    Cursor cursor = _context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        for (String column : filePathColumn) {
                            int curColIndex = cursor.getColumnIndex(column);
                            if (curColIndex == -1) {
                                continue;
                            }
                            picturePath = cursor.getString(curColIndex);
                            if (!TextUtils.isEmpty(picturePath)) {
                                break;
                            }
                        }
                        cursor.close();
                    }

                    
                    data.setAction(Intent.ACTION_VIEW);
                    picturePath = picturePath != null ? picturePath : extractFileFromIntentStr(data);

                    
                    if (picturePath == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            ParcelFileDescriptor parcelFileDescriptor = _context.getContentResolver().openFileDescriptor(selectedImage, "r");
                            if (parcelFileDescriptor != null) {
                                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                                FileInputStream input = new FileInputStream(fileDescriptor);

                                
                                picturePath = File.createTempFile("image", "tmp", _context.getCacheDir()).getAbsolutePath();
                                FileUtils.writeFile(new File(picturePath), FileUtils.readCloseBinaryStream(input));
                            }
                        } catch (IOException ignored) {
                            
                        }
                    }

                    
                    if (picturePath != null) {
                        sendLocalBroadcastWithStringExtra(REQUEST_CAMERA_PICTURE + "", EXTRA_FILEPATH, picturePath);
                    }
                    return picturePath;
                }
                break;
            }

            case REQUEST_SAF: {
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri treeUri = data.getData();
                    PreferenceManager.getDefaultSharedPreferences(_context).edit().putString(PREF_KEY__SAF_TREE_URI, treeUri.toString()).commit();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        activity.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    return treeUri;
                }
                break;
            }
        }
        return null;
    }

    
    public void sendLocalBroadcastWithStringExtra(final String action, final String extra, final CharSequence value) {
        Intent intent = new Intent(action);
        intent.putExtra(extra, value);
        LocalBroadcastManager.getInstance(_context).sendBroadcast(intent);
    }

    
    public BroadcastReceiver receiveResultFromLocalBroadcast(final Callback.a2<Intent, BroadcastReceiver> callback, final boolean autoUnregister, final String... filterActions) {
        IntentFilter intentFilter = new IntentFilter();
        for (String filterAction : filterActions) {
            intentFilter.addAction(filterAction);
        }
        final BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    if (autoUnregister) {
                        LocalBroadcastManager.getInstance(_context).unregisterReceiver(this);
                    }
                    try {
                        callback.callback(intent, this);
                    } catch (Exception ignored) {
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(_context).registerReceiver(br, intentFilter);
        return br;
    }

    
    public void requestPictureEdit(final File file) {
        Uri uri = getUriByFileProviderAuthority(file);
        int flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION;

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(uri, "image
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public Uri getMediaUri(final File file, final int mode) {
        Uri uri = MediaStore.Files.getContentUri("external");
        uri = (mode != 0) ? (mode == 1 ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI) : uri;

        Cursor cursor = null;
        try {
            cursor = _context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "= ?", new String[]{file.getAbsolutePath()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int mediaid = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                return Uri.withAppendedPath(uri, mediaid + "");
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    
    public void enableChromeCustomTabsForOtherBrowsers(final Intent customTabIntent) {
        String[] checkpkgs = new String[]{
                "com.android.chrome", "com.chrome.beta", "com.chrome.dev", "com.google.android.apps.chrome", "org.chromium.chrome",
                "org.mozilla.fennec_fdroid", "org.mozilla.firefox", "org.mozilla.firefox_beta", "org.mozilla.fennec_aurora",
                "org.mozilla.klar", "org.mozilla.focus",
        };

        
        PackageManager pm = _context.getPackageManager();
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com"));
        List<String> browsers = new ArrayList<>();
        for (ResolveInfo ri : pm.queryIntentActivities(urlIntent, 0)) {
            Intent i = new Intent("android.support.customtabs.action.CustomTabsService");
            i.setPackage(ri.activityInfo.packageName);
            if (pm.resolveService(i, 0) != null) {
                browsers.add(ri.activityInfo.packageName);
            }
        }

        
        ResolveInfo ri = pm.resolveActivity(urlIntent, 0);
        String userDefaultBrowser = (ri == null) ? null : ri.activityInfo.packageName;

        
        String pkg = null;
        if (browsers.isEmpty()) {
            pkg = null;
        } else if (browsers.size() == 1) {
            pkg = browsers.get(0);
        } else if (!TextUtils.isEmpty(userDefaultBrowser) && browsers.contains(userDefaultBrowser)) {
            pkg = userDefaultBrowser;
        } else {
            for (String checkpkg : checkpkgs) {
                if (browsers.contains(checkpkg)) {
                    pkg = checkpkg;
                    break;
                }
            }
            if (pkg == null && !browsers.isEmpty()) {
                pkg = browsers.get(0);
            }
        }
        if (pkg != null && customTabIntent != null) {
            customTabIntent.setPackage(pkg);
        }
    }

    
    public void requestStorageAccessFramework(final Activity... activity) {
        Activity a = greedyGetActivity(activity);
        if (a != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
            );
            a.startActivityForResult(intent, REQUEST_SAF);
        }
    }

    
    public Uri getStorageAccessFrameworkTreeUri() {
        String treeStr = PreferenceManager.getDefaultSharedPreferences(_context).getString(PREF_KEY__SAF_TREE_URI, null);
        if (!TextUtils.isEmpty(treeStr)) {
            try {
                return Uri.parse(treeStr);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    
    public File getStorageAccessFolder() {
        Uri safUri = getStorageAccessFrameworkTreeUri();
        if (safUri != null) {
            String safUriStr = safUri.toString();
            ContextUtils cu = new ContextUtils(_context);
            for (Pair<File, String> storage : cu.getStorages(false, true)) {
                @SuppressWarnings("ConstantConditions") String storageFolderName = storage.first.getName();
                if (safUriStr.contains(storageFolderName)) {
                    return storage.first;
                }
            }
            cu.freeContextRef();
        }
        return null;
    }

    
    public boolean isUnderStorageAccessFolder(final File file) {
        if (file != null) {
            
            if (file.canWrite()) {
                return false;
            }
            ContextUtils cu = new ContextUtils(_context);
            for (Pair<File, String> storage : cu.getStorages(false, true)) {
                if (file.getAbsolutePath().startsWith(storage.first.getAbsolutePath())) {
                    cu.freeContextRef();
                    return true;
                }
            }
            cu.freeContextRef();
        }
        return false;
    }

    
    private Activity greedyGetActivity(final Activity... activity) {
        if (activity != null && activity.length != 0 && activity[0] != null) {
            return activity[0];
        }
        if (_context instanceof Activity) {
            return (Activity) _context;
        }
        return null;
    }

    
    public boolean canWriteFile(final File file, final boolean isDir) {
        if (file == null) {
            return false;
        } else if (file.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())
                || file.getAbsolutePath().startsWith(_context.getFilesDir().getAbsolutePath())) {
            boolean s1 = isDir && file.getParentFile().canWrite();
            return !isDir && file.getParentFile() != null ? file.getParentFile().canWrite() : file.canWrite();
        } else {
            DocumentFile dof = getDocumentFile(file, isDir);
            return dof != null && dof.canWrite();
        }
    }

    
    @SuppressWarnings("RegExpRedundantEscape")
    public DocumentFile getDocumentFile(final File file, final boolean isDir) {
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            return DocumentFile.fromFile(file);
        }

        
        ContextUtils cu = new ContextUtils(_context);
        File baseFolderFile = cu.getStorageRootFolder(file);
        cu.freeContextRef();

        String baseFolder = baseFolderFile == null ? null : baseFolderFile.getAbsolutePath();
        boolean originalDirectory = false;
        if (baseFolder == null) {
            return null;
        }

        String relPath = null;
        try {
            String fullPath = file.getCanonicalPath();
            if (!baseFolder.equals(fullPath)) {
                relPath = fullPath.substring(baseFolder.length() + 1);
            } else {
                originalDirectory = true;
            }
        } catch (IOException e) {
            return null;
        } catch (Exception ignored) {
            originalDirectory = true;
        }
        Uri treeUri;
        if ((treeUri = getStorageAccessFrameworkTreeUri()) == null) {
            return null;
        }
        DocumentFile dof = DocumentFile.fromTreeUri(_context, treeUri);
        if (originalDirectory) {
            return dof;
        }
        String[] parts = relPath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDof = dof.findFile(parts[i]);
            if (nextDof == null) {
                try {
                    nextDof = ((i < parts.length - 1) || isDir) ? dof.createDirectory(parts[i]) : dof.createFile("image", parts[i]);
                } catch (Exception ignored) {
                    nextDof = null;
                }
            }
            dof = nextDof;
        }
        return dof;
    }

    public void showMountSdDialog(@StringRes final int title, @StringRes final int description, @DrawableRes final int mountDescriptionGraphic, final Activity... activityOrNull) {
        Activity activity = greedyGetActivity(activityOrNull);
        if (activity == null) {
            return;
        }

        
        ImageView imv = new ImageView(activity);
        imv.setImageResource(mountDescriptionGraphic);
        imv.setAdjustViewBounds(true);

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setView(imv);
        dialog.setTitle(title);
        dialog.setMessage(_context.getString(description) + "\n\n");
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> requestStorageAccessFramework(activity));
        AlertDialog dialogi = dialog.create();
        dialogi.show();
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "StatementWithEmptyBody"})
    public void writeFile(final File file, final boolean isDirectory, final Callback.a2<Boolean, FileOutputStream> writeFileCallback) {
        try {
            FileOutputStream fileOutputStream = null;
            ParcelFileDescriptor pfd = null;
            final boolean existingEmptyFile = file.canWrite() && file.length() < MIN_OVERWRITE_LENGTH;
            final boolean nonExistingCreatableFile = !file.exists() && file.getParentFile().canWrite();
            if (existingEmptyFile || nonExistingCreatableFile) {
                if (isDirectory) {
                    file.mkdirs();
                } else {
                    fileOutputStream = new FileOutputStream(file);
                }
            } else {
                DocumentFile dof = getDocumentFile(file, isDirectory);
                if (dof != null && dof.getUri() != null && dof.canWrite()) {
                    if (isDirectory) {
                        
                    } else {
                        pfd = _context.getContentResolver().openFileDescriptor(dof.getUri(), "rwt");
                        fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                    }
                }
            }
            if (writeFileCallback != null) {
                writeFileCallback.callback(fileOutputStream != null || (isDirectory && file.exists()), fileOutputStream);
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception ignored) {
                }
            }
            if (pfd != null) {
                pfd.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @SuppressWarnings("SimplifiableConditionalExpression")
    public void callTelephoneNumber(final String telNo, final boolean... directCall) {
        Activity activity = greedyGetActivity();
        if (activity == null) {
            throw new RuntimeException("Error: ShareUtil::callTelephoneNumber needs to be contstructed with activity context");
        }
        boolean ldirectCall = (directCall != null && directCall.length > 0) ? directCall[0] : true;


        if (android.os.Build.VERSION.SDK_INT >= 23 && ldirectCall && activity != null) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 4001);
                ldirectCall = false;
            } else {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + telNo));
                    activity.startActivity(callIntent);
                } catch (Exception ignored) {
                    ldirectCall = false;
                }
            }
        }
        
        if (!ldirectCall) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", telNo, null));
            activity.startActivity(intent);
        }
    }
}
