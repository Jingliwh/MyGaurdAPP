package com.my.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.activiy.R;

/**
 * Created by c_ljf on 17-1-16.
 */
public class MyGridItemAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private String[] mNameList ;
    private int[]  mDrawableList;
    private Context mContext;

    public MyGridItemAdapter(Context mContext, String[] mNameList, int[] mDrawableList) {
        this.mContext = mContext;
        this.mNameList = mNameList;
        this.mDrawableList = mDrawableList;
         mInflater= LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mNameList.length;
    }

    @Override
    public Object getItem(int position) {
        return mNameList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //找到布局
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item, null);
            TextView  tv_item= (TextView) convertView.findViewById(R.id.tv_item_function);
            ImageView iv_item= (ImageView) convertView.findViewById(R.id.iv_item_function);
            tv_item.setText(mNameList[position]);
            iv_item.setImageResource(mDrawableList[position]);

            return convertView;
        }
        return convertView;
    }
}
