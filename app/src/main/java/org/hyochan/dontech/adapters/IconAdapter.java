package org.hyochan.dontech.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.models.IconMap;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hyochan on 2016. 10. 8..
 */

public class IconAdapter extends BaseAdapter {

    private final String TAG = "IconAdapter";
    private Activity activity;
    private Context context;
    private ViewHolder viewHolder;

    private ArrayList<IconMap> arrIconMap;

    public IconAdapter(Activity activity, ArrayList<IconMap> arrIconMap) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.arrIconMap = arrIconMap;
    }

    @Override
    public int getCount() {
        return arrIconMap.size();
    }

    @Override
    public IconMap getItem(int position) {
        return arrIconMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_icon, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.img.setImageResource(getItem(position).getDrawable());
        return convertView;
    }
}
