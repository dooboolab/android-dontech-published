package org.hyochan.dontech.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hyochan.dontech.R;
import org.hyochan.dontech.adapters.IconAdapter;
import org.hyochan.dontech.database.TableGagebu;
import org.hyochan.dontech.database.TableGagebuBook;
import org.hyochan.dontech.fragments_dialog.DialogBtn2;
import org.hyochan.dontech.models.GagebuBook;
import org.hyochan.dontech.models.IconMap;
import org.hyochan.dontech.utils.CustomToast;
import org.hyochan.dontech.functions.Function;
import org.hyochan.dontech.utils.MyLog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by hyochan on 2016. 10. 9..
 */

public class AddUpdateGagebuBookActivity extends AppCompatActivity {

    private final String TAG = "AddUpdateGagebuBookActivity";

    private Activity activity;
    private Context context;
    private ArrayList<IconMap> iconMaps;

    // 타이틀 뷰
    @BindView(R.id.rel_back)
    RelativeLayout relBack;
    @BindView(R.id.rel_title)
    RelativeLayout relTitle;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.rel_save)
    RelativeLayout relSave;
    @BindView(R.id.rel_delete)
    RelativeLayout relDelete;
    // 바디 뷰
    @BindView(R.id.btn_clear)
    ImageButton btnClear;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.grid_view)
    GridView gridView;

    private int selectedIconPos = -1;
    private String nameOfGagebu;
    private GagebuBook updateGagebu;
    private Intent intent;
    private String intentAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_gagebu_book);
        ButterKnife.bind(this);

        activity = this;
        context = this;
        intentAction = getIntent().getAction();

        // 롤리팝 이상이면 status bar 색상 변경
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSubPrimary));
        }

        // 제목 세팅
        relTitle.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSubPrimary));
        txtTitle.setText(getString(R.string.add_gagebu));

        // gridview 세팅
        gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        iconMaps = new ArrayList<>();
        iconMaps.add(new IconMap("ic_gagebu_card", R.drawable.ic_gagebu_card));
        iconMaps.add(new IconMap("ic_gagebu_male", R.drawable.ic_gagebu_male));
        iconMaps.add(new IconMap("ic_gagebu_female", R.drawable.ic_gagebu_female));
        iconMaps.add(new IconMap("ic_gagebu_group", R.drawable.ic_gagebu_group));
        iconMaps.add(new IconMap("ic_gagebu_heart", R.drawable.ic_gagebu_heart));
        iconMaps.add(new IconMap("ic_gagebu_money", R.drawable.ic_gagebu_money));
        iconMaps.add(new IconMap("ic_gagebu_star", R.drawable.ic_gagebu_star));
        iconMaps.add(new IconMap("ic_gagebu_money_hand", R.drawable.ic_gagebu_money_hand));
        iconMaps.add(new IconMap("ic_gagebu_shoppay_list", R.drawable.ic_gagebu_shoppay_list));
        iconMaps.add(new IconMap("ic_gagebu_shoppay_sale", R.drawable.ic_gagebu_shoppay_sale));
        iconMaps.add(new IconMap("ic_gagebu_shoppay_coin_hand", R.drawable.ic_gagebu_shoppay_coin_hand));
        iconMaps.add(new IconMap("ic_gagebu_shoppay_present", R.drawable.ic_gagebu_shoppay_present));
        iconMaps.add(new IconMap("ic_gagebu_shoppay_card", R.drawable.ic_gagebu_shoppay_card));
        iconMaps.add(new IconMap("ic_gagebu_shoppay_wallet_coin", R.drawable.ic_gagebu_shoppay_wallet_coin));
        iconMaps.add(new IconMap("ic_gagebu_shoppay_money", R.drawable.ic_gagebu_shoppay_money));
        iconMaps.add(new IconMap("ic_gagebu_money2", R.drawable.ic_gagebu_money2));
        iconMaps.add(new IconMap("ic_gagebu_money_graph", R.drawable.ic_gagebu_money_graph));
        iconMaps.add(new IconMap("ic_gagebu_dollar_light", R.drawable.ic_gagebu_dollar_light));
        IconAdapter iconAdapter = new IconAdapter(this, iconMaps);
        gridView.setAdapter(iconAdapter);

        // intentAction이 수정모드이면 아이콘 선택하면서 시작
        if(!Function.getInstance(context).isEmptyStr(intentAction) && intentAction.equals(Intent.ACTION_EDIT)){
            txtTitle.setText(getString(R.string.update_gagebu));
            relDelete.setVisibility(View.VISIBLE);

            nameOfGagebu = getIntent().getStringExtra("name");
            updateGagebu = TableGagebuBook.getInstance(context).selecyMyGagebu(nameOfGagebu);
            editName.setText(nameOfGagebu);
            for(int i = 0; i < iconMaps.size(); i++){
                if(getIntent().getStringExtra("icon_name").equals(iconMaps.get(i).getStr())){
                    gridView.setItemChecked(i, true);
                    selectedIconPos = i;
                }
            }
        }

    }

    @OnItemClick(R.id.grid_view)
    void onGridItemClick(AdapterView<?> parent, View view, int position, long id){
        // TODO : testing
        MyLog.d(TAG, "itemChecked : " + position);
        gridView.setItemChecked(position, true);
        selectedIconPos = position;
    }

    @OnClick(R.id.btn_clear) void onClickEditTextClear(){
        editName.setText("");
    }

    @OnClick({R.id.rel_back, R.id.rel_save, R.id.rel_delete})
    void onTitleClick(View view){
        int rowNum;
        switch (view.getId()){
            case R.id.rel_back:
                setResult(RESULT_CANCELED);
                onBackPressed();
                break;
            case R.id.rel_save:
                intent = new Intent();

                if(selectedIconPos == -1) {
                    CustomToast.getInstance(context).createToast(
                            getString(R.string.plz_select_icon));
                    return;
                } else if (Function.getInstance(context).isEmptyStr(editName.getText().toString())){
                    CustomToast.getInstance(context).createToast(
                            getString(R.string.plz_write_gagebu_name_you_wish_to_add));
                    return;
                }

                // 테이블에 값 넣기
                if(intentAction.equals(Intent.ACTION_INSERT)){
                    if(TableGagebuBook.getInstance(context).checkMyGagebuName(editName.getText().toString(), "") != 0){
                        CustomToast.getInstance(context).createToast(getString(R.string.cannot_create_same_gagebu_name));
                        return;
                    }
                    TableGagebuBook.getInstance(context).insertMyGagebu(
                            new GagebuBook(iconMaps.get(selectedIconPos).getStr(), editName.getText().toString(), String.valueOf(Calendar.getInstance().getTimeInMillis())));
                } else if(intentAction.equals(Intent.ACTION_EDIT)){
                    if(TableGagebuBook.getInstance(context).checkMyGagebuName(editName.getText().toString(), updateGagebu.getName()) != 0){
                        CustomToast.getInstance(context).createToast(getString(R.string.cannot_create_same_gagebu_name));
                        return;
                    }
                    updateGagebu.setIconName(iconMaps.get(selectedIconPos).getStr());
                    updateGagebu.setName(editName.getText().toString());
                    // GagebuBook 변경
                    TableGagebuBook.getInstance(context).updateMyGagebu(updateGagebu.get_id(), updateGagebu);
                    // Gagebu 이름 변경
                    TableGagebu.getInstance(context).gagebuBookHasUpdated(editName.getText().toString(), nameOfGagebu);

                    intent.putExtra("before_name", nameOfGagebu);
                }

                // intent.setAction(Intent.ACTION_DEFAULT);
                intent.setAction(intentAction); // 수정이면 edit 아니면 insert
                intent.putExtra("name", editName.getText().toString());
                setResult(RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.rel_delete:
                // 가계부가 하나 밖에 없으면 삭제 안됨
                if(TableGagebuBook.getInstance(context).countMyGagebus() == 1){
                    CustomToast.getInstance(context).createToast(getString(R.string.can_not_delete_last_one_gagebu));
                    return;
                }

                // 정말로 지울 건지 다이얼로그 띄우기
                Bundle args = new Bundle();
                args.putString("content",
                        getString(R.string.are_you_sure_you_want_to_delete_gagebu_book));
                final DialogBtn2 dialogBtn2 = new DialogBtn2();
                dialogBtn2.setArguments(args);
                dialogBtn2.show(getSupportFragmentManager(), "delete_gagebu");
                dialogBtn2.setDialogBtn2(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {// 취소
                                dialogBtn2.dismiss();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 가계부 지우기
                                TableGagebu.getInstance(context).deleteGagebuInBook(nameOfGagebu);
                                // 가계부 북 지우기
                                TableGagebuBook.getInstance(context).deleteMyGagebu(updateGagebu.get_id());
                                // TODO : 해당 가계부에 해당하는 가게부 사용내역 지우기

                                intent = new Intent();
                                intent.setAction(Intent.ACTION_DELETE);
                                intent.putExtra("name", nameOfGagebu);
                                setResult(RESULT_OK, intent);
                                dialogBtn2.dismiss();
                                onBackPressed();
                            }
                        }
                );
                break;
        }
    }
}