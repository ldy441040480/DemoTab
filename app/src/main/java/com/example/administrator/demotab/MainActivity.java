package com.example.administrator.demotab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ldy.scale.indicator.ScaleTabLayout;

import java.util.ArrayList;

/**
 * Created by lidongyang on 2015/7/26.
 */
public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private ScaleTabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Fragment> list = new ArrayList<Fragment>();
        list.add(ItemFragment.newInstance("Fuck One"));
        list.add(ItemFragment.newInstance("Fuck Two"));
        list.add(ItemFragment.newInstance("Fuck Three"));
        list.add(ItemFragment.newInstance("Fuck Four"));

        mTabLayout = (ScaleTabLayout) findViewById(R.id.layout_tab);
        mViewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        mViewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), list));
        mTabLayout.setViewPager(mViewPager);

        mTabLayout.setOnScalePageChangeListener(new ScaleTabLayout.OnScalePageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setOnTabItemClickListener(new ScaleTabLayout.OnTabItemClickListener() {
            @Override
            public void onTabClick(View view, int position) {

            }
        });
    }
}
