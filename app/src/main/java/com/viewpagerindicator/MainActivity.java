package com.viewpagerindicator;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmoothPagerIndicator indicator = findViewById(R.id.indicator);
        ViewPager viewPager = findViewById(R.id.view_pager);
        List<ImageView> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ImageView iv = new ImageView(this);
            iv.setBackgroundColor(Color.BLACK);
            list.add(iv);
        }
        viewPager.setAdapter(new ViewPagerAdapter(list));
        viewPager.setCurrentItem(2);

        indicator.setViewPager(viewPager);
    }


    private class ViewPagerAdapter extends PagerAdapter {

        private List<ImageView> viewList;

        public ViewPagerAdapter(List<ImageView> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }
}
