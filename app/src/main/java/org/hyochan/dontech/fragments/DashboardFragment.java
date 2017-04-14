package org.hyochan.dontech.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import org.hyochan.dontech.R;
import org.hyochan.dontech.adapters.DashboardAdapter;
import org.hyochan.dontech.database.TableGagebuBook;
import org.hyochan.dontech.events.DashboardSlideEvent;
import org.hyochan.dontech.events.GagebuBookUpdateEvent;
import org.hyochan.dontech.functions.AppFunction;
import org.hyochan.dontech.global_variables.MyNumber;
import org.hyochan.dontech.models.DashboardMenu;
import org.hyochan.dontech.models.GagebuBook;
import org.hyochan.dontech.utils.BusProvider;
import org.hyochan.dontech.functions.Function;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardFragment extends ListFragment{

    private final String TAG = "DashboardFragment";
    private ViewGroup header;
    @BindView(android.R.id.list) ListView listView;
    private DashboardAdapter dashboardAdapter;

    public DashboardFragment() {
        // Required empty public constructor
    }

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dashboard, null);
        ButterKnife.bind(this, view);

        // 1. setup data
        // 2. set adapter
        dashboardAdapter = new DashboardAdapter(getActivity(), getMenu());
        // 3. bind to listview
        listView.setAdapter(dashboardAdapter);

        // add header
        if (listView.getHeaderViewsCount() == 0) {
            header = (ViewGroup) inflater.inflate(R.layout.dashboard_header, null);
            listView.addHeaderView(header, null, false);
            listView.addFooterView(Function.getInstance(getContext()).getEmptyViewForListView(30));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);
        super.onDestroyView();
    }

    @Subscribe public void gagebuBookUpdatedEvent(GagebuBookUpdateEvent event) {
        // TODO: React to the event somehow!
        MyLog.d(TAG, "reload gagebu event : " + event.getEvent() + ", name : " + event.getName());
        dashboardAdapter = new DashboardAdapter(getActivity(), getMenu());
        listView.setAdapter(dashboardAdapter);
    }

    private ArrayList<DashboardMenu> getMenu(){
        ArrayList<DashboardMenu> arrMenu = new ArrayList<>();
        arrMenu.add(new DashboardMenu(MyNumber.DASHBOARD_LIST_TOPIC, getString(R.string.gagebu_list)));
        //// 저장된 가계부 추가 : 가변적
        ArrayList<GagebuBook> gagebuBooks = TableGagebuBook.getInstance(getContext()).selectMyGagebuBooks();
        String currentGagebuBook = null;
        for(GagebuBook gagebuBook : gagebuBooks){
            if(AppFunction.getInstance(getContext()).getCurrentGagebuBook().equals(gagebuBook.getName())){
                currentGagebuBook = gagebuBook.getName();
            }
            arrMenu.add(new DashboardMenu(MyNumber.DASHBOARD_LIST_MENU_MAIN,
                    gagebuBook.getIconName(), gagebuBook.getName(),
                    AppFunction.getInstance(getContext()).getCurrentGagebuBook().equals(gagebuBook.getName())));
        }
        arrMenu.add(new DashboardMenu(MyNumber.DASHBOARD_LIST_ADD));
        arrMenu.add(new DashboardMenu(MyNumber.DASHBOARD_LIST_TOPIC, getString(R.string.additional_function)));
        arrMenu.add(new DashboardMenu(MyNumber.DAHSBOARD_LIST_MENU_SUB, "ic_settings", getString(R.string.setting)));
        arrMenu.add(new DashboardMenu(MyNumber.DAHSBOARD_LIST_MENU_SUB, "ic_zip", getString(R.string.backup_and_restore)));
        arrMenu.add(new DashboardMenu(MyNumber.DAHSBOARD_LIST_MENU_SUB, "ic_share", getString(R.string.recommand_app)));

        /**
         * 가계부가 삭제되어서 선택된 가계부가 없으면 임의로 하나 설정
         */
        if(currentGagebuBook == null){
            arrMenu.get(1).setSelected(true);
            BusProvider.getInstance().post(new DashboardSlideEvent("open", arrMenu.get(1).getStrTxt()) );
        }

        return arrMenu;
    }

}
