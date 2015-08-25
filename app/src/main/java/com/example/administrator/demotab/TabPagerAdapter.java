package com.example.administrator.demotab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by lidongyang on 2015/7/26.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragList;

    public TabPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragList) {
        super(fm);
        this.mFragList = fragList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragList.get(position);
    }

    @Override
    public int getCount() {
        return mFragList == null ? 0 : mFragList.size();
    }
}

