package com.benny.openlauncher.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.util.AttributeSet;
import android.view.View;

import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.manager.Setup;
import com.benny.openlauncher.model.Item;
import com.benny.openlauncher.notifications.MDNotificationListener;
import com.benny.openlauncher.util.AppManager;
import com.benny.openlauncher.util.DragAction;
import com.benny.openlauncher.util.DragHandler;
import com.benny.openlauncher.util.TabActionsHelper;
import com.benny.openlauncher.util.Tool;
import com.benny.openlauncher.viewutil.DesktopCallback;
import com.benny.openlauncher.viewutil.GroupDrawable;

public class AppItemView extends View implements Drawable.Callback, MDNotificationListener.NotificationCallback {
    private static final int MIN_ICON_TEXT_MARGIN = 8;
    private static final char ELLIPSIS = '…';

    private Drawable _icon = null;
    private String _label;
    private Paint _textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint _notifyTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint _notifyPaint = new Paint();
    private Rect _textContainer = new Rect(), testTextContainer = new Rect();
    private float _iconSize;
    private boolean _showLabel = true;
    private boolean _vibrateWhenLongPress;
    private float _labelHeight;
    private int _targetedWidth;
    private int _targetedHeightPadding;
    private float _heightPadding;

    private int _notificationCount = 0;

    public AppItemView(Context context) {
        this(context, null);
    }

    public AppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        _labelHeight = Tool.dp2px(14);
        _textPaint.setTextSize(Tool.sp2px(12));
        _textPaint.setColor(Color.WHITE);
        _notifyTextPaint.setColor(Color.WHITE);
        _notifyPaint.setColor(Color.RED);
    }

    public Drawable getIcon() {
        return _icon;
    }

    public void setIcon(Drawable icon) {
        setIcon(icon, true);
    }

    public void setIcon(Drawable icon, boolean useRound) {
        if (!useRound || (icon instanceof GroupDrawable)) {
            _icon = icon;
            return;
        }
        
        int w = icon.getIntrinsicWidth();
        int h = icon.getIntrinsicHeight();
        Bitmap.Config config = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, w, h);
        icon.draw(canvas);
        
        _icon = new RoundRectImageDrawable(bitmap);
    }

    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        _label = label;
    }

    public void notificationCallback(Integer count) {
        _notificationCount = count;

        invalidate();
    }

    public float getIconSize() {
        return _iconSize;
    }

    public void setIconSize(float iconSize) {
        _iconSize = iconSize;
    }

    public boolean getShowLabel() {
        return _showLabel;
    }

    public void setTargetedWidth(int width) {
        _targetedWidth = width;
    }

    public void setTargetedHeightPadding(int padding) {
        _targetedHeightPadding = padding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float mWidth = _iconSize;
        float mHeight = _iconSize + (_showLabel ? _labelHeight : 0);
        if (_targetedWidth != 0) {
            mWidth = _targetedWidth;
        }
        setMeasuredDimension((int) Math.ceil(mWidth), (int) Math.ceil((int) mHeight) + Tool.dp2px(2) + _targetedHeightPadding * 2);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        _heightPadding = (getHeight() - _iconSize - (_showLabel ? _labelHeight : 0)) / 2f;

        if (_label != null && _showLabel) {
            _textPaint.getTextBounds(_label, 0, _label.length(), _textContainer);
            int maxTextWidth = getWidth() - MIN_ICON_TEXT_MARGIN * 2;

            
            if (_textContainer.width() > maxTextWidth) {
                String testLabel = _label + ELLIPSIS;
                _textPaint.getTextBounds(testLabel, 0, testLabel.length(), testTextContainer);

                
                float characterSize = testTextContainer.width() / testLabel.length();
                int charsToTruncate = (int) ((testTextContainer.width() - maxTextWidth) / characterSize);

                canvas.drawText(_label.substring(0, _label.length() - charsToTruncate) + ELLIPSIS, MIN_ICON_TEXT_MARGIN, getHeight() - _heightPadding, _textPaint);
            } else {
                canvas.drawText(_label, (getWidth() - _textContainer.width()) / 2f, getHeight() - _heightPadding, _textPaint);
            }
        }

        
        if (_icon != null) {
            canvas.save();
            canvas.translate((getWidth() - _iconSize) / 2, _heightPadding);
            _icon.setBounds(0, 0, (int) _iconSize, (int) _iconSize);
            _icon.draw(canvas);

            if (_notificationCount > 0) {
                final String count = (_notificationCount > 99 ? "…" : String.valueOf(_notificationCount));
                float radius = _iconSize * .15f;
                canvas.drawCircle(_iconSize - radius, radius, radius, _notifyPaint);

                _notifyTextPaint.setTextSize((int) (radius * 1.5));

                canvas.drawText(count, _iconSize - radius - (_notifyTextPaint.measureText(count) / 2), radius - ((_notifyTextPaint.descent() + _notifyTextPaint.ascent()) / 2), _notifyTextPaint);
            }

            canvas.restore();
        }
    }

    public float getDrawIconTop() {
        return _heightPadding;
    }

    public float getDrawIconLeft() {
        return (getWidth() - _iconSize) / 2;
    }

    public static class Builder {
        
        
        
        AppItemView _view;

        public Builder(Context context) {
            _view = new AppItemView(context);
        }

        public Builder(AppItemView view) {
            _view = view;
        }

        public AppItemView getView() {
            return _view;
        }

        public Builder setAppItem(final Item item) {
            _view.setLabel(item.getLabel());
            _view.setIcon(item.getIcon());
            _view.setOnClickListener(v -> {
                Tool.createScaleInScaleOutAnim(_view, () -> {
                    Tool.startApp(_view.getContext(), AppManager.getInstance(_view.getContext()).findApp(item._intent), _view);
                });
            });
            return this;
        }

        public Builder setStableItem(final Item item) {
            
            _view.setLabel(getResourceId(_view.getContext(), item._label));
            _view.setIcon(item.getIcon(), false);
            _view.setOnClickListener(v -> {
                Tool.createScaleInScaleOutAnim(_view, () -> {
                    
                    TabActionsHelper.startIntent(item);
                });
            });
            return this;
        }

        public Builder setShortcutItem(final Item item) {
            _view.setLabel(item.getLabel());
            _view.setIcon(item.getIcon(), false);
            _view.setOnClickListener(v -> Tool.createScaleInScaleOutAnim(_view, () -> {
                Intent intent = item.getIntent();
                String id = intent.getStringExtra("shortcut_id");

                
                if (id == null || id.trim().isEmpty()) {
                    _view.getContext().startActivity(intent);
                }
                
                else {
                    LauncherApps launcherApps = (LauncherApps) _view.getContext().getSystemService(Context.LAUNCHER_APPS_SERVICE);
                    String packageName = intent.getPackage();
                    launcherApps.startShortcut(packageName, id, intent.getSourceBounds(), null, Process.myUserHandle());
                }
            }));
            return this;
        }

        public Builder setGroupItem(Context context, final DesktopCallback callback, final Item item) {
            _view.setLabel(item.getLabel());
            _view.setIcon(new GroupDrawable(context, item, Setup.appSettings().getIconSize()));
            _view.setOnClickListener(v -> {
                if (HomeActivity.Companion.getLauncher() != null && (HomeActivity.Companion.getLauncher()).getGroupPopup().showPopup(item, v, callback)) {
                    ((GroupDrawable) ((AppItemView) v).getIcon()).popUp();
                }
            });
            return this;
        }

        public Builder setActionItem(Item item) {
            _view.setLabel(item.getLabel());
            _view.setIcon(item._icon, false);
            _view.setOnClickListener(view -> {
                Tool.createScaleInScaleOutAnim(_view, () -> {
                    TabActionsHelper.action(_view, item._actionValue);
                });
            });
            return this;
        }

        public Builder withOnLongClick(final Item item, final DragAction.Action action, DesktopCallback desktopCallback) {
            _view.setOnLongClickListener(DragHandler.getLongClick(item, action, desktopCallback));
            return this;
        }

        public Builder setTextColor(int color) {
            _view._textPaint.setColor(color);
            return this;
        }

        public Builder setIconSize(int iconSize) {
            _view.setIconSize(Tool.dp2px(iconSize));
            return this;
        }

        public Builder setLabelVisibility(boolean visible) {
            _view._showLabel = visible;
            return this;
        }

        public Builder vibrateWhenLongPress(boolean vibrate) {
            _view._vibrateWhenLongPress = vibrate;
            return this;
        }

        private String getResourceId(Context context, String id) {
            
            int resourceId = context.getResources().getIdentifier(id, "string", context.getPackageName());  
            return context.getString(resourceId);
        }
    }
}
