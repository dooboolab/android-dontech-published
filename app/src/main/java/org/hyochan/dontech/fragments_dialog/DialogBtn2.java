package org.hyochan.dontech.fragments_dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.hyochan.dontech.R;

/**
 * Created by hyochan on 3/29/15.
 */
public class DialogBtn2 extends DialogFragment {

    private String TAG = "DialogBtn2";

    // components for view
    private TextView txtContent;
    private Button btn1;
    private Button btn2;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    public DialogBtn2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogBtnAnimation;

        View view = inflater.inflate(R.layout.dialog_btn2, container);
        txtContent = (TextView) view.findViewById(R.id.txt_content);
        if(getArguments().getString("content") != null)
            txtContent.setText(getArguments().getString("content"));
        if(getArguments().getCharSequence("html") != null){
            if(getArguments().getCharSequence("html").equals("restore")){
                txtContent.setText(getText(R.string.html_ask_before_restore));
            } else if(getArguments().getCharSequence("html").equals("backup")){
                txtContent.setText(getText(R.string.html_ask_before_backup));
            }
        }
        btn1 = (Button) view.findViewById(R.id.btn1);
        btn2 = (Button) view.findViewById(R.id.btn2);
        btn1.setOnClickListener(mLeftClickListener);
        btn2.setOnClickListener(mRightClickListener);

        return view;
    }


    public void setDialogBtn2(View.OnClickListener btn1, View.OnClickListener btn2) {
        mLeftClickListener = btn1;
        mRightClickListener = btn2;
    }

}

