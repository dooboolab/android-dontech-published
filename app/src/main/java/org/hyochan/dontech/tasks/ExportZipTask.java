package org.hyochan.dontech.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.hyochan.dontech.R;
import org.hyochan.dontech.utils.ZipUtil;

/**
 * Created by hyochan on 2016-03-03.
 */


public class ExportZipTask extends AsyncTask<Void, String, String> {

    public interface OnTaskCompleted{
        void onTaskCompleted(String result);
    }

    private Context context;
    private OnTaskCompleted listener;

    private ProgressDialog pd;

    public ExportZipTask(Context context, OnTaskCompleted listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        // Show your progress bar here.
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(context.getResources().getString(R.string.exporting_zip));
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        return ZipUtil.getInstance(context).exportZip();
    }


    @Override
    protected void onPostExecute(String result) {
        // Hide the progress bar here.
        pd.dismiss();
        listener.onTaskCompleted(result);
    }
}