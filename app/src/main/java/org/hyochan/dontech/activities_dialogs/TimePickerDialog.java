package org.hyochan.dontech.activities_dialogs;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import org.hyochan.dontech.R;

import java.util.Calendar;

public class TimePickerDialog extends AppCompatActivity implements View.OnClickListener{


    private TimePicker timePicker;
    private Button btnSelect;
    private Button btnCancel;

    private Intent intent;
    private int hour;
    private int minute;
    private int day;
    private boolean showDay;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select:
                intent = new Intent();
                int hour;
                int minute;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                intent.putExtra("hour", hour);
                intent.putExtra("minute", minute);
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
        setContentView(R.layout.dialog_time_picker);

        intent = getIntent();
        // show_day 파라미터가 true면 day 부분을 보여준다.

        hour = intent.getIntExtra("hour", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        minute = intent.getIntExtra("minute", Calendar.getInstance().get(Calendar.MINUTE));

        timePicker = (TimePicker) findViewById(R.id.time_picker);

        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }

        btnSelect = (Button) findViewById(R.id.btn_select);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnSelect.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
}
