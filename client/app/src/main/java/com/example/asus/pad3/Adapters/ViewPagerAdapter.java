package com.example.asus.pad3.Adapters;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.example.asus.pad3.BooksFragment;
import com.example.asus.pad3.R;
import com.example.asus.pad3.StudentFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    FragmentManager fragmentManager;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        if (position ==0) {
            return new StudentFragment();
        } else if (position == 1) {
            return new BooksFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}