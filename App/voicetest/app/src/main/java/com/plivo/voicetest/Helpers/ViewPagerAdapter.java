package com.plivo.voicetest.Helpers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.plivo.voicetest.Activities.SipContacts;
import com.plivo.voicetest.Activities.PhoneContacts;
import com.plivo.voicetest.Activities.RecentCalls;

/**
 * Created by Siva on 14/06/17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter{

    final int PAGE_COUNT = 3;
    // Tab Titles
    private String tabtitles[] = new String[] { "Recents", "Contacts", "SIP" };
    Context context;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open FragmentTab1.java
            case 0:
                RecentCalls fragmenttab1 = new RecentCalls();
                return fragmenttab1;

            // Open FragmentTab2.java
            case 1:
                PhoneContacts fragmenttab2 = new PhoneContacts();
                return fragmenttab2;

            // Open FragmentTab3.java
            case 2:
                SipContacts fragmenttab3 = new SipContacts();
                return fragmenttab3;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
