package com.vlusi.klintelligent.adapters;

import com.vlusi.klintelligent.Bean.GridItem;

import java.util.Comparator;

public class YMComparator implements Comparator<GridItem> {

    @Override
    public int compare(GridItem o1, GridItem o2) {
        return o2.getTime().compareTo(o1.getTime());
    }

}
