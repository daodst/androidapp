

package com.wallet.ctc.view.choosemorepic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.wallet.ctc.R;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.ui.me.about.LocalFile;
import com.wallet.ctc.ui.me.about.LocalImageHelper;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.view.TitleBarView;
import com.wallet.ctc.view.choosemorepic.utils.ExtraKey;

import java.util.List;



public class LocalAlbumDetail extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private TitleBarView titleBarView;
    private GridView gridView;
    private String folder;
    private LocalImageHelper helper = LocalImageHelper.getInstance();
    private List<LocalFile> checkedItems;
    private Intent intent;
    private int num = 0;
    @Override
    public int initContentView() {
        return R.layout.localalbumdetail_activity_wallet;
    }
    @Override
    public void initUiAndListener() {
        titleBarView = (TitleBarView) findViewById(R.id.title_bar);
        gridView = (GridView) findViewById(R.id.gridview);
    }

    @Override
    public void initData() {
        if (!LocalImageHelper.getInstance().isInited()) {
            finish();
            return;
        }
        intent = getIntent();
        num = intent.getIntExtra("num",9);
        titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                
                LocalImageHelper.getInstance().setResultOk(true);
                finish();
            }

            @Override
            public void rightClick() {
                LocalImageHelper.getInstance().setResultOk(true);
                finish();
            }
        });
        folder = getIntent().getExtras().getString(ExtraKey.LOCAL_FOLDER_NAME);
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                helper.initImage();
                
                final List<LocalFile> folders = helper.getFolder(folder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (folders != null) {
                            MyAdapter adapter = new MyAdapter(LocalAlbumDetail.this, folders);
                            titleBarView.setText(folder);
                            gridView.setAdapter(adapter);
                            
                            if (checkedItems.size() > 0) {
                                titleBarView.setRightText("(" + (checkedItems.size()) + "/"+num+")");
                                titleBarView.setRightTextOnclick(true);
                            } else {
                                titleBarView.setRightText("");
                                titleBarView.setRightTextOnclick(false);
                            }
                        }
                    }
                });
            }
        }).start();
        checkedItems = helper.getCheckedItems();
        LocalImageHelper.getInstance().setResultOk(false);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (!b) {
            if (checkedItems.contains(compoundButton.getTag())) {
                checkedItems.remove(compoundButton.getTag());
            }
        } else {
            if (!checkedItems.contains(compoundButton.getTag())) {
                if (checkedItems.size() >= num) {
                    Toast.makeText(this, ""+num+"", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                    return;
                }
                checkedItems.add((LocalFile) compoundButton.getTag());
            }
        }
        if (checkedItems.size() > 0) {
            titleBarView.setRightText("(" + (checkedItems.size()) + "/"+num+")");
            titleBarView.setRightTextOnclick(true);
        } else {

            titleBarView.setRightText("");
            titleBarView.setRightTextOnclick(false);

        }
    }

    public class MyAdapter extends BaseAdapter {
        private Context context;
        private List<LocalFile> paths;
        public MyAdapter(Context context, List<LocalFile> paths) {
            this.context = context;
            this.paths = paths;
        }

        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public LocalFile getItem(int i) {
            return paths.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.simple_list_item, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                viewHolder.checkBox.setOnCheckedChangeListener(LocalAlbumDetail.this);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            LocalFile localFile = paths.get(i);
            GlideUtil.showImgSD(context, localFile.getThumbnailUri(), viewHolder.imageView);
            viewHolder.checkBox.setTag(localFile);
            viewHolder.checkBox.setChecked(checkedItems.contains(localFile));
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
                }
            });
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            CheckBox checkBox;
        }
    }


}
