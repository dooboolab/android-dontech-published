package org.hyochan.dontech.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.activities.AddUpdateGagebuBookActivity;
import org.hyochan.dontech.activities.BackupRestoreActivity;
import org.hyochan.dontech.activities.SettingActivity;
import org.hyochan.dontech.database.TableGagebuBook;
import org.hyochan.dontech.events.DashboardSlideEvent;
import org.hyochan.dontech.global_variables.MyNumber;
import org.hyochan.dontech.models.DashboardMenu;
import org.hyochan.dontech.utils.BusProvider;
import org.hyochan.dontech.utils.CustomToast;
import org.hyochan.dontech.functions.Function;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;

/**
 * Created by hyochan on 2016. 10. 6..
 */

public class DashboardAdapter extends BaseAdapter {

    private final String TAG = "DashboardAdapter";
    private final int ITEM_VIEW_TYPE_CNT = 4;

    private ViewElements viewElements;
    private Activity activity;
    private Context context;
    private ArrayList<DashboardMenu> arrMenu;
    private Intent intent;

    private LayoutInflater layoutInflater;

    private class ViewElements {
        RelativeLayout rel;
        LinearLayout lin;
        TextView txt;
        ImageView img;
        // 가계부
        ImageView imgEdit;
    }

    public DashboardAdapter(Activity activity, ArrayList<DashboardMenu> arrMenu) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.arrMenu = arrMenu;
    }

    @Override
    public int getCount() {
        return arrMenu.size();
    }

    @Override
    public DashboardMenu getItem(int i) {
        return arrMenu.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        // MyLog.d(TAG, "getView started");
        viewElements = new ViewElements();
        switch (arrMenu.get(i).getViewType()){
            case MyNumber.DASHBOARD_LIST_TOPIC:
                view = LayoutInflater.from(activity).inflate(R.layout.dashboard_list_topic, null);
                break;
            case MyNumber.DASHBOARD_LIST_MENU_MAIN:
                view = LayoutInflater.from(activity).inflate(R.layout.dashboard_menu_main, null);
                viewElements.img = (ImageView) view.findViewById(R.id.img);
                MyLog.d(TAG, "imgName : " + arrMenu.get(i).getImgName());
                viewElements.img.setImageResource(
                        context.getResources().getIdentifier(arrMenu.get(i).getImgName(), "drawable", context.getPackageName())
                );
                viewElements.imgEdit = (ImageView) view.findViewById(R.id.img_edit);
                viewElements.imgEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AddUpdateGagebuBookActivity.class);
                        intent.setAction(Intent.ACTION_EDIT);
                        intent.putExtra("name", arrMenu.get(i).getStrTxt());
                        intent.putExtra("icon_name", arrMenu.get(i).getImgName());
                        activity.startActivityForResult(intent, MyNumber.REQ_GAGEBU_BOOK_ADD_UPDATE);
                    }
                });
                viewElements.lin = (LinearLayout) view.findViewById(R.id.lin);
                viewElements.lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyLog.d(TAG, "click position : " + i);
                        for(DashboardMenu menu : arrMenu){
                            if(menu.getViewType() == MyNumber.DASHBOARD_LIST_MENU_MAIN)
                                menu.setSelected(false);
                        }
                        arrMenu.get(i).setSelected(true);
                        for(DashboardMenu menu : arrMenu){
                            if(menu.getViewType() == MyNumber.DASHBOARD_LIST_MENU_MAIN)
                                MyLog.d(TAG, "menu : " + menu.getStrTxt() + ", selected : " + menu.isSelected());
                        }
                        BusProvider.getInstance().post(new DashboardSlideEvent("close", arrMenu.get(i).getStrTxt()) );
                        notifyDataSetChanged();
                    }
                });
                if(arrMenu.get(i).isSelected()) {
                    // MyLog.d(TAG, "selected : " + arrMenu.get(i).getStrTxt());
                    viewElements.lin.setSelected(true);
                }
                else viewElements.lin.setSelected(false);
                break;
            case MyNumber.DAHSBOARD_LIST_MENU_SUB:
                view = LayoutInflater.from(activity).inflate(R.layout.dashboard_menu_sub, null);
                viewElements.img = (ImageView) view.findViewById(R.id.img);
                viewElements.img.setImageResource(
                        context.getResources().getIdentifier(arrMenu.get(i).getImgName(), "drawable", context.getPackageName())
                );
                viewElements.rel = (RelativeLayout) view.findViewById(R.id.rel);
                viewElements.rel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /** 환경설정 **/
                        if(arrMenu.get(i).getStrTxt().equals(context.getString(R.string.setting))){
                            intent = new Intent(context, SettingActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intent);
                        }
                        /** 백웝 / 복원 **/
                        else if(arrMenu.get(i).getStrTxt().equals(context.getString(R.string.backup_and_restore))){
                            intent = new Intent(context, BackupRestoreActivity.class);
                            activity.startActivityForResult(intent, MyNumber.REQ_RESTORE_ACT);
                        }
                        /** 앱 추천 **/
                        else if(arrMenu.get(i).getStrTxt().equals(context.getString(R.string.recommand_app))){
                            final Intent recommendIntent = new Intent(Intent.ACTION_SEND);
                            recommendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.recommand_app));
                            String strBody = context.getString(R.string.recommand_app_description);
                            strBody = strBody + "https://play.google.com/store/apps/details?id=org.hyochan.dontech";
                            recommendIntent.putExtra(Intent.EXTRA_TEXT, strBody);
                            recommendIntent.setType("text/plain");
                            intent = Intent.createChooser(recommendIntent, context.getString(R.string.recommand_app));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intent);
                        }
                    }
                });
                break;
            case MyNumber.DASHBOARD_LIST_ADD:
                view = LayoutInflater.from(activity).inflate(R.layout.dashboard_menu_add, null);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 가계부 10개 이상 생성 불가
                        if (TableGagebuBook.getInstance(context).countMyGagebus() == 10){
                            CustomToast.getInstance(context).createToast(context.getString(R.string.cannot_create_gagebu_more_than_10));
                            return;
                        }
                        Intent intent = new Intent(context, AddUpdateGagebuBookActivity.class);
                        intent.setAction(Intent.ACTION_INSERT);
                        activity.startActivityForResult(intent, MyNumber.REQ_GAGEBU_BOOK_ADD_UPDATE);
                    }
                });
                break;
        }

        viewElements.txt = (TextView) view.findViewById(R.id.txt);

        // MyLog.d(TAG, "icon : " + arrMenu.get(i).getImgName());

        if(!Function.getInstance(context).isEmptyStr(arrMenu.get(i).getImgName())){
            viewElements.img.setImageResource(
                    context.getResources().getIdentifier(arrMenu.get(i).getImgName(), "drawable", context.getPackageName())
            );
        }
        viewElements.txt.setText(arrMenu.get(i).getStrTxt());

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_CNT;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return arrMenu.get(position).getViewType();
    }
}
