package com.szip.jswitch.Fragment.ReportFragment.bloodPressure;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.szip.jswitch.Activity.report.ReportActivity;
import com.szip.jswitch.Adapter.BloodAdapter;
import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.DB.LoadDataUtil;
import com.szip.jswitch.Interface.OnPageViewScorllAble;
import com.szip.jswitch.Model.DrawDataBean;
import com.szip.jswitch.Model.EvenBusModel.UpdateReport;
import com.szip.jswitch.Model.ReportDataBean;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.View.ReportScorllView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2019/12/18.
 */

public class BloodPressureDayFragment extends BaseFragment implements OnPageViewScorllAble, View.OnClickListener {

    private ReportScorllView reportScorllView;

    private ReportActivity activity;

    private ListView listView;
    private BloodAdapter adapter;

    private ReportDataBean reportDataBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blood_pressure_day;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        activity = (ReportActivity) getActivity();
        initEvent();
        initData();
        initView();
        updateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initEvent() {
        getView().findViewById(R.id.leftIv).setOnClickListener(this);
        getView().findViewById(R.id.rightIv).setOnClickListener(this);
    }

    private void updateView() {
        reportScorllView.addData(reportDataBean.getDrawDataBeans());
        ArrayList<DrawDataBean> list = reportDataBean.getDrawDataBeans();
        Collections.sort(list);
        adapter.setDrawDataBeans(list);
        if (DateUtil.getTimeOfToday()==((ReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((ReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
            ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getBloodPressureWithDay(((ReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportScorllView = getView().findViewById(R.id.reportView);
        reportScorllView.setOnPageViewScorllAble(this);
        listView = getView().findViewById(R.id.dataList);
        adapter = new BloodAdapter(reportDataBean.getDrawDataBeans(),0,getContext());
        listView.setAdapter(adapter);
    }

    @Override
    public void onScroll(boolean scrollAble) {
        if (activity !=null)
            activity.setViewPagerScroll(scrollAble);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReport(UpdateReport updateReport){
        initData();
        updateView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rightIv:
                if (activity.reportDate==DateUtil.getTimeOfToday())
                    showToast(getString(R.string.tomorrow));
                else{
                    activity.reportDate+=24*60*60;
                    EventBus.getDefault().post(new UpdateReport());
                }
                break;
            case R.id.leftIv:
                activity.reportDate-=24*60*60;
                EventBus.getDefault().post(new UpdateReport());
                break;
        }
    }
}
