package org.hyochan.dontech.fragments_dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hyochan.dontech.R;

/**
 * Created by hyochan on 2016-03-08.
 */
public class DialogPhotoActPicker extends DialogFragment {

    // components for view
    private TextView txtContent;
    private ImageButton imgBtn1;
    private ImageButton imgBtn2;

    private View.OnClickListener imgBtn1Listener;
    private View.OnClickListener imgBtn2Listener;

    public DialogPhotoActPicker() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogBtnAnimation;

        View view = inflater.inflate(R.layout.dialog_photo_act_picker, container);
        txtContent = (TextView) view.findViewById(R.id.txt_content);
        txtContent.setText(getArguments().getString("content"));
        imgBtn1 = (ImageButton) view.findViewById(R.id.img_btn1);
        imgBtn2 = (ImageButton) view.findViewById(R.id.img_btn2);
        imgBtn1.setOnClickListener(imgBtn1Listener);
        imgBtn2.setOnClickListener(imgBtn2Listener);

        return view;
    }

    public void setDialogBtn(View.OnClickListener img_btn1, View.OnClickListener img_btn2) {
        imgBtn1Listener = img_btn1;
        imgBtn2Listener = img_btn2;
    }
}
