package com.relecotech.androidsparsh_smallwonder.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.relecotech.androidsparsh_smallwonder.fragments.ClassSchedule_Friday;
import com.relecotech.androidsparsh_smallwonder.fragments.ClassSchedule_Monday;
import com.relecotech.androidsparsh_smallwonder.fragments.ClassSchedule_Saturday;
import com.relecotech.androidsparsh_smallwonder.fragments.ClassSchedule_Thursday;
import com.relecotech.androidsparsh_smallwonder.fragments.ClassSchedule_Tuesday;
import com.relecotech.androidsparsh_smallwonder.fragments.ClassSchedule_Wednesday;

public class ClassSchedulePagerAdapter extends FragmentStatePagerAdapter {
    int mTabNo;

    public ClassSchedulePagerAdapter(FragmentManager fm, int TabNo) {
        super(fm);
        mTabNo = TabNo;
    }

    @Override
    public Fragment getItem(int position) {

        // Fragment frag = null;
        switch (position) {
            case 0:
                return new ClassSchedule_Monday();
            case 1:
                return new ClassSchedule_Tuesday();
            case 2:
                return new ClassSchedule_Wednesday();
            case 3:
                return new ClassSchedule_Thursday();
            case 4:
                return new ClassSchedule_Friday();
            case 5:
                return new ClassSchedule_Saturday();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTabNo;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = " ";
        switch (position) {
            case 0:
                title = "MON";
                break;
            case 1:
                title = "TUE";
                break;
            case 2:
                title = "WED";
                break;
            case 3:
                title = "THU";
                break;
            case 4:
                title = "FRI";
                break;
            case 5:
                title = "SAT";
                break;
        }

        return title;
    }
}