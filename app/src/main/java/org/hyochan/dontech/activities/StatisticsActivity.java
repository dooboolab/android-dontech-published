package org.hyochan.dontech.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.hyochan.dontech.R;
import org.hyochan.dontech.database.TableGagebu;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.models.DateManager;
import org.hyochan.dontech.models.Gagebu;
import org.hyochan.dontech.utils.MyLog;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticsActivity extends AppCompatActivity {

    private final String TAG = " StatisticsActivity";

    private Activity activity;
    private Context context;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.lin_chart) LinearLayout linChart;
    @BindView(R.id.pie_chart) PieChart pieChart;

    private ArrayList<Gagebu> gagebus;
    private ArrayList<PieEntry> pieConsumeData;
    private ArrayList<PieEntry> pieIncomeData;
    private PieDataSet dataSet;

    // 총액
    private int totalIncome = 0;
    private int totalConsume = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

        activity = this;
        context = this;

        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.statistics));
        } catch (NullPointerException ne){
            MyLog.e(TAG, ne.getMessage());
        }


        MyLog.d(TAG, "year : " + DateManager.getInstance().getYear());
        MyLog.d(TAG, "month : " + DateManager.getInstance().getMonth());

        // 1. 데이터 세팅
        gagebus = TableGagebu.getInstance(context).selectGagebuInBookInMonth(
                AppFunction.getInstance(context).getCurrentGagebuBook());
        // MyLog.d(TAG, "current gagebu : " + AppFunction.getInstance(context).getCurrentGagebuBook() + ", size : " + gagebus.size());
        pieIncomeData = new ArrayList<>();
        pieConsumeData = new ArrayList<>();

        for(Gagebu gagebu : gagebus){
            int i = 0;
            if(gagebu.getMoney() < 0){
                totalConsume += gagebu.getMoney();
                for(PieEntry pieEntry : pieConsumeData){
                    if(pieEntry.getLabel().equals(gagebu.getCategory())){
                        pieEntry.setY(pieEntry.getY() + Math.abs(gagebu.getMoney()));
                        MyLog.d(TAG, "add money to category : " + gagebu.getCategory());
                        break;
                    }
                    i++;
                }
                if(pieConsumeData.size() == i){
                    pieConsumeData.add(new PieEntry( Math.abs(gagebu.getMoney()), gagebu.getCategory()));
                    MyLog.d(TAG, "add category : " + gagebu.getCategory());
                }
            } else if (gagebu.getMoney() > 0){
                totalIncome += gagebu.getMoney();
                for(PieEntry pieEntry : pieIncomeData){
                    if(pieEntry.getLabel().equals(gagebu.getCategory())){
                        pieEntry.setY(pieEntry.getY() + Math.abs(gagebu.getMoney()));
                        MyLog.d(TAG, "add money to category : " + gagebu.getCategory());
                        break;
                    }
                    i++;
                }
                if(pieIncomeData.size() == i){
                    pieIncomeData.add(new PieEntry( Math.abs(gagebu.getMoney()), gagebu.getCategory()));
                    MyLog.d(TAG, "add category : " + gagebu.getCategory());
                }
            }
        }

        // tests
        for(PieEntry pieEntry : pieIncomeData){
            MyLog.d(TAG, "label : " + pieEntry.getLabel() + ", value : " + pieEntry.getValue());
        }

        // 2. 그래프 그리기
        drawPieChart(pieConsumeData, getString(R.string.consume) + " : " + AppFunction.getInstance(context).convertMoneyToWonString(totalConsume));

    }

    private void drawPieChart(ArrayList<PieEntry> pieEntries, String description){

        dataSet = new PieDataSet(pieEntries, description);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // 색상 넣기
        ArrayList<Integer> colors = new ArrayList<>();
/*
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
*/

        colors.add(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        colors.add(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
        colors.add(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        colors.add(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
        colors.add(ContextCompat.getColor(context, android.R.color.holo_purple));
        colors.add(ContextCompat.getColor(context, R.color.pink_s));
        colors.add(ContextCompat.getColor(context, R.color.blue_s));
        colors.add(ContextCompat.getColor(context, R.color.colorSubPrimary));
        colors.add(ContextCompat.getColor(context, R.color.colorPrimary));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setUsePercentValues(false);
        pieChart.setDescription(description);
        // pieChart.setUsePercentValues(true);

        pieChart.invalidate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.statistics_option, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_income) {
            drawPieChart(pieIncomeData, getString(R.string.income) + " : " + AppFunction.getInstance(context).convertMoneyToWonString(totalIncome));
            return true;
        } else if (id == R.id.action_consume){
            drawPieChart(pieConsumeData, getString(R.string.consume) + " : " + AppFunction.getInstance(context).convertMoneyToWonString(totalConsume));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
