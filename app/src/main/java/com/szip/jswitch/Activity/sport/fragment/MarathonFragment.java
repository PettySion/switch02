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
import com.szip.jswitch.View.SportSpeedView;

import java.util.Locale;

public class MarathonFragment extends BaseFragment {


    private TextView timeTv,distanceTv,unitTv,kcalTv,sportTimeTv, averageTv1, averageTv2,averageTv3,averageTv4;
    private SportReportView tableView1, tableView2,tableView3;
    private SportData sportData;
    private SportSpeedView sportSpeed;

    private String[] heartArray = new String[0];
    private String[] strideArray = new String[0];
    private String[] speedArray = new String[0];
    private String[] altitudeArray = new String[0];

    public MarathonFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_marathon;
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
        averageTv3 = getView().findViewById(R.id.averageTv3);
        averageTv4 = getView().findViewById(R.id.averageTv4);
        tableView1 = getView().findViewById(R.id.tableView1);
        tableView2 = getView().findViewById(R.id.tableView2);
        tableView3 = getView().findViewById(R.id.tableView3);
        sportSpeed = getView().findViewById(R.id.sportSpeed);
    }

    private void initData() {
        heartArray = sportData.getHeartArray().split(",");
        strideArray = sportData.getStrideArray().split(",");
        speedArray = sportData.getSpeedArray().split(",");
        altitudeArray = sportData.getAltitudeArray().split(",");

        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        if (MyApplication.getInstance().getUserInfo().getUnit()==0){
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",((sportData.distance+5)/10)/100f));
            unitTv.setText("km");
        } else{
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.km2Miles(sportData.distance)));
            unitTv.setText("mile");
            ((TextView)getView().findViewById(R.id.speedUnitTv)).setText("mile/h");
        }
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f",((sportData.calorie+55)/100)/10f));
        averageTv1.setText(sportData.heart+"");
        averageTv2.setText(sportData.stride+"");
        averageTv3.setText(sportData.height+"");
        averageTv4.setText(String.format(Locale.ENGLISH,"%02d'%02d''",sportData.speed/60,sportData.speed%60));
        tableView1.addData(heartArray);
        tableView2.addData(strideArray);
        tableView3.addData(altitudeArray);
        sportSpeed.addData(speedArray);

        if(sportData.heart==0){
            getView().findViewById(R.id.heartLl).setVisibility(View.GONE);
        }
        if(sportData.height==0){
            getView().findViewById(R.id.altitudeLl).setVisibility(View.GONE);
        }
        if(sportData.stride==0){
            getView().findViewById(R.id.strideLl).setVisibility(View.GONE);
        }
        if(sportData.speed==0){
            getView().findViewById(R.id.speedLl).setVisibility(View.GONE);
        }
    }
}
