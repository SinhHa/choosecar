package com.hasbrain.chooseyourcar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.View;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 10/19/15.
 */
public class CarDetailActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 13;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollpage);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(getIntent().getExtras().getInt("position"));
        mPager.setPageMargin(120);



    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0 || mPager.getCurrentItem()==getIntent().getExtras().getInt("position")) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            if(position > 13){
                position = position -13;
            }
            bundle.putInt("position", position );
           CarDetailFragment cardetail = new CarDetailFragment();
            cardetail.setArguments(bundle);
           return cardetail;
        }
        @Override
        public float getPageWidth(int position) {
            return (1f);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
