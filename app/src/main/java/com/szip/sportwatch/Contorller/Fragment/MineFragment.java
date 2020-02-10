package com.szip.sportwatch.Contorller.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mediatek.wearable.WearableManager;
import com.szip.sportwatch.Contorller.AboutActivity;
import com.szip.sportwatch.Contorller.BluetoochCallActivity;
import com.szip.sportwatch.Contorller.LoginActivity;
import com.szip.sportwatch.Contorller.MainActivity;
import com.szip.sportwatch.Contorller.NotificationAppListActivity;
import com.szip.sportwatch.Contorller.SeachingActivity;
import com.szip.sportwatch.Contorller.UnitSelectActivity;
import com.szip.sportwatch.Contorller.UserInfoActivity;
import com.szip.sportwatch.DB.SaveDataUtil;
import com.szip.sportwatch.Interface.HttpCallbackWithBase;
import com.szip.sportwatch.Model.EvenBusModel.ConnectState;
import com.szip.sportwatch.Model.HttpBean.BaseApi;
import com.szip.sportwatch.Model.UserInfo;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.View.CharacterPickerWindow;
import com.szip.sportwatch.View.MyAlerDialog;
import com.szip.sportwatch.View.character.OnOptionChangedListener;
import com.szip.sportwatch.BLE.EXCDController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import static android.content.Context.MODE_PRIVATE;
import static com.szip.sportwatch.MyApplication.FILE;

