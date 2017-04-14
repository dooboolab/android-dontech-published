package org.hyochan.dontech.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.activities.AddUpdateGagebuActivity;
import org.hyochan.dontech.database.TableGagebuCategory;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.functions.Function;
import org.hyochan.dontech.global_variables.MyNumber;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.utils.MyLog;
import org.hyochan.dontech.utils.SoundSearcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hyochan on 2016. 10. 10..
 */

public class GagebuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private String TAG = "GagebuAdapter";
    private Activity activity;
    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<Gagebu> gagebus;
    private ArrayList<Gagebu> gagebusNotFiltered;

    // FILTER
    private Filter filter;

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.lin) LinearLayout lin;
        @BindView(R.id.img_category) ImageView imgCategory;
        @BindView(R.id.txt_cost) TextView txtCost;
        @BindView(R.id.txt_datetime) TextView txtDateTime;
        @BindView(R.id.txt_detail) TextView txtDetail;
        @BindView(R.id.txt_location) TextView txtLocation;
        @BindView(R.id.img_thumb) ImageView imgThumb;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public GagebuAdapter(Activity activity, ArrayList<Gagebu> gagebus) {
        this.activity = activity;
        this.context = activity;
        this.gagebus = gagebus;
        this.gagebusNotFiltered = gagebus;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.adapter_gagebu, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    /** 가계부 동적 추가 **/
    public void addGagebu(Gagebu gagebu){
        MyLog.d(TAG, "add gagebu");
        if (gagebus.size() == 0) {
            gagebus.add(gagebu);
            notifyItemInserted(getItemCount());
        } else {
            for(int i=0; i<=getItemCount(); i++){ // date가 가장 크면 마지막에 추가 그래서 조건이 <=
                if(i == getItemCount()){
                    gagebus.add(i, gagebu);
                    gagebusNotFiltered = gagebus;
                    notifyItemInserted(i);
                    break;
                }
                else if(Long.valueOf(gagebus.get(i).getDate()) > Long.valueOf(gagebu.getDate())){
                    gagebus.add(i, gagebu);
                    gagebusNotFiltered = gagebus;
                    notifyItemInserted(i);
                    break;
                }
            }
        }
    }

    /** 가계부 동적 삭제 **/
    public void deleteGagebu(int _id){
        for(int i=0; i<gagebus.size(); i++){
            if(gagebus.get(i).get_id() == _id){
                gagebus.remove(i);
                gagebusNotFiltered = gagebus;
                notifyItemRemoved(i);
                break;
            }
        }
    }

    /** 가계부 동적 수정 **/
    public void updateGagebu(Gagebu gagebu){
        for(int i=0; i<gagebus.size(); i++){
            if(gagebus.get(i).get_id() == gagebu.get_id()){
                gagebus.get(i).updateAll(gagebu);
                gagebusNotFiltered = gagebus;
                notifyItemChanged(i);
                break;
            }
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Gagebu gagebu = gagebus.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(gagebu.getDate()));

        if (holder instanceof ListItemViewHolder){
            final ListItemViewHolder listHolder = (ListItemViewHolder) holder;

            listHolder.txtDateTime.setText(cal.get(Calendar.DAY_OF_MONTH) + context.getString(R.string.day) + " - " +
                    String.format(Locale.KOREA, "%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.format(Locale.KOREA, "%02d", cal.get(Calendar.MINUTE)));

            listHolder.imgCategory.setImageResource(
                    context.getResources().getIdentifier(
                            TableGagebuCategory.getInstance(context).getIconName(gagebu.getCategory()),
                            "drawable", context.getPackageName())
            );
            listHolder.txtCost.setText(
                    AppFunction.getInstance(context).convertMoneyToWonString(Math.abs(gagebu.getMoney())));
            if(gagebu.getMoney() < 0)
                listHolder.txtCost.setTextColor(ContextCompat.getColor(context, R.color.consume));
            else
                listHolder.txtCost.setTextColor(ContextCompat.getColor(context, R.color.income));

            if(Function.getInstance(context).isEmptyStr(gagebu.getDetail())){
                listHolder.txtDetail.setVisibility(View.GONE);
            } else{
                listHolder.txtDetail.setVisibility(View.VISIBLE);
                listHolder.txtDetail.setText(gagebu.getDetail());
            }

            if (Function.getInstance(context).isEmptyStr(gagebu.getAddress())) {
                listHolder.txtLocation.setVisibility(View.GONE);
            } else {
                listHolder.txtLocation.setVisibility(View.VISIBLE);
                listHolder.txtLocation.setText(gagebu.getAddress());
            }

            listHolder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyLog.d(TAG, "onclick : " + position);
                    Intent intent = new Intent(context, AddUpdateGagebuActivity.class);
                    intent.putExtra("gagebu", gagebu);
                    intent.setAction(Intent.ACTION_EDIT);
                    activity.startActivityForResult(intent,  MyNumber.REQ_GAGEBU_ADD_UPDATE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return gagebus.size();
    }

/*
    뷰 타입이 여러개일 떄 쓰는거
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }
*/

    public Gagebu getItem(int position){
        return gagebus.get(position);
    }

    public void gagebuNameChanged(String name){
        for (Gagebu gagebu : gagebus){
            gagebu.setName(name);
        }
        gagebusNotFiltered = gagebus;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new SearchFilter();
        return filter;
    }

    // FILTERABLE
    private class SearchFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread
            FilterResults results = new FilterResults();
            ArrayList<Gagebu> filteredGagebu = new ArrayList<>();

            if(constraint != null && constraint.toString().length() > 0) { // 검색 텍스트 있음

                MyLog.d(TAG, "text : " + constraint.toString());

                for (Gagebu gagebu : gagebusNotFiltered){
                    MyLog.d(TAG, "constraint : " + constraint.toString());
                    if(
                            SoundSearcher.getInstance().matchString(gagebu.getCategory(), constraint.toString()) ||
                                    SoundSearcher.getInstance().matchString(gagebu.getDetail(), constraint.toString()) ||
                                    SoundSearcher.getInstance().matchString(gagebu.getAddress(), constraint.toString()) ||
                                    SoundSearcher.getInstance().matchString(String.valueOf(gagebu.getMoney()), constraint.toString())) {
                        filteredGagebu.add(gagebu);
                    }
                    MyLog.d(TAG, "filtered memoData : " + filteredGagebu.size());
                }
                results.count = filteredGagebu.size();
                results.values = filteredGagebu;
            } else {
                results.count = gagebusNotFiltered.size();
                results.values = gagebusNotFiltered;
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults result) {
            // NOTE: this function is *always* called from the UI thread.
            gagebus = (ArrayList<Gagebu>) result.values;
            notifyDataSetChanged();
        }
    }
}
