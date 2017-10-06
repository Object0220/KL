package com.vlusi.klintelligent.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.vlusi.klintelligent.Bean.GridItem;
import com.vlusi.klintelligent.Bean.Constant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by qi022 on 2017/7/12.
 * 图片视频扫描器
 */
public class ImageScanner {
    private Context mContext;
    private Cursor mCursor;
    private List<GridItem> mGirdList = new ArrayList<>();
    private static int section = 1;
    private  List<String> listTimes =new ArrayList<>();  //时间戳
    private Map<String, Integer> sectionMap = new HashMap<>();

    /**
     * 构造
     * @param context
     */
    public ImageScanner(Context context) {
        this.mContext = context;

    }

    private void init() {
        String path =  Constant.filefolderpath ;
        //selection: 指定查询条件
        //MediaStore.MediaColumns.DATA 字段存的就是图片的绝对路径，
        String data = MediaStore.MediaColumns.DATA;
        String selection = data + " like ?";

        //设定查询目录,定义selectionArgs：
        String[] selectionArgs = {path + "%"};   //视频和图片的绝对路径+%

        //查看指定文件夹里面的所有文件
        //第一步：得到内容解析器，
        //第二部：通过内容解析器查询操作
        //第三部：指定查询条件
        mCursor = mContext.getContentResolver().query(MediaStore.Files.getContentUri("external"), null,
                selection, selectionArgs, null);

    }


    /**
     * 扫描完成之后的回调接口
     */
    public  interface ScanCompleteCallBack {
     //    void scanComplete(Cursor cursor);
        void scanComplete(List mGirdList);
    }


    /**
     * 利用ContentProvider扫描手机中的图片，将扫描的Cursor回调到ScanCompleteCallBack 接口的scanComplete方法中，此方法在运行在子线程中
     */
    public void scanImages(final ScanCompleteCallBack callback) {
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                callback.scanComplete((List) msg.obj);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                init();
                while (mCursor.moveToNext()) {
                  //   获取图片或视频的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    // Log.e("获取图片或视频的路径" + path);
                    File file=new File(path);
                    if (file.exists()){   //只有路径存在的情况下才获取时间戳
                        long times = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                        if(!listTimes.contains(times+"")&&times!=0){
                            //添加到集合
                            if (path.endsWith(".mp4")&&file.exists()){
                                String s = paserTimeToYM(times);
                                GridItem mGridItem = new GridItem(path, s,null,null,null,true);
                                mGirdList.add(mGridItem);
                            }
                            if (path.endsWith("jpg")&&file.exists()){
                                GridItem mGridItem = new GridItem(path, paserTimeToYM(times),null,null,null,false);
                                mGirdList.add(mGridItem);
                            }
                            listTimes.add(times+"");
                        }
                    }
                    for (ListIterator<GridItem> it = mGirdList.listIterator(); it.hasNext(); ) {
                        GridItem mGridItem = it.next();
                        String ym = mGridItem.getTime().substring(0, 11);
                        if (!sectionMap.containsKey(ym)) {
                            mGridItem.setSection(section);
                            sectionMap.put(ym, section);
                            section++;
                        } else {
                            mGridItem.setSection(sectionMap.get(ym));
                        }
                    }

                }

                mCursor.close();
                Collections.sort(mGirdList, new YMComparator());
                Message msg = mHandler.obtainMessage();
                msg.obj =mGirdList ;
                mHandler.sendMessage(msg);

            }
        }).start();

    }

    public static String paserTimeToYM(long time) {
        //时区      亚洲上海
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        //简单的日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");
        return format.format(new Date(time * 1000L));
    }


}
