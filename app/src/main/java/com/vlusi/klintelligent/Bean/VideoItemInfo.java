package com.vlusi.klintelligent.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/1.
 */
public class VideoItemInfo implements Serializable {
    public String url;

    public VideoItemInfo( String url) {
        this.url = url;
    }
}
