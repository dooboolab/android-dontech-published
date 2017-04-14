package org.hyochan.dontech.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.database.TableGagebu;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.models.DaySet;
import org.hyochan.dontech.models.TotalIncomeConsume;
import org.hyochan.dontech.utils.AutoResizeTextView;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by hyochan on 2016. 10. 12..
 */

public class GridDayAdapter extends BaseAdapter {

    private static final String TAG = "GridDayAdapter";
    private Context context;
    private ArrayList<DaySet> listDay;
    private LayoutInflater layoutInflater;
    private ViewHolder viewHolder = null;

    private static class ViewHolder{
        private RelativeLayout relDay;
        public TextView txtDay;
        public AutoResizeTextView txtTotal;
        public AutoResizeTextView txtIncome;
        public AutoResizeTextView txtConsume;
    }

    public GridDayAdapter(Context context, ArrayList<DaySet> listDay) {
        this.context = context;
        this.listDay = listDay;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.adapter_grid_day, null);
            viewHolder.relDay = (RelativeLayout) convertView.findViewById(R.id.rel_day);
            viewHolder.txtDay = (TextView) convertView.findViewById(R.id.txt_day);
            viewHolder.txtTotal = (AutoResizeTextView) convertView.findViewById(R.id.txt_total);
            viewHolder.txtIncome= (AutoResizeTextView) convertView.findViewById(R.id.txt_income);
            viewHolder.txtConsume = (AutoResizeTextView) convertView.findViewById(R.id.txt_consume);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 이번달이거나 담달이면 좀 흐리게 보이기
        if(listDay.get(position).isPrevMon() || listDay.get(position).isNextMon()){
            viewHolder.relDay.setSelected(true);
            viewHolder.txtDay.setAlpha(0.35f);
        }else{
            viewHolder.relDay.setSelected(false);
            viewHolder.txtDay.setAlpha(1.0f);
        }

        viewHolder.txtDay.setText(String.valueOf(listDay.get(position).getCal().get(Calendar.DATE)));

        // 오늘이면 애니메이션 적용
        if(listDay.get(position).isToday()){
            viewHolder.txtDay.setAnimation(AnimationUtils.loadAnimation(context, R.anim.today_anim));
        }

        /**
         * TODO :
         * 1. txtToal, txtIncome, txtConsume 각각 동적으로 계산해서 표시하기
         */

        TotalIncomeConsume totalIncomeConsume = TableGagebu.getInstance(context).getThisDayTotalIncomConsume(
                AppFunction.getInstance(context).getCurrentGagebuBook(),
                listDay.get(position).getCal().getTimeInMillis()
        );
        if(totalIncomeConsume.getTotal() > 0) viewHolder.txtTotal.setTextColor(ContextCompat.getColor(context, R.color.income));
        else if(totalIncomeConsume.getTotal() < 0) viewHolder.txtTotal.setTextColor(ContextCompat.getColor(context, R.color.consume));
        else viewHolder.txtTotal.setTextColor(ContextCompat.getColor(context, R.color.colorSubPrimary));

        if(totalIncomeConsume.getTotal() == 0 && totalIncomeConsume.getConsume() == 0 && totalIncomeConsume.getIncome() == 0){
            viewHolder.txtTotal.setVisibility(View.INVISIBLE);
            viewHolder.txtIncome.setVisibility(View.INVISIBLE);
            viewHolder.txtConsume.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.txtTotal.setVisibility(View.VISIBLE);
            viewHolder.txtIncome.setVisibility(View.VISIBLE);
            viewHolder.txtConsume.setVisibility(View.VISIBLE);
        }

        viewHolder.txtTotal.setText(
                AppFunction.getInstance(context).putCommasToMoney(
                        totalIncomeConsume.getTotal()
                ));
        viewHolder.txtIncome.setText(
                AppFunction.getInstance(context).putCommasToMoney(
                        totalIncomeConsume.getIncome()
                ));
        viewHolder.txtConsume.setText(
                AppFunction.getInstance(context).putCommasToMoney(
                        totalIncomeConsume.getConsume()
                ));

        return convertView;
    }

    @Override
    public int getCount() {
        return listDay.size();
    }

    @Override
    public DaySet getItem(int position) {
        return listDay.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void drawAdapter(ArrayList<DaySet> arrDay){
        this.listDay = arrDay;
        notifyDataSetChanged();
    }
}
