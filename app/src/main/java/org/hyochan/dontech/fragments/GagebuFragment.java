package org.hyochan.dontech.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;

import org.hyochan.dontech.R;
import org.hyochan.dontech.adapters.GagebuAdapter;
import org.hyochan.dontech.database.TableGagebu;
import org.hyochan.dontech.events.GagebuBookSelectedEvent;
import org.hyochan.dontech.events.GagebuBookUpdateEvent;
import org.hyochan.dontech.events.GagebuUpdateEvent;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.models.DateManager;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.utils.BusProvider;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GagebuFragment extends Fragment {

    private final String TAG = "GagebuFragment";

    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.rel_empty)
    RelativeLayout relEmpty;
    @BindView(R.id.img_erase)
    ImageView imgErase;

    private ArrayList<Gagebu> gagebus;
    private GagebuAdapter gagebuAdapter;


    public GagebuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @OnClick(R.id.img_erase) void onClick(){
        editSearch.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gagebu, null);
        ButterKnife.bind(this, view);

        editSearch.addTextChangedListener(filterTextWatcher);
        // 리사이클 뷰는 이걸 꼭 해줘야 child view가 생성된다.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setGagebu();

        return view;
    }

    private void setGagebu(){
        gagebus = TableGagebu.getInstance(getContext()).selectGagebuInBookInMonth(
                AppFunction.getInstance(getContext()).getCurrentGagebuBook());
        MyLog.d(TAG, "current gagebu : " + AppFunction.getInstance(getContext()).getCurrentGagebuBook() + ", size : " + gagebus.size());
        gagebuAdapter = new GagebuAdapter(getActivity(), gagebus);
        recyclerView.setAdapter(gagebuAdapter);

        if(gagebus.size() == 0){
            relEmpty.setVisibility(View.VISIBLE);
        } else {
            relEmpty.setVisibility(View.GONE);
        }
    }

    // 검색
    TextWatcher filterTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        public void afterTextChanged(Editable s) {
            gagebuAdapter.getFilter().filter(editSearch.getText().toString());
            if(editSearch.getText().toString() != null && editSearch.getText().toString().length() > 0){
                imgErase.setVisibility(View.VISIBLE);
            } else {
                imgErase.setVisibility(View.GONE);
            }
        }
    };

    @Subscribe
    public void gagebuBookUpdatedEvent(GagebuBookUpdateEvent event) {
/*
        if(event.getEvent().equals(Intent.ACTION_EDIT)){
        }
*/
        MyLog.d(TAG, "get current gagebu book : " + AppFunction.getInstance(getContext()).getCurrentGagebuBook());
        MyLog.d(TAG, "get event gagebu book : " + event.getName());
        if(event.getName().equals(AppFunction.getInstance(getContext()).getCurrentGagebuBook())){
            // 현재 선택된 가계부 이름이 변경되었으면 element 이름 변경
            gagebuAdapter.gagebuNameChanged(event.getName());
        }
        setGagebu();
    }

    @Subscribe
    public void gagebuSelectedEvent(GagebuBookSelectedEvent event){
        setGagebu();
    }

    @Subscribe
    public void gagebuUpdatedEvent(GagebuUpdateEvent event) {
        Gagebu gagebu = event.getGagebu();
        MyLog.d(TAG, "gageupUpdatedEvent");
        MyLog.d(TAG, "action : " + event.getAction());
        MyLog.d(TAG, "name : " + gagebu.getName());
        /** 현재 월에 해당되는 가계부만 업데이트 해준다. 이상하게 오류가 뜨는데 무시하자. **/
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(gagebu.getDate()));

        int selectedYear = DateManager.getInstance().getYear();
        int selectedMonth = DateManager.getInstance().getMonth();

        int gagebuYear = cal.get(Calendar.YEAR);
        int gagebuMonth = cal.get(Calendar.MONTH);

        MyLog.d(TAG, "year : " + gagebuYear + ", manager year : " + selectedYear);
        MyLog.d(TAG, "month : " + gagebuMonth + ", manager month : " + selectedMonth);
        if(gagebuYear == selectedYear && gagebuMonth == selectedMonth){
            MyLog.d(TAG, "gagebu date is current date");
            if(AppFunction.getInstance(getContext()).getCurrentGagebuBook().equals(gagebu.getName())){
                if (event.getAction().equals(Intent.ACTION_INSERT)){
                    gagebuAdapter.addGagebu(gagebu);
                } else if (event.getAction().equals(Intent.ACTION_EDIT)){
                    gagebuAdapter.updateGagebu(gagebu);
                } else if (event.getAction().equals(Intent.ACTION_DELETE)){
                    gagebuAdapter.deleteGagebu(gagebu.get_id());
                }
                if(gagebuAdapter.getItemCount() == 0){
                    relEmpty.setVisibility(View.VISIBLE);
                } else {
                    relEmpty.setVisibility(View.GONE);
                }
            }
        }
    }

}
