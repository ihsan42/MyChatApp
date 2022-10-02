package com.communication.mychatapp.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.communication.mychatapp.admin_panel_tabpages.AllPersonsAdminFragment;
import com.communication.mychatapp.admin_panel_tabpages.SignUpRequestsFragment;

public class PageAdapterForAdminPanel extends FragmentPagerAdapter {

    public PageAdapterForAdminPanel(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new SignUpRequestsFragment();
            case 1:
                return new AllPersonsAdminFragment();
            /*case 2:
                return new AllPersons();*/
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Kayıt İstekleri";
            case 1:
                return "Tüm Kişiler";
            /*case 2:
                return "Kişi Ara";*/
        }
        return null;
    }
}
