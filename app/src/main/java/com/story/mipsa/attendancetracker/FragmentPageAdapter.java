package com.story.mipsa.attendancetracker;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentPageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentArrayList =  new ArrayList<>();
    private final List<String> fragmentTitleList =  new ArrayList<>();

    public FragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    private void addFragment(Fragment fragment, String title){
        fragmentArrayList.add(fragment);
        fragmentTitleList.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
}
