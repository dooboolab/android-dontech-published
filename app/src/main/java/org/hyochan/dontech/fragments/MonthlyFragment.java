package org.hyochan.dontech.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.hyochan.dontech.R;
import org.hyochan.dontech.adapters.GridDayAdapter;
import org.hyochan.dontech.database.TableGagebu;
import org.hyochan.dontech.events.GagebuBookSelectedEvent;
import org.hyochan.dontech.events.GagebuUpdateEvent;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.functions.Function;
import org.hyochan.dontech.models.DateManager;
import org.hyochan.dontech.models.DaySet;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.models.TotalIncomeConsume;
import org.hyochan.dontech.utils.BusProvider;
import org.hyochan.dontech.utils.CustomToast;
import org.hyochan.dontech.utils.MyGridView;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonthlyFragment extends Fragment {

    private final String TAG = "MonthlyFragment";

    @BindView(R.id.txt_total_amount) TextView txtTotalAmount;
    @BindView(R.id.txt_balance_carried_over) TextView txtBalanceCarriedOver;
    @BindView(R.id.txt_consume_amount) TextView txtConsumeAmount;
    @BindView(R.id.txt_income_amount) TextView txtIncomeAmount;
    @BindView(R.id.grid_days) MyGridView gridDay;


    private ArrayList<DaySet> listDaySet;
    private GridDayAdapter gridDayAdapter;

    public MonthlyFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_monthly, null);
        ButterKnife.bind(this, view);

        setTopLayoutElement();

        // 달력 그리기
        //gridview 요일 표시

        listDaySet = new ArrayList<>();
        gridDayAdapter = new GridDayAdapter(getContext(), listDaySet);
        // 2. gridDayAdapter
        gridDay.setAdapter(gridDayAdapter);
        DateManager.getInstance().fillCalendar(gridDayAdapter);

        /**
         * TODO :
         * 1. 총액, 수입, 이월, 지출 표시
         * 2. linDaySet에 itemclick listener 붙이기
         * 3. prevMonth를 눌렀을 때는 이전 달력으로 넘어감.
         * 4. nextMonth를 눌렀을 떄는 이 후 달력으로 넘어감
         * 5. 이번달 날짜를 누르면 해당 일에 쓴 내역 보기로 들어감. CurrentDayUsageActivity(?)
         */
        gridDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Calendar cal = gridDayAdapter.getItem(position).getCal();
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(Calendar.YEAR, DateManager.getInstance().getYear());
                selectedCal.set(Calendar.MONTH, DateManager.getInstance().getMonth());
                if(cal.before(selectedCal) && selectedCal.get(Calendar.MONTH) != cal.get(Calendar.MONTH)){
                    // 한달 전
                    MyLog.d(TAG, "한달 전");
                    DateManager.getInstance().setYear(cal.get(Calendar.YEAR));
                    DateManager.getInstance().setMonth(cal.get(Calendar.MONTH) + 1);

                    BusProvider.getInstance().post(new GagebuBookSelectedEvent(
                            AppFunction.getInstance(getContext()).getCurrentGagebuBook()));
                } else if (cal.after(selectedCal) && selectedCal.get(Calendar.MONTH) != cal.get(Calendar.MONTH)){
                    // 한달 후
                    MyLog.d(TAG, "한달 후");
                    DateManager.getInstance().setYear(cal.get(Calendar.YEAR));
                    DateManager.getInstance().setMonth(cal.get(Calendar.MONTH));
                    BusProvider.getInstance().post(new GagebuBookSelectedEvent(
                            AppFunction.getInstance(getContext()).getCurrentGagebuBook()));
                } else {
                    // TODO : 현재 날짜
                    CustomToast.getInstance(getContext()).createToast("일별 사용내역 보기는 다음 업데이트에 제공됩니다.");
                }
            }
        });

        return view;
    }

    private void setTopLayoutElement(){
        int year = DateManager.getInstance().getYear();
        int month = DateManager.getInstance().getMonth();
        long startDate = AppFunction.getInstance(getContext()).getMilisecDate(year, month, 1);
        long endDate = AppFunction.getInstance(getContext()).getMilisecDate(year, month+1, 1);
        // 총액
        TotalIncomeConsume totalIncomeConsume = TableGagebu.getInstance(getContext()).getThisMonthTotalIncomConsume(
                AppFunction.getInstance(getContext()).getCurrentGagebuBook(),
                startDate, endDate
        );

        // 총액 계산을 위해 이월금액 미리 받아놈
        int balance = TableGagebu.getInstance(getContext()).selectBalanceCarriedOver(
                AppFunction.getInstance(getContext()).getCurrentGagebuBook());

        MyLog.d(TAG, "total : " + totalIncomeConsume.getTotal() + ", balance : " + balance);
        totalIncomeConsume.setTotal(totalIncomeConsume.getTotal() + balance);

        if(totalIncomeConsume.getTotal() < 0 ) txtTotalAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.consume));
        else if(totalIncomeConsume.getTotal() > 0 ) txtTotalAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.income));
        if(balance < 0 ) txtBalanceCarriedOver.setTextColor(ContextCompat.getColor(getContext(), R.color.consume));
        else if(balance > 0 ) txtBalanceCarriedOver.setTextColor(ContextCompat.getColor(getContext(), R.color.income));

        // 총액 입력
        txtTotalAmount.setText(AppFunction.getInstance(getContext()).convertMoneyToWonString(totalIncomeConsume.getTotal()));
        // 이월 입력
        txtBalanceCarriedOver.setText(AppFunction.getInstance(getContext()).convertMoneyToWonString(balance));
        // 수입 입력
        txtIncomeAmount.setText(AppFunction.getInstance(getContext()).convertMoneyToWonString(
                totalIncomeConsume.getIncome()
        ));

        // 지출 입력
        txtConsumeAmount.setText(AppFunction.getInstance(getContext()).convertMoneyToWonString(
                totalIncomeConsume.getConsume()
        ));
    }

    @Subscribe
    public void gagebuSelectedEvent(GagebuBookSelectedEvent event){
        setTopLayoutElement();
        DateManager.getInstance().fillCalendar(gridDayAdapter);
    }


    @Subscribe
    public void gagebuUpdatedEvent(GagebuUpdateEvent event) {
        Gagebu gagebu = event.getGagebu();
        DateManager.getInstance().fillCalendar(gridDayAdapter);
        setTopLayoutElement();
        if(AppFunction.getInstance(getContext()).getCurrentGagebuBook().equals(gagebu.getName())){
            if(event.getAction().equals(Intent.ACTION_INSERT)){

            } else if (event.getAction().equals(Intent.ACTION_EDIT)){

            } else if (event.getAction().equals(Intent.ACTION_DELETE)){

            }
        }
    }
}
