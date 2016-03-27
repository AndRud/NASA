package com.andrutyk.nasa.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by admin on 11.02.2016.
 */
public class ImageAdapter extends PagerAdapter {

    private ArrayList<View> views = new ArrayList<View>();

    public ImageAdapter(Context context){
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View imageLayout = views.get (position);
        container.addView(imageLayout);
        return imageLayout;
    }

    @Override
    public int getItemPosition(Object object) {
        int index = views.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public int addView(View view){
        return addView(view, views.size());
    }

    public int addView(View view, int position){
        views.add(position, view);
        return position;
    }

    public int removeView(ViewPager pager, View view){
        return removeView(pager, views.indexOf(view));
    }

    public int removeView(ViewPager pager, int position){
        pager.setAdapter(null);
        views.remove(position);
        pager.setAdapter(this);
        return position;
    }

    public View getView(int position){
        return views.get(position);
    }

    public View getViewByTag(String tag){
        for (View view : views){
            if (view.getTag().toString().equals(tag)){
                return view;
            }
        }
        return null;
    }
}