/**
 * Created by Administrator on 2019/12/1.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener,HttpCallbackWithBase{
    private SharedPreferences sharedPreferencesp;
    private UserInfo userInfo;
    private MyApplication app;

    private ImageView pictureIv;
    private TextView userNameTv;

    private TextView stateTv;

    private TextView stepPlanTv;
    private TextView sleepPlanTv;

    private Switch blePhotoSwitch;

    private CharacterPickerWindow window,window1;

    private int STEPFLAG = 1,SLEEPFLAG = 2;
    private int stepPlan = 0,sleepPlan = 0;



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        initView();
        initEvent();
        initData();
        initWindow();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        HttpMessgeUtil.getInstance(getContext()).setHttpCallbackWithBase(this);
        if (MainService.getInstance().getConnectState() == WearableManager.STATE_CONNECTED){
            stateTv.setText(getString(R.string.connect));
        }else if (MainService.getInstance().getConnectState() == WearableManager.STATE_CONNECT_LOST||
                MainService.getInstance().getConnectState() == WearableManager.STATE_CONNECT_FAIL){
            stateTv.setText(getString(R.string.disConnect));
        }else if (MainService.getInstance().getConnectState() == WearableManager.STATE_CONNECTING){
            stateTv.setText(getString(R.string.connectting));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        HttpMessgeUtil.getInstance(getContext()).setHttpCallbackWithBase(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectStateChange(ConnectState connectBean){
        if (connectBean.getState() == WearableManager.STATE_CONNECTED){
            stateTv.setText(getString(R.string.connect));
        }else if (connectBean.getState()  == WearableManager.STATE_CONNECT_LOST||
                connectBean.getState()  == WearableManager.STATE_CONNECT_FAIL){
            stateTv.setText(getString(R.string.disConnect));
        }else if (connectBean.getState()  == WearableManager.STATE_CONNECTING){
            stateTv.setText(getString(R.string.connectting));
        }
    }

    /**
     * 初始化选择器
     * */
    private void initWindow() {
        //性别选择器
        window = new CharacterPickerWindow(getContext(),getString(R.string.stepPlan));

        final List<String> stepList = MathUitl.getStepPlanList();
        //初始化选项数据
        window.getPickerView().setPicker(stepList);
        //设置默认选中的三级项目
        window.setCurrentPositions(app.getUserInfo().getSex(), 0, 0);
        //监听确定选择按钮
        window.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                try {
                    ProgressHudModel.newInstance().show(getContext(),getString(R.string.waitting),getString(R.string.httpError),3000);
                    HttpMessgeUtil.getInstance(getContext()).postForSetStepsPlan(stepList.get(option1),STEPFLAG);
                    stepPlan = Integer.valueOf(stepList.get(option1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        //身高选择器
        window1 = new CharacterPickerWindow(getContext(),getString(R.string.sleepPlan));

        final ArrayList<String> sleepList = MathUitl.getSleepPlanList();
        window1.getPickerView().setText("h","");
        //初始化选项数据
        window1.getPickerView().setPicker(sleepList);
        //设置默认选中的三级项目
        window1.setCurrentPositions(sleepList.size()/2, 0, 0);
        //监听确定选择按钮
        window1.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                try {
                    ProgressHudModel.newInstance().show(getContext(),getString(R.string.waitting),getString(R.string.httpError),3000);
                    HttpMessgeUtil.getInstance(getContext()).postForSetSleepPlan((int)(Float.valueOf(sleepList.get(option1))*60)+"",SLEEPFLAG);
                    sleepPlan = (int)(Float.valueOf(sleepList.get(option1))*60);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 初始化视图
     * */
    private void initView() {
        pictureIv = getView().findViewById(R.id.pictureIv);
        userNameTv = getView().findViewById(R.id.userNameTv);
        stateTv = getView().findViewById(R.id.deviceNameTv);
        stepPlanTv = getView().findViewById(R.id.stepPlanTv);
        sleepPlanTv = getView().findViewById(R.id.sleepPlanTv);
        blePhotoSwitch = getView().findViewById(R.id.blePhotoSwitch);
        if (app.isCamerable())
            blePhotoSwitch.setChecked(true);
        else
            blePhotoSwitch.setChecked(false);
    }

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        getView().findViewById(R.id.pictureIv).setOnClickListener(this);
        getView().findViewById(R.id.userNameTv).setOnClickListener(this);
        getView().findViewById(R.id.deviceLl).setOnClickListener(this);
        getView().findViewById(R.id.stepPlanLl).setOnClickListener(this);
        getView().findViewById(R.id.sleepPlanLl).setOnClickListener(this);
        getView().findViewById(R.id.notificationLl).setOnClickListener(this);
        getView().findViewById(R.id.findLl).setOnClickListener(this);
        getView().findViewById(R.id.blePhoneLl).setOnClickListener(this);
        getView().findViewById(R.id.unitLl).setOnClickListener(this);
        getView().findViewById(R.id.aboutLl).setOnClickListener(this);
        getView().findViewById(R.id.logoutLl).setOnClickListener(this);
        blePhotoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                            requestPermissions(new String[]{Manifest.permission.CAMERA,
                            }, 102);
                        }else {
                            app.setCamerable(isChecked);
                        }
                    }else {
                        app.setCamerable(isChecked);
                    }
                }else {
                    app.setCamerable(isChecked);
                }
            }
        });
    }

    /**
     * 初始化数据
     * */
    private void initData() {
        userInfo = app.getUserInfo();
        userNameTv.setText(userInfo.getUserName());
        pictureIv.setImageResource(userInfo.getSex()==1?R.mipmap.my_head_male_52:R.mipmap.my_head_female_52);
        stepPlanTv.setText(userInfo.getStepsPlan()+"");
        sleepPlanTv.setText(String.format("%.1fh",userInfo.getSleepPlan()/60f));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102){
            int code = grantResults[0];
            if (!(code == PackageManager.PERMISSION_GRANTED)){
                showToast(getString(R.string.permissionErrorForCamare));
                app.setCamerable(false);
                blePhotoSwitch.setChecked(false);
            }else {
                app.setCamerable(true);
            }
        }
    }

    /**
     * 点击事件监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pictureIv:
            case R.id.userNameTv:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.deviceLl:
                MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.unbind), null, null,
                        false, new MyAlerDialog.AlerDialogOnclickListener() {
                            @Override
                            public void onDialogTouch(boolean flag) {
                                if (flag){
                                    try {
                                        String datas = MathUitl.getStringWithJson(getActivity().getSharedPreferences(FILE,MODE_PRIVATE));
                                        HttpMessgeUtil.getInstance(getActivity()).postForUpdownReportData(datas);
                                        ProgressHudModel.newInstance().show(getContext(),getString(R.string.waitting),getString(R.string.httpError)
                                        ,3000);
                                        HttpMessgeUtil.getInstance(getActivity().getApplicationContext()).getUnbindDevice();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },getActivity());
                break;
            case R.id.stepPlanLl:
                window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.sleepPlanLl:
                window1.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.notificationLl:
                startActivity(new Intent(getActivity(), NotificationAppListActivity.class));
                break;
            case R.id.findLl:
                if(MainService.getInstance().getConnectState()!=3)
                    showToast(getString(R.string.lostDevice));
                else{
                    ((MainActivity)getActivity()).showMyToast(getString(R.string.sendOK));
                    EXCDController.getInstance().writeForFindDevice();
                }
                break;
            case R.id.blePhoneLl:
                startActivity(new Intent(getActivity(), BluetoochCallActivity.class));
                break;
            case R.id.unitLl:
                startActivity(new Intent(getActivity(), UnitSelectActivity.class));
                break;
            case R.id.aboutLl:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.logoutLl:
                MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.logoutTip), null, null,
                        false, new MyAlerDialog.AlerDialogOnclickListener() {
                            @Override
                            public void onDialogTouch(boolean flag) {
                                if (flag){
                                    String datas = MathUitl.getStringWithJson(getActivity().getSharedPreferences(FILE,MODE_PRIVATE));
                                    try {
                                        HttpMessgeUtil.getInstance(getActivity()).postForUpdownReportData(datas);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (sharedPreferencesp==null)
                                        sharedPreferencesp = getActivity().getSharedPreferences(FILE,MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferencesp.edit();
                                    editor.putString("token",null);
                                    editor.commit();
                                    SaveDataUtil.newInstance(getContext()).clearDB();
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(),LoginActivity.class);
                                    startActivity(intent);
                                    ((MainActivity)getActivity()).MyFinish();
                                }
                            }
                        },getActivity());
                break;
        }
    }

    @Override
    public void onCallback(BaseApi baseApi, int id) {
        ProgressHudModel.newInstance().diss();
        if (id == STEPFLAG){
            stepPlanTv.setText(stepPlan+"");
            app.getUserInfo().setStepsPlan(stepPlan);
            if (MainService.getInstance().getConnectState()!=3){
                showToast(getString(R.string.syceError));
            }else {
                EXCDController.getInstance().writeForSetInfo(app.getUserInfo());
            }
        }else if (id == SLEEPFLAG){
            sleepPlanTv.setText(String.format("%.1f",sleepPlan/60f));
            app.getUserInfo().setSleepPlan(sleepPlan);
        }else {
            app.getUserInfo().setDeviceCode(null);
            SaveDataUtil.newInstance(getContext()).clearDB();
            Intent intent = new Intent();
            intent.setClass(getActivity(),SeachingActivity.class);
            startActivity(intent);
            ((MainActivity)getActivity()).MyFinish();
        }
        MathUitl.saveInfoData(getContext(),app.getUserInfo()).commit();
    }
}