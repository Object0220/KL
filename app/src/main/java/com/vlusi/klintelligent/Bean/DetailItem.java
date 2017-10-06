package com.vlusi.klintelligent.Bean;

import java.util.UUID;

/**
 * 详细的item
 * Created by dingjikerbo on 2016/9/5.
 */
public class DetailItem {

    public static final int TYPE_SERVICE = 0;  //服务类型
    public static final int TYPE_CHARACTER = 1; //

    public int type;

    public UUID uuid;

    public UUID service;

    public DetailItem(int type, UUID uuid, UUID service) {
        this.type = type;
        this.uuid = uuid;
        this.service = service;
    }
}
