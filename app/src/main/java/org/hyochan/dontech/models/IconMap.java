package org.hyochan.dontech.models;

/**
 * Created by hyochan on 2016. 10. 8..
 *
 * 아이콘과 문자열을 매핑시켜주는 모델
 * IconAdapter에서 database에 icon명을 int형이 아닌 string으로 넣어주기 위해서 필요.
 */

public class IconMap {
    private String str;
    private int drawable;

    public IconMap(String str, int drawable) {
        this.str = str;
        this.drawable = drawable;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
