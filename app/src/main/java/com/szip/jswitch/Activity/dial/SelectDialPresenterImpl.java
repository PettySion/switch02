package com.szip.jswitch.Activity.dial;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.szip.jswitch.Adapter.DialAdapter;
import com.szip.jswitch.BLE.EXCDController;
import com.szip.jswitch.Model.HttpBean.DialBean;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.JsonGenericsSerializator;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.Util.ScreenCapture;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;

public class SelectDialPresenterImpl implements ISelectDialPresenter{

    private Context context;
    private ISelectDialView iSelectDialView;
    private ArrayList<DialBean.Dial> dialArrayList = new ArrayList<>();

    public SelectDialPresenterImpl(Context context, ISelectDialView iSelectDialView) {
        this.context = context;
        this.iSelectDialView = iSelectDialView;
        getDialList();
    }

    private void getDialList() {
        try {
            HttpMessgeUtil.getInstance().getDialList(MyApplication.getInstance().getDialGroupId(),
                    new GenericsCallback<DialBean>(new JsonGenericsSerializator()) {
                @Override
                public void onError(Call call, Exception e, int id) {
                    if (iSelectDialView!=null)
                        iSelectDialView.initList(false);
                }

                @Override
                public void onResponse(DialBean response, int id) {
                    if (response.getCode() == 200){
                        dialArrayList = response.getData().getList();
                        if (iSelectDialView!=null)
                            iSelectDialView.initList(true);
                    }else {
                        if (iSelectDialView!=null)
                            iSelectDialView.initList(false);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getViewConfig(RecyclerView dialRv) {
        dialRv.setLayoutManager(new GridLayoutManager(context, 3));
        DialAdapter dialAdapter = new DialAdapter(dialArrayList,context);
        dialRv.setAdapter(dialAdapter);
        dialRv.setHasFixedSize(true);
        dialRv.setNestedScrollingEnabled(false);

        if (iSelectDialView!=null&&dialArrayList.size()!=0)
            iSelectDialView.setView(dialArrayList.get(0).getPreviewUrl(),
                    dialArrayList.get(0).getPlateBgUrl(),dialArrayList.get(0).getPointerNumber());

        dialAdapter.setOnItemClickListener(new DialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position==-1){
                    if (iSelectDialView!=null)
                        iSelectDialView.setDialView(null,null,-1);
                } else{
                    if (iSelectDialView!=null){
                        iSelectDialView.setDialView(dialArrayList.get(position).getPreviewUrl(),
                                dialArrayList.get(position).getPlateBgUrl(),dialArrayList.get(position).getPointerNumber());
                    }
                }
            }
        });
    }

    @Override
    public void sendDial(String resultUri, int clock) {
        if (resultUri!=null){
            int PAGENUM = 8000;//分包长度
            InputStream in = null;
            try {
                in = new FileInputStream(MyApplication.getInstance().getPrivatePath()+"dial.jpg");
                byte[] datas = toByteArray(in);
                in.close();
                int num = datas.length/PAGENUM;
                num = datas.length%PAGENUM==0?num:num+1;

                if (iSelectDialView!=null)
                    iSelectDialView.setDialProgress(num);

                if (datas.length<=PAGENUM){
                    EXCDController.getInstance().writeForSendImage(datas,1,num,clock, MathUitl.getClockStyle(clock));
                }else {
                    byte[] newDatas;
                    for (int i=0;i<datas.length;i+=PAGENUM){
                        int len = (datas.length-i>PAGENUM)?PAGENUM:(datas.length-i);
                        newDatas = new byte[len];
                        System.arraycopy(datas,i,newDatas,0,len);
                        EXCDController.getInstance().writeForSendImage(newDatas,i/PAGENUM+1,num,clock,MathUitl.getClockStyle(clock));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setViewDestory() {
        iSelectDialView = null;
    }

    private byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }
}
