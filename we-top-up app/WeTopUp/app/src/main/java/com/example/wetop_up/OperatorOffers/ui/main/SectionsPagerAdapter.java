package com.example.wetop_up.OperatorOffers.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.wetop_up.OperatorOffers.BundleOfferFrag;
import com.example.wetop_up.OperatorOffers.InternetOfferFrag;
import com.example.wetop_up.OperatorOffers.VoiceOfferFrag;
import com.example.wetop_up.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        if(position == 0){
//            return new InternetOfferFrag();
//        }else if(position == 1){
//            return new VoiceOfferFrag();
//        }
        switch (position){
            case 0:
                return new InternetOfferFrag();
            case 1:
                return new VoiceOfferFrag();
            case 2:
                return new BundleOfferFrag();
            default:
                return null;
        }
//        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}