package org.hyochan.dontech.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.hyochan.dontech.R;
import org.hyochan.dontech.functions.Function;


/**
 * Created by hyochan on 2016-03-03.
 */


public class SaveImgTask extends AsyncTask<Void, String, String> {

    public interface OnTaskCompleted{
        void onTaskCompleted(String result);
    }

    private Context context;
    private String name;
    private Bitmap bitmap;
    private OnTaskCompleted listener;

    private ProgressDialog pd;

    public SaveImgTask(Context context, String name, Bitmap bitmap, OnTaskCompleted listener){
        this.context = context;
        this.name = name;
        this.bitmap = bitmap;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        // Show your progress bar here.
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(context.getResources().getString(R.string.saving_img));
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        if(bitmap != null && name != null)
            return Function.getInstance(context).saveImgToAppStorage(bitmap, name);
        else
            return null;
    }


    @Override
    protected void onPostExecute(String result) {
        // Hide the progress bar here.
        pd.dismiss();
        listener.onTaskCompleted(result);
    }
}