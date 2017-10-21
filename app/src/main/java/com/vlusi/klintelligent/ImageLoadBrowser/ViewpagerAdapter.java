package com.vlusi.klintelligent.ImageLoadBrowser;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class ViewpagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> allList = null;
    List<CharSequence> titles = null;



    public ViewpagerAdapter(android.support.v4.app.FragmentManager manager, List<Fragment> allList,
                            List<CharSequence> titles) {
        super(manager);
        this.allList = allList;
        this.titles = titles;

    }

    @Override
    public Fragment getItem(int arg0) {
        return allList.get(arg0);
    }

    @Override
    public int getCount() {
        return allList.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return titles.get(position);
    }

}
