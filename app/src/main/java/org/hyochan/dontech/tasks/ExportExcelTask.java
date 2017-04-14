package org.hyochan.dontech.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.hyochan.dontech.R;
import org.hyochan.dontech.utils.ExcelUtil;

/**
 * Created by hyochan on 2016-03-03.
 */


public class ExportExcelTask extends AsyncTask<Void, String, String> {

    public interface OnTaskCompleted{
        void onTaskCompleted(String result);
    }

    private Context context;
    private OnTaskCompleted listener;

    private ProgressDialog pd;

    public ExportExcelTask(Context context, OnTaskCompleted listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        // Show your progress bar here.
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(context.getResources().getString(R.string.exporting_excel));
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        return ExcelUtil.getInstance(context).exportExcel();
    }


    @Override
    protected void onPostExecute(String result) {
        // Hide the progress bar here.
        pd.dismiss();
        listener.onTaskCompleted(result);
    }
}