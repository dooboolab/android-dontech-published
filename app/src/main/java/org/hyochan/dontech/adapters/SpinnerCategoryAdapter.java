package org.hyochan.dontech.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.models.GagebuCategory;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyochan on 2016-02-08.
 */
public class SpinnerCategoryAdapter extends BaseAdapter {

    private final String TAG = "SpinnerCategoryAdapter";
    private Context context;
    private ArrayList<GagebuCategory> arrCategory;
    private LayoutInflater layoutInflater;


    public SpinnerCategoryAdapter(Context context, ArrayList<GagebuCategory> arrCategory) {
        this.context = context;
        this.arrCategory = arrCategory;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View spinnerLayout = layoutInflater.inflate(R.layout.spinner_category_layout, parent, false);
        TextView txt=(TextView) spinnerLayout.findViewById(R.id.txt);
        txt.setText(arrCategory.get(position).getCategory());

        ImageView img = (ImageView) spinnerLayout.findViewById(R.id.img);
        img.setImageResource(
                context.getResources().getIdentifier(arrCategory.get(position).getIconName(), "drawable", context.getPackageName()));
        return spinnerLayout;
    }

    @Override
    public int getCount() {
        return arrCategory.size();
    }

    @Override
    public GagebuCategory getItem(int i) {
        return arrCategory.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}