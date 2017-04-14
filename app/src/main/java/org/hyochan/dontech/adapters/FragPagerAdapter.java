package org.hyochan.dontech.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.hyochan.dontech.R;

import java.util.List;


public class FragPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<Fragment> fragments;
    private String tabTitles[];
/*
    private int[] imageResId = {
            R.drawable.ic_one,
            R.drawable.ic_two,
            R.drawable.ic_three
    };
*/


    public FragPagerAdapter(FragmentManager fm, Context context, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        this.context = context;

        tabTitles = new String[] {
                context.getString(R.string.monthly),
                context.getString(R.string.usage)
        };
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        if(fragments == null) return 0;
        else return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        // put image in tab layout
/*
        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        SpannableString sb = new SpannableString("   " + tabTitles[position]); // add image + text
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
*/

        return tabTitles[position];
    }
}
