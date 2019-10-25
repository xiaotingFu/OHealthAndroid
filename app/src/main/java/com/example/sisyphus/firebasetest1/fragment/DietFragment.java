package com.example.sisyphus.firebasetest1.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.adapter.ViewPagerAdapter;

public class DietFragment extends Fragment{


    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_diet, container, false);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        addTabs(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        return view;
    }
    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter3 = new ViewPagerAdapter(getChildFragmentManager());
        adapter3.addFrag(PageFragment.newInstance("Diet"), "Track");
        adapter3.addFrag(TrendFragment.newInstance("Diet"), "History+");
        viewPager.setAdapter(adapter3);
    }


}
