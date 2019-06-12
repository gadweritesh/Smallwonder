package com.relecotech.androidsparsh_smallwonder.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by amey on 3/07/2018.
 */
public class AssignmentPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mfragmentsList;
    private ArrayList<String> mfragmentsNameList;

    public AssignmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentsList, ArrayList<String> fragmentsNameList) {
        super(fm);
        mfragmentsList = fragmentsList;
        mfragmentsNameList = fragmentsNameList;
    }

    @Override
    public Fragment getItem(int position) {
        return mfragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return mfragmentsList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = " ";
        for (int titleLoop = 0; titleLoop < mfragmentsNameList.size(); titleLoop++) {
            title = mfragmentsNameList.get(position);
        }
        return title;
    }
}
