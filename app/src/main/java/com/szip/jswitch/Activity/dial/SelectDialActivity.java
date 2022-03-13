package com.szip.jswitch.Activity.dial;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.szip.jswitch.Activity.BaseActivity;
import com.szip.jswitch.Activity.diy.DIYActivity;
import com.szip.jswitch.BLE.BleClient;
import com.szip.jswitch.BLE.EXCDController;
import com.szip.jswitch.Model.EvenBusModel.UpdateView;
import com.szip.jswitch.Model.SendDialModel;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.ProgressHudModel;
import com.szip.jswitch.Util.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SelectDialActivity extends BaseActivity implements ISelectDialView{


    private ISelectDialPresenter iSelectDialPresenter;
    private String pictureUrl;
    private int clock = -1;
    private ImageView dialIv,changeIv;
    private int progress = 0;
    private boolean isSendPic = false;

    private String faceType = "";
    private boolean isCircle = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_select_dial);
        if (MyApplication.getInstance().isMtk()) {
            iSelectDialPresenter = new SelectDialPresenterImpl(getApplicationContext(),this);
        } else{
            iSelectDialPresenter = new SelectDialPresenterImpl06(getApplicationContext(),this);
        }
        isCircle = MyApplication.getInstance().isCircle();
        faceType = MyApplication.getInstance().getFaceType();
        StatusBarCompat.translucentStatusBar(this,true);
        setAndroidNativeLightStatusBar(this,true);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!isSendPic){
            progress = 0;
            ProgressHudModel.newInstance().diss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iSelectDialPresenter.setViewDestory();
        EventBus.getDefault().unregister(this);
    }


    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.rightIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 保存成功
                if (!ProgressHudModel.newInstance().isShow()){
                    ProgressHudModel.newInstance().show(SelectDialActivity.this,getString(R.string.loading),
                            getString(R.string.connect_error),10000);
                    MainService.getInstance().downloadFirmsoft(pictureUrl,"dial.jpg");
                }
            }
        });
    }

    private void initView() {
        setTitleText(getString(R.string.face));
        RecyclerView dialRv = findViewById(R.id.dialRv);
        changeIv = findViewById(R.id.changeIv);
        iSelectDialPresenter.getViewConfig(dialRv);
    }

    /**
     * 更新数据显示
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdataView(UpdateView updateView){
        if(updateView.getState().equals("0")){//进度+1
            progress++;
            iSelectDialPresenter.sendDial(null,-1);
            ProgressHudModel.newInstance().setProgress(progress);
        }else if (updateView.getState().equals("1")){//完成
            isSendPic = false;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            showToast(getString(R.string.diyDailOK));
        }else if (updateView.getState().equals("2")){//失败
            if (isSendPic){
                isSendPic = false;
                progress = 0;
                ProgressHudModel.newInstance().diss();
                showToast(getString(R.string.diyDailError));
            }
        }else {
            isSendPic = true;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            iSelectDialPresenter.sendDial(MyApplication.getInstance().getPrivatePath()+"dial.jpg",clock);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendPicture(SendDialModel sendDialModel){
        if (sendDialModel.isLoadSuccess()){
            if (MyApplication.getInstance().isMtk())
                EXCDController.getInstance().initDialInfo();
            else
                BleClient.getInstance().writeForSendPicture(0,clock,0,0,new byte[0]);
        }else {
            showToast(getString(R.string.httpError));
            ProgressHudModel.newInstance().diss();
        }
    }

    @Override
    public void initList(boolean initSuccess) {
        if (!initSuccess)
            showToast(getString(R.string.httpError));
        initView();
        initEvent();
    }

    @Override
    public void setView(String id, String pictureId, int clock) {
        if (isCircle){
            changeIv.setImageResource(R.mipmap.change_watch_c);
            dialIv = findViewById(R.id.dialIv_c);
        }else {
            if (faceType.indexOf("320*385")>=0){
                changeIv.setImageResource(R.mipmap.change_watch_06);
                dialIv = findViewById(R.id.dialIv_r06);
            }else {
                dialIv = findViewById(R.id.dialIv_r);
            }
        }
        this.pictureUrl = pictureId;
        this.clock = clock;
        Glide.with(this).load(id).into(dialIv);
    }

    @Override
    public void setDialView(String dialId, String pictureId, int clock) {
        if (dialId==null){
            startActivity(new Intent(SelectDialActivity.this, DIYActivity.class));
            finish();
        }else {
            this.pictureUrl = pictureId;
            this.clock = clock;
            Glide.with(this).load(dialId).into(dialIv);
        }
    }

    @Override
    public void setDialProgress(int max) {
        ProgressHudModel.newInstance().showWithPie(this,getString(R.string.diyDailing),max,
                getString(R.string.diyDailError),100*1000);
    }
}
