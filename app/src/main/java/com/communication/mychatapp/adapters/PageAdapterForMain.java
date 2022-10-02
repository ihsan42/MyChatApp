package com.communication.mychatapp.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.communication.mychatapp.main_activity_tabpages.AllPersons;
import com.communication.mychatapp.main_activity_tabpages.Inbox;
import com.communication.mychatapp.main_activity_tabpages.MyPersons;

public class PageAdapterForMain extends FragmentPagerAdapter {

    public PageAdapterForMain(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new Inbox();
            case 1:
                return new MyPersons();
            case 2:
                return new AllPersons();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Mesajlar";
            case 1:
                return "Kişiler";
            case 2:
                return "Kişi Ara";
        }
        return null;
    }
}
