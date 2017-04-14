package org.hyochan.dontech.activities_dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import org.hyochan.dontech.R;

import java.util.Calendar;


public class DatePickerDialog extends Activity implements View.OnClickListener{

    private DatePicker datePicker;
    private Button btnSelect;
    private Button btnCancel;

    private Intent intent;
    private int year;
    private int month;
    private int day;
    private boolean showDay;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_select:
                intent = new Intent();
                intent.putExtra("year", datePicker.getYear());
                intent.putExtra("month", datePicker.getMonth());
                intent.putExtra("day", datePicker.getDayOfMonth());
                setResult(RESULT_OK, intent);
                break;
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                break;
        }
        finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_datepicker);

        intent = getIntent();
        // show_day 파라미터가 true면 day 부분을 보여준다.

        year = intent.getIntExtra("year", Calendar.getInstance().get(Calendar.YEAR));
        month = intent.getIntExtra("month", Calendar.getInstance().get(Calendar.MONTH));

        showDay = intent.getBooleanExtra("show_day", true);
        day = intent.getIntExtra("day", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePicker = (DatePicker) findViewById(R.id.date_picker);
        datePicker.init(year, month, day, null);

        btnSelect = (Button) findViewById(R.id.btn_select);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnSelect.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if(!showDay){
            LinearLayout ll = (LinearLayout) datePicker.getChildAt(0);
            LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
            ll2.getChildAt(2).setVisibility(View.GONE);
        } else {
            LinearLayout ll = (LinearLayout)datePicker.getChildAt(0);
            LinearLayout ll2 = (LinearLayout)ll.getChildAt(0);
            ll2.getChildAt(2).setVisibility(View.VISIBLE);
        }
    }
}
