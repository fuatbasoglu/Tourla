package com.fuatbasoglu.tourla.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.fuatbasoglu.tourla.fragments.Home;
import com.fuatbasoglu.tourla.fragments.Map;
import com.fuatbasoglu.tourla.fragments.Notification;
import com.fuatbasoglu.tourla.fragments.Profile;
import com.fuatbasoglu.tourla.fragments.Search;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int noOfTabs;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int noOfTabs){
        super(fm);
        this.noOfTabs = noOfTabs;

    }



    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                return new Home();


            case 1:
                return new  Search();


            case 2:
                return new  Map();


            case 3:
                return new  Notification();


            case 4:
                return new  Profile();


            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
