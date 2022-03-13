package com.szip.jswitch.Util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.szip.jswitch.R;
import com.szip.jswitch.View.HealthyProgressView;

/**
 * Created by Administrator on 2019/12/23.
 */

public class ViewUtil{

    private Context context;

    public ViewUtil(Context context) {
        this.context = context;
    }

    /**
     * 计算心率游标位置
     * */
    public void setHeartView(int heart,View textView,View healthyProgressView){
        if (heart==0){
            ((TextView)textView).setText("--");
            ((HealthyProgressView)healthyProgressView).setRadio(0);
            return;
        }
        if (heart/150f<0.333f){
            ((TextView)textView).setText(context.getString(R.string.slow));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.normalT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_normal));
        }else if (heart/150f<0.666f){
            ((TextView)textView).setText(context.getString(R.string.normal));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.goodT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_good));
        }else {
            ((TextView)textView).setText(context.getString(R.string.quick));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.badT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_bad));
        }
        ((HealthyProgressView)healthyProgressView).setRadio(heart/150f);
    }

    /**
     * 计算收缩压游标位置
     * */
    public void setBloodPressureView(int sbp,int dbp,View textView,View healthyProgressView,
                                       View healthyProgressView1){
        if (sbp==0){
            ((TextView)textView).setText("--");
            ((HealthyProgressView)healthyProgressView).setRadio(0);
            ((HealthyProgressView)healthyProgressView1).setRadio(0);
            return;
        }
        if ((sbp-40)/150f<0.333){
            ((TextView)textView).setText(context.getString(R.string.flat));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.normalT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_normal));
        }else if ((sbp-40)/150f<0.666){
            if ((dbp-30)/90f<0.333){
                ((TextView)textView).setText(context.getString(R.string.flat));
                ((TextView)textView).setTextColor(context.getResources().getColor(R.color.normalT));
                textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_normal));
            }else if ((dbp-30)/90f<0.666){
                ((TextView)textView).setText(context.getString(R.string.normal));
                ((TextView)textView).setTextColor(context.getResources().getColor(R.color.goodT));
                textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_good));
            }else {
                ((TextView)textView).setText(context.getString(R.string.higher));
                ((TextView)textView).setTextColor(context.getResources().getColor(R.color.badT));
                textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_bad));
            }
        }else {
            ((TextView)textView).setText(context.getString(R.string.higher));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.badT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_bad));
        }
        ((HealthyProgressView)healthyProgressView).setRadio((sbp-40)/150f);
        ((HealthyProgressView)healthyProgressView1).setRadio((dbp-30)/90f);
    }


    /**
     * 计算血氧游标位置
     * */
    public void setBloodOxygenView(int bloodOxygen,View textView,View healthyProgressView){
        if (bloodOxygen==0){
            ((TextView)textView).setText("--");
            ((HealthyProgressView)healthyProgressView).setRadio(0);
            return;
        }
        if ((bloodOxygen-88)/12f<0.5){
            ((TextView)textView).setText(context.getString(R.string.flat));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.normalT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_normal));
        }else {
            ((TextView)textView).setText(context.getString(R.string.normal));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.goodT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_good));
        }
      ((HealthyProgressView)healthyProgressView).setRadio((bloodOxygen-88)/12f);
    }

    /**
     * 计算温度游标位置
     * */
    public void setAnimalHeatView(int animalHeat,View textView,View healthyProgressView){
        if (animalHeat==0){
            ((TextView)textView).setText("--");
            ((HealthyProgressView)healthyProgressView).setRadio(0);
            return;
        }
        if (animalHeat<=373){
            ((TextView)textView).setText(context.getString(R.string.normal));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.goodT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_good));
        }else {
            ((TextView)textView).setText(context.getString(R.string.hot));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.badT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_bad));
        }
        ((HealthyProgressView)healthyProgressView).setRadio((animalHeat-323)/100f);
    }

    /**
     * 判断睡眠质量
     * */
    public void setSleepView(int sleep, View textView){
        if (sleep==0){
            ((TextView)textView).setText("--");
            return;
        }
        if (sleep/480f<0.4f){
            ((TextView)textView).setText(context.getString(R.string.bad));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.badT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_bad));
        }else if (sleep/480f<0.8f){
            ((TextView)textView).setText(context.getString(R.string.good));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.normalT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_normal));
        }else {
            ((TextView)textView).setText(context.getString(R.string.perfect));
            ((TextView)textView).setTextColor(context.getResources().getColor(R.color.goodT));
            textView.setBackground(context.getResources().getDrawable(R.drawable.bg_healthy_good));
        }
    }

}
