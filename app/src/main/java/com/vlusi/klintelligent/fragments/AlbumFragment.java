package com.vlusi.klintelligent.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.vlusi.klintelligent.Bean.GridItem;
import com.vlusi.klintelligent.ImageLoadBrowser.PhotoBrowser;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.activities.AlbumActivity;
import com.vlusi.klintelligent.activities.Camera_Activity;
import com.vlusi.klintelligent.activities.VideoPlayActivity;
import com.vlusi.klintelligent.adapters.ImageScanner;
import com.vlusi.klintelligent.adapters.StickyGridAdapter1;
import com.vlusi.klintelligent.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 相册界面
 * Created by 吴启 on 2017/7/10.
 */
public class AlbumFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.tv_edit)
    TextView tvEdit;
    private Context mContext;
    public StickyGridAdapter1 mAdapter;
    private ImageScanner mScanner;
    private StickyGridHeadersGridView mGridView;
    private View view;
    private int Clickfailure = 0;
    private boolean loadOnce = true;
    private List<GridItem> AlbumList;
    private TextView aReturn;
    private boolean scanning=true;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        EventBus.getDefault().register(this);
        Log.i("Event_Bus","event:注册了");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        scanning=event.Mess;
        Log.i("Event_Bus","event:"+event.Mess);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album, container, false);
        initView(view);
        ButterKnife.inject(this, view);
        tvEdit.setOnClickListener(this);
        return view;
    }

    private void initView(View view) {
        mGridView = (StickyGridHeadersGridView) view.findViewById(R.id.asset_grid);
        aReturn = (TextView) view.findViewById(R.id.tv_select);
        aReturn.setOnClickListener(this);
    }

    private void RunSannerFile() {
        mScanner = new ImageScanner(getActivity());
        if (loadOnce) {
            loadOnce = false;
        }
        mScanner.scanImages(new ImageScanner.ScanCompleteCallBack() {
            @Override
            public void scanComplete(final List list) {
                AlbumList = list;
                mAdapter = new StickyGridAdapter1(getContext(), list, mGridView);
                mGridView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mAdapter.setGuoListener(new StickyGridAdapter1.GridItems() {
                    @Override
                    public void onItem(int position, String path, int rule) {
                        if (Clickfailure == 0) {
                            if (rule == 0) {
                                if (path.endsWith(".jpg")) {
                                    Intent intent = new Intent(getContext(), PhotoBrowser.class);
                                    intent.putExtra("albumList", (Serializable) AlbumList);
                                    intent.putExtra("pic_position", position);
                                    startActivity(intent);
                                    scanning=false;
                                } else {
                                    Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                                    intent.putExtra("video_path", path);
                                    startActivity(intent);
                                    scanning=false;

                                }
                            }
                        }

                    }
                });
            }

        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_edit:
                Intent intent = new Intent(mContext, AlbumActivity.class);
                intent.putExtra("list", (Serializable) AlbumList);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
                scanning=false;
                break;
            case R.id.tv_select:
                startActivity(new Intent(mContext, Camera_Activity.class));
                scanning=true;
                break;
        }

    }

    @Override
    public void onResume() {
        getActivity().overridePendingTransition(0, 0);
        super.onResume();
        Clickfailure = 0;
        if (scanning){
            RunSannerFile();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.i("Event_Bus","event:注销了");
    }
}
