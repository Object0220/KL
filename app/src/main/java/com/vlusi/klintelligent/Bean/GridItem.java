package com.vlusi.klintelligent.Bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class GridItem implements Serializable {
    /**
     * 图片的路径
     */

    private String path;
    /**
     * 图片加入手机中的时间，只取了年月日
     */
    private String time;
    /**
     * 每个Item对应的HeaderId
     */
    private int section;

    /**
     * 视频略缩图
     */
    private Bitmap VideoBitmap;

    public Bitmap getVideoPic() {
        return VideoBitmap;
    }

    public void setVideoPic(Bitmap videoPic) {
        VideoBitmap = videoPic;
    }

    /**
     * 图片缩略图
     */
    private Bitmap PicBitmap;


    public Bitmap getPic() {
        return PicBitmap;
    }

    public void setPic(Bitmap Bitmap) {
        this.PicBitmap = Bitmap;
    }

    /**
     * 选项 选择
     */
    private boolean ischecks;
    public boolean ischecks() {
        return ischecks;
    }

    public void setIschecks(boolean ischecks) {
        this.ischecks = ischecks;
    }

    /**
     * 视频或者图片
     */
     private  boolean isMp4orPng=false;

    public boolean getisMp4orPng() {
        return isMp4orPng;
    }

    public void setMp4orPng(boolean mp4orPng) {
        isMp4orPng = mp4orPng;
    }

    /**
     * 视频图片的缩略图路径
     */
   private String thumbPath;

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public GridItem(String path, String time, Bitmap PicBitmap, Bitmap VideoBitmap,String thumbPath,boolean isMp4orPng) {
        super();
        this.path = path;
        this.time = time;
        this.PicBitmap=PicBitmap;
        this.VideoBitmap=VideoBitmap;
        this.thumbPath=thumbPath;
        this.isMp4orPng=isMp4orPng;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

}
