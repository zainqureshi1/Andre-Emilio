package com.e2esp.andreemilio.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.e2esp.andreemilio.fragments.CategoriesFragment;
import com.e2esp.andreemilio.fragments.HomeFragment;

/**
 * Created by Ali on 8/9/2017.
 */

public class PageSliderAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs=2;

    public PageSliderAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new CategoriesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
