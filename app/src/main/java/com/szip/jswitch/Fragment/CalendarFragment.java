package com.szip.jswitch.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.necer.calendar.BaseCalendar;
import com.necer.calendar.MonthCalendar;
import com.necer.enumeration.SelectedModel;
import com.necer.listener.OnCalendarChangedListener;
import com.szip.jswitch.Interface.CalendarListener;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.StatusBarCompat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.view.ViewCompat;

import org.joda.time.LocalDate;

import java.util.Locale;


/**
 * Created by Administrator on 2019/12/31.
 */

public class CalendarFragment extends AppCompatDialogFragment {

    private View mContentView;
    private TextView dateTv;

    private String date;
    private int flag;

    private boolean enableAnim = false;
    private int mAnimStyle = com.zaaach.citypicker.R.style.DefaultCityPickerAnimation;
    private MonthCalendar monthCalendar;

    private CalendarListener calendarListener;
    /**
     * 获取实例
     * @param enable 是否启用动画效果
     * @return
     */
    public static CalendarFragment newInstance(boolean enable){
        final CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putBoolean("cp_enable_anim", enable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, com.zaaach.citypicker.R.style.CityPickerStyle);

        Bundle args = getArguments();
        if (args != null) {
            enableAnim = args.getBoolean("cp_enable_anim");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.fragment_calendar, container, false);
        monthCalendar = mContentView.findViewById(R.id.monthCalendar);
        monthCalendar.setSelectedMode(SelectedModel.SINGLE_UNSELECTED);
        dateTv = mContentView.findViewById(R.id.dateTv);




        mContentView.findViewById(R.id.backIv).setOnClickListener(onClickListener);
        mContentView.findViewById(R.id.nextMonthIv).setOnClickListener(onClickListener);
        mContentView.findViewById(R.id.lastMonthIv).setOnClickListener(onClickListener);
        monthCalendar.setFlag(flag);
        monthCalendar.setInitializeDate(date);
        monthCalendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {
            @Override
            public void onCalendarChange(BaseCalendar baseCalendar, int year, int month, LocalDate localDate,boolean isTouch) {
                dateTv.setText(String.format(Locale.ENGLISH,"%d-%02d",year,month));
                if (localDate!=null&&calendarListener!=null&&isTouch){
                    calendarListener.onClickDate(localDate.toString());
                    dismiss();
                }
            }
        });


        return mContentView;
    }


    public void setsetInitializeDate(String date){
        this.date = date;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.backIv:
                    dismiss();
                    break;
                case R.id.lastMonthIv:
                    monthCalendar.toLastPager();
                    break;
                case R.id.nextMonthIv:
                    monthCalendar.toNextPager();
                    break;
            }
        }
    };


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if(window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle);
            }
        }

        setAndroidNativeLightStatusBar(dialog,true);
        return dialog;
    }


    public void setAnimationStyle(int style){
        this.mAnimStyle = style <= 0 ? com.zaaach.citypicker.R.style.DefaultCityPickerAnimation : style;
    }

    public void setCalendarListener(CalendarListener listener){
        this.calendarListener = listener;
    }

    private void setAndroidNativeLightStatusBar(Dialog activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }


}
