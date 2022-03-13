package com.szip.jswitch.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.szip.jswitch.Activity.initInfo.InitInfoActivity;
import com.szip.jswitch.Activity.main.MainActivity;
import com.szip.jswitch.Interface.HttpCallbackWithLogin;
import com.szip.jswitch.Model.HttpBean.LoginBean;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.JsonGenericsSerializator;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.Util.ProgressHudModel;
import com.szip.jswitch.Util.StatusBarCompat;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;

import static com.szip.jswitch.MyApplication.FILE;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    /**
     * 用户名密码
     * */
    private TextView userTipTv,passwordTipTv;
    private EditText userEt,passwordEt;
    /**
     * 国家以及国家编号
     * */
    private TextView countryTv,countryCodeTv,countryTipTv;

    /**
     * 隐私条款
     * */
    private CheckBox checkBox;

    private Context mContext;

    private SharedPreferences sharedPreferencesp;

    private int flagForEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        mContext = this;
        initView();
        initEvent();
    }

    /**
     * 初始化视图
     * */
    private void initView() {
        StatusBarCompat.translucentStatusBar(LoginActivity.this,true);
        setAndroidNativeLightStatusBar(this,true);
        userTipTv = findViewById(R.id.userTipTv);
        passwordTipTv = findViewById(R.id.passwordTipTv);
        countryCodeTv = findViewById(R.id.countryCodeTv);
        countryTipTv = findViewById(R.id.countryTipTv);
        userEt = findViewById(R.id.userEt);
        passwordEt = findViewById(R.id.passwordEt);
        Log.i("data******",""+R.id.userEt);
        Log.i("data******",""+R.id.passwordEt);
        countryTv = findViewById(R.id.countryTv);
        checkBox = findViewById(R.id.checkbox);


        if (sharedPreferencesp==null)
            sharedPreferencesp = getSharedPreferences(FILE,MODE_PRIVATE);
        userEt.setText(sharedPreferencesp.getString("user",""));
        countryTv.setText(sharedPreferencesp.getString("countryName",""));
        countryCodeTv.setText(sharedPreferencesp.getString("countryCode",""));
        if (countryTv.getText().toString().equals("")){
            countryTv.setText(getString(R.string.choseCountry));
            countryTv.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    /**
     * 初始化事件
     * */
    private void initEvent() {
        findViewById(R.id.loginBtn).setOnClickListener(this);
        findViewById(R.id.forgetTv).setOnClickListener(this);
        findViewById(R.id.registerTv).setOnClickListener(this);
        findViewById(R.id.countryRl).setOnClickListener(this);
        findViewById(R.id.visitorTv).setOnClickListener(this);
        (findViewById(R.id.privacyTv)).setOnClickListener(this);
        userEt.addTextChangedListener(watcher);
        userEt.setOnFocusChangeListener(focusChangeListener);
        passwordEt.addTextChangedListener(watcher);
        passwordEt.setOnFocusChangeListener(focusChangeListener);
        ((CheckBox)findViewById(R.id.lawsCb)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String psd = passwordEt.getText().toString();
                if (isChecked){
                    passwordEt.setInputType(0x90);
                }else {
                    passwordEt.setInputType(0x81);
                }
                passwordEt.setSelection(psd.length());
            }
        });
    }

    /**
     * 点击监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                if (countryCodeTv.getText().toString().equals("")){
                    showToast(getString(R.string.choseCountry));
                }else if (userEt.getText().toString().equals("")){
                    showToast(getString(R.string.phoneOrEmail));
                } else if (!checkBox.isChecked()){
                    showToast(getString(R.string.checkPrivacy));
                }else if (passwordEt.getText().toString().equals("")){
                    showToast(getString(R.string.enterPassword));
                }else {
                    try {
                        if (!MathUitl.isNumeric(userEt.getText().toString())){//邮箱
                            if (MathUitl.isEmail(userEt.getText().toString())){//如果是邮箱登录，判断邮箱格式是否正确
                                ProgressHudModel.newInstance().show(mContext,getString(R.string.logging),getString(R.string.httpError),10000);
                                HttpMessgeUtil.getInstance().postLogin("2","","",
                                        userEt.getText().toString(),passwordEt.getText().toString(),"","",callback);
                            }else {
                                showToast(getString(R.string.enterRightEmail));
                            }
                        }else {//电话
                            ProgressHudModel.newInstance().show(mContext,getString(R.string.logging),getString(R.string.httpError),10000);
                            HttpMessgeUtil.getInstance().postLogin("1","00"+countryCodeTv.getText().toString().substring(1),
                                    userEt.getText().toString(), "",passwordEt.getText().toString(),"","",callback);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.visitorTv:
                if (!checkBox.isChecked()){
                    showToast(getString(R.string.checkPrivacy));
                }else {
                    ProgressHudModel.newInstance().show(mContext,getString(R.string.logging),getString(R.string.httpError),10000);
                    try {
                        HttpMessgeUtil.getInstance().postLogin("3","",
                                "", "","",MathUitl.getDeviceId(mContext),"1",callback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.forgetTv:
                startActivity(new Intent(mContext, ForgetPasswordActivity.class));
                break;
            case R.id.registerTv:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
            case R.id.countryRl:
                CityPicker.getInstance()
                        .setFragmentManager(getSupportFragmentManager())
                        .enableAnimation(true)
                        .setAnimationStyle(R.style.CustomAnim)
                        .setLocatedCity(null)
                        .setHotCities(null)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                countryTv.setText(data == null ? "" :  data.getName());
                                countryCodeTv.setText("+"+data.getCode().substring(2));
                                countryTv.setTextColor(getResources().getColor(R.color.rayblue));
                                countryCodeTv.setTextColor(getResources().getColor(R.color.rayblue));
                                countryTipTv.setTextColor(getResources().getColor(R.color.gray));
                                if (sharedPreferencesp==null)
                                    sharedPreferencesp = getSharedPreferences(FILE,MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferencesp.edit();
                                editor.putString("countryName",countryTv.getText().toString());
                                editor.putString("countryCode",countryCodeTv.getText().toString());
                                editor.commit();
                            }
                            @Override
                            public void onLocate() {

                            }
                        }).show();
                break;
            case R.id.privacyTv:
                startActivity(new Intent(LoginActivity.this, PrivacyActivity.class));
                break;
        }
    }


    /**
     * 输入框键入监听器
     * */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String data = s.toString();

            switch (flagForEt){
                case 0:
                    if (TextUtils.isEmpty(data)){
                        userTipTv.setTextColor(getResources().getColor(R.color.rayblue));
                    }else {
                        userTipTv.setTextColor(getResources().getColor(R.color.gray));
                    }
                    break;
                case 1:
                    if (TextUtils.isEmpty(data)){
                        passwordTipTv.setTextColor(getResources().getColor(R.color.rayblue));
                    }else {
                        passwordTipTv.setTextColor(getResources().getColor(R.color.gray));
                    }
                    break;
            }
        }
    };

    /**
     * 输入框焦点监听
     * */
    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.userEt:
                    if (hasFocus){
                        flagForEt = 0;
                    }
                    break;
                case R.id.passwordEt:
                    if (hasFocus){
                        flagForEt = 1;
                    }
                    break;
            }
        }
    };

    private GenericsCallback<LoginBean> callback = new GenericsCallback<LoginBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(LoginBean loginBean, int id) {
            ProgressHudModel.newInstance().diss();
            if (loginBean.getCode()==200){
                HttpMessgeUtil.getInstance().setToken(loginBean.getData().getToken());
                SharedPreferences.Editor editor = sharedPreferencesp.edit();
                if (loginBean.getData().getUserInfo().getPhoneNumber()!=null||loginBean.getData().getUserInfo().getEmail()!=null)
                    editor.putString("user",loginBean.getData().getUserInfo().getPhoneNumber()!=null?
                            loginBean.getData().getUserInfo().getPhoneNumber():
                            loginBean.getData().getUserInfo().getEmail());
                editor.putString("phone",loginBean.getData().getUserInfo().getPhoneNumber());
                editor.putString("mail",loginBean.getData().getUserInfo().getEmail());
                ((MyApplication)getApplicationContext()).setUserInfo(loginBean.getData().getUserInfo());
                editor.putString("password",passwordEt.getText().toString());
                startActivity(new Intent(mContext, InitInfoActivity.class));
                editor.commit();
            }else {
                showToast(loginBean.getMessage());
            }
        }
    };
}
