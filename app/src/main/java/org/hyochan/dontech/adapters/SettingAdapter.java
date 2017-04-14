package org.hyochan.dontech.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.models.SettingData;
import org.hyochan.dontech.preferences.AppPref;

import java.util.ArrayList;

/**
 * Created by hyochan on 2016. 10. 9..
 */

public class SettingAdapter extends ArrayAdapter<SettingData> {

    private static final String TAG = "CustomAdapter";
    private Activity activity;
    private Context context;
    private ArrayList<SettingData> arrSetting;


    public SettingAdapter(Activity activity, int resource, ArrayList<SettingData> arrSetting) {
        super(activity, resource, arrSetting);
        this.activity = activity;
        this.context = activity;
        this.arrSetting = arrSetting;
    }

    class ViewHolder {
        TextView txt;
        TextView txtMore;
        ImageView imgProfile;
        ImageView imgArrow;
        ImageView imgChk;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.adapter_setting, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txt = (TextView) convertView.findViewById(R.id.txt);
            viewHolder.txtMore = (TextView) convertView.findViewById(R.id.txt_more);
            viewHolder.imgProfile = (ImageView) convertView.findViewById(R.id.img_profile);
            viewHolder.imgArrow = (ImageView) convertView.findViewById(R.id.img_arrow);
            viewHolder.imgChk = (ImageView) convertView.findViewById(R.id.img_chk);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        switch (parent.getId()) {
            case R.id.list_setting:
                switch (arrSetting.get(position).getViewType()) {
                    // hide array imgs in version info list
                    case SettingData.TYPE_ARR:
                        viewHolder.imgArrow.setVisibility(View.VISIBLE);
                        viewHolder.txtMore.setVisibility(View.GONE);
                        viewHolder.imgChk.setVisibility(View.GONE);
                        break;
                    case SettingData.TYPE_MORE:
                        viewHolder.imgArrow.setVisibility(View.GONE);
                        viewHolder.txtMore.setVisibility(View.VISIBLE);
                        viewHolder.imgChk.setVisibility(View.GONE);
                        viewHolder.txt.setEnabled(false);
                        break;
                    case SettingData.TYPE_ARR_MORE:
                        viewHolder.imgArrow.setVisibility(View.VISIBLE);
                        viewHolder.txtMore.setVisibility(View.VISIBLE);
                        viewHolder.imgChk.setVisibility(View.GONE);
                        break;
                    case SettingData.TYPE_CHK:
                        viewHolder.imgArrow.setVisibility(View.GONE);
                        viewHolder.txtMore.setVisibility(View.GONE);
                        viewHolder.imgChk.setVisibility(View.VISIBLE);
                        break;
                    case SettingData.TYPE_CHK_MORE:
                        viewHolder.imgArrow.setVisibility(View.GONE);
                        viewHolder.imgChk.setVisibility(View.VISIBLE);
                        if(position == SettingData.POS_SMS_PARSE){
                            if (AppPref.getInstance(context).getValue(AppPref.SMS_PARSE, false)){
                                viewHolder.imgChk.setSelected(true);
                                viewHolder.txtMore.setVisibility(View.VISIBLE);
                            } else {
                                viewHolder.imgChk.setSelected(false);
                                viewHolder.txtMore.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
        }
        viewHolder.txt.setText(arrSetting.get(position).getTxt());
        viewHolder.txtMore.setText(arrSetting.get(position).getTxtMore());

        return convertView;
    }

    @Override
    public int getCount() {
        return arrSetting.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public SettingData getItem(int position) {
        return arrSetting.get(position);
    }
}
