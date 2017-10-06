package com.vlusi.klintelligent.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.vlusi.klintelligent.Bean.GridItem;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.adapters.StickyGridAdapter2;
import com.vlusi.klintelligent.utils.MessageEvent;
import com.vlusi.klintelligent.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 相册界面
 * Created by 吴启 on 2017/7/16.
 */

public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {

    /*@InjectView(R.id.tv_select)
    TextView tvSelect;*/
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_cancel)
    TextView tvCancel;
    @InjectView(R.id.Selection_bar)
    RelativeLayout SelectionBar;
    @InjectView(R.id.asset_grid)
    StickyGridHeadersGridView mGridView;
    @InjectView(R.id.iv_delete)
    ImageButton ivDelete;
    private StickyGridAdapter2 mAdapter;
    private Context mContext;
    boolean checked;
    private List list;
    private List<String> pathList = new ArrayList<>();
    private int count = 0;
    private ContentValues values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        changeAppLanguage();
        setContentView(R.layout.activity_album);
        ButterKnife.inject(this);
        list = (List<Object>) getIntent().getSerializableExtra("list");
        if (list != null) {
            init(list);
        }
    }


    private void init(List list) {
        tvCancel.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        mAdapter = new StickyGridAdapter2(mContext, list, mGridView);
        mAdapter.notifyDataSetChanged();
        mGridView.setAdapter(mAdapter);
        mAdapter.setGuoListener(new StickyGridAdapter2.GridItems() {

            @Override
            public void onItem(int position, String path, int count) {
                if (!pathList.contains(path)) {
                    tvTitle.setText(" "+count+" ");
                    Log.i("hhh", "已经选中的路径：" + path);
                    pathList.add(path);
                } else {
                    for (int i = 0; i < pathList.size(); i++) {
                        if (path == pathList.get(i)) {
                            Log.i("hhh", "取消选中的路径：" + path);
                            pathList.remove(path);
                            tvTitle.setText(" "+count+" ");
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                Log.i("aaaa", "取消");
                cancel();
                break;
            case R.id.iv_delete:
                delete();
                Log.i("aaaa", "删除");
                break;
        }
    }

    /**
     * 删除
     */
    private void delete() {
        EventBus.getDefault().post(new MessageEvent(true));

        values = new ContentValues();
        for (int i = 0; i < pathList.size(); i++) {
            File file = new File(pathList.get(i));
            if (file.exists()) {
                file.delete();
            }
        }
        finish();
    }

    /**
     * 取消
     */
    private void cancel() {
        for (int i = 0; i < list.size(); i++) {
            GridItem gridItem = (GridItem) list.get(i);
            gridItem.setIschecks(false);
        }
        finish();
    }


    /**
     * 改变语言的切换
     */
    public void changeAppLanguage() {
        String lanString = "zh";
        //得到语言设置的返回值
        lanString = SPUtil.getString(mContext, "language", lanString);
        Configuration config = getResources().getConfiguration();//获得设置对象
        Resources resources = getResources();//获得res资源对象
        DisplayMetrics dm = resources.getDisplayMetrics();
        switch (lanString) {
            case "zh":
                config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文
                break;
            case "en":
                config.locale = Locale.ENGLISH;            //英文
                break;
        }
        resources.updateConfiguration(config, dm);
    }



}
