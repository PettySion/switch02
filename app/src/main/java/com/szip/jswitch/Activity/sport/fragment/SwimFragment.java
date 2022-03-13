package com.szip.jswitch.Activity.sport.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.DB.dbModel.SportData;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.View.SportReportView;

import java.util.Locale;

public class SwimFragment extends BaseFragment {

    private TextView timeTv,distanceTv,unitTv,kcalTv,sportTimeTv,averageTv1, averageTv2;
    private SportReportView tableView1,tableView2;
    private SportData sportData;

    private String[] heartArray = new String[0];
    private String[] speedPerHourArray = new String[0];

    public SwimFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_swim;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {
        timeTv = getView().findViewById(R.id.timeTv);
        sportTimeTv = getView().findViewById(R.id.sportTimeTv);
        distanceTv = getView().findViewById(R.id.distanceTv);
        unitTv = getView().findViewById(R.id.unitTv);
        kcalTv = getView().findViewById(R.id.kcalTv);
        averageTv1 = getView().findViewById(R.id.averageTv1);
        averageTv2 = getView().findViewById(R.id.averageTv2);
        tableView1 = getView().findViewById(R.id.tableView1);
        tableView2 = getView().findViewById(R.id.tableView2);
    }

    private void initData() {
        heartArray = sportData.getHeartArray().split(",");
        speedPerHourArray = sportData.getSpeedPerHourArray().split(",");

        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f",((sportData.calorie+55)/100)/10f));
        if (MyApplication.getInstance().getUserInfo().getUnit()==0){
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",((sportData.distance+5)/10)/100f));
            averageTv2.setText(String.format(Locale.ENGLISH,"%.1f",sportData.speedPerHour/10f));
            unitTv.setText("km");
        } else{
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.km2Miles(sportData.distance)));
            averageTv2.setText(String.format(Locale.ENGLISH,"%.1f",MathUitl.kmPerHour2MilesPerHour(sportData.speedPerHour)/10f));
            unitTv.setText("mile");
            speedPerHourArray = MathUitl.kmPerHour2MilesPerHour(speedPerHourArray);
            ((TextView)getView().findViewById(R.id.speedUnitTv)).setText("mile/h");
        }
        averageTv1.setText(sportData.heart+"");


        tableView1.addData(heartArray);
        tableView2.addData(speedPerHourArray);

        if(sportData.heart==0){
            getView().findViewById(R.id.heartLl).setVisibility(View.GONE);
        }
        if(sportData.speedPerHour==0){
            getView().findViewById(R.id.speedPerHourLl).setVisibility(View.GONE);
        }
    }


}
