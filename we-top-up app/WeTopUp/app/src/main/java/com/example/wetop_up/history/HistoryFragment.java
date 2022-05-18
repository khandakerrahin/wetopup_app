package com.example.wetop_up.history;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.wetop_up.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import static android.content.Context.MODE_PRIVATE;
import static com.example.wetop_up.URLHandler.SHARED_PREFS;

public class HistoryFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    TabItem tabEmergency, tabTopUp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        tabLayout = view.findViewById(R.id.hist_tabs);
        tabTopUp = view.findViewById(R.id.tabTopUp);
        tabEmergency = view.findViewById(R.id.tabEmergency);
        viewPager = view.findViewById(R.id.view_pager_hist);

        pageAdapter = new PageAdapter(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

//        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        final SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        final String stock_configuration = prefs.getString("stock_configuration","00000");

        try{
            // check if Stock View allowed for user
            // 0 = inactive; 1 = active;		bitwise	:		0 = visibility;	1 = history;	2 = topup;	3 = refill;	4 = transfer;
            if (stock_configuration.charAt(1) == '0') {
                tabLayout.removeTabAt(1);
            }
        } catch(Exception e){
            tabLayout.removeTabAt(1);
        }
        return view;
    }
}