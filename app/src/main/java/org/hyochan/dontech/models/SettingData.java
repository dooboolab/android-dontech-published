package org.hyochan.dontech.models;

/**
 * Created by hyochan on 2016. 10. 9..
 */

public class SettingData {

    // SettingActivity 번호
    public static final int POS_ANNOUNCE= 0;
    public static final int POS_FAQ= 1;
    public static final int POS_VERSION_INFO= 2;
    public static final int POS_SMS_PARSE= 3;

    public static final int TYPE_ARR = 1;
    public static final int TYPE_MORE = 2;
    public static final int TYPE_ARR_MORE = 3;
    public static final int TYPE_CHK = 4;
    public static final int TYPE_CHK_MORE = 5; // 체크랑 설명이랑 같이 있는거

    private int type;
    private String txt;
    private String txtMore;

    public SettingData(int type, String txt, String txtMore) {
        this.type = type;
        this.txt = txt;
        this.txtMore = txtMore;
    }

    public int getViewType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getTxtMore() {
        return txtMore;
    }

    public void setTxtMore(String txtMore) {
        this.txtMore = txtMore;
    }

}
