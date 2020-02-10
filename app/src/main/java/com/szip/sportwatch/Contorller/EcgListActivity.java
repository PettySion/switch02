package com.szip.sportwatch.Contorller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.szip.sportwatch.Adapter.EcgDataAdapter;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Interface.CalendarListener;
import com.szip.sportwatch.Model.DrawDataBean;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CalendarPicker;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class EcgListActivity extends BaseActivity implements View.OnClickListener{

    private ListView listView;
    private EcgDataAdapter ecgDataAdapter;
    private ArrayList<DrawDataBean> dataList;
    public long reportDate = DateUtil.getTimeOfToday();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ecg_list);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(this);
        findViewById(R.id.image2).setOnClickListener(this);
    }

    private void initData() {
        dataList =  LoadDataUtil.newInstance().getEcgDataList(reportDate);
    }

    private void updateView() {
        ((TextView)findViewById(R.id.timeTv)).setText(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM"));
        ecgDataAdapter.setList(dataList);
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(EcgListActivity.this,true);
        listView = findViewById(R.id.ecgListView);
        ((TextView)findViewById(R.id.timeTv)).setText(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM"));
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.ecgReport));

        ecgDataAdapter = new EcgDataAdapter(dataList,this);
        listView.setAdapter(ecgDataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EcgListActivity.this,EcgDataActivity.class);
                intent.putExtra("name",((MyApplication)getApplication()).getUserInfo().getUserName());
                intent.putExtra("average",dataList.get(position).getValue());
                intent.putExtra("max",dataList.get(position).getValue1());
                intent.putExtra("min",dataList.get(position).getValue2());
                intent.putExtra("time",dataList.get(position).getTime());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                finish();
                break;
            case R.id.image2:
                CalendarPicker.getInstance()
                        .enableAnimation(true)
                        .setFragmentManager(getSupportFragmentManager())
                        .setAnimationStyle(R.style.CustomAnim)
                        .setFlag(5)
                        .setDate(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM-dd"))
                        .setCalendarListener(new CalendarListener() {
                            @Override
                            public void onClickDate(String date) {
                                reportDate = DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd");
                                dataList =  LoadDataUtil.newInstance().getEcgDataList(reportDate);
                                updateView();
                            }
                        })
                        .show();
                break;
        }
    }
}