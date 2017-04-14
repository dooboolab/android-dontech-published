package org.hyochan.dontech.customs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import org.hyochan.dontech.R;


/**
 * Created by hyochan on 11/16/15.
 */
public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        setContentView(R.layout.custom_progress_dialog); // 다이얼로그에 박을 레이아웃

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0.2f);
        setCanceledOnTouchOutside(false);

    }
}