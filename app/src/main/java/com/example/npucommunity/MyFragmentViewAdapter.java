package com.example.npucommunity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;



public class MyFragmentViewAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;

    public MyFragmentViewAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);;
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }


}
