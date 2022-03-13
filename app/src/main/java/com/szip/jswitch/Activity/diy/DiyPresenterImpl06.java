package com.szip.jswitch.Activity.diy;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.szip.jswitch.Adapter.DIYAdapter;
import com.szip.jswitch.BLE.BleClient;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.ScreenCapture;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

public class DiyPresenterImpl06 implements IDiyPresenter{

    private Handler handler;
    private Context context;
    private IDiyView iDiyView;

    public DiyPresenterImpl06(Context context, IDiyView iDiyView) {
        this.context = context;
        this.iDiyView = iDiyView;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void getViewConfig(RecyclerView dialRv) {
        dialRv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        final int [] dials;
        final int [] clock;
        final boolean isCircle = MyApplication.getInstance().isCircle();
        if (isCircle){
            dials = new int[]{
                    R.mipmap.diy_2,R.mipmap.diy_4, R.mipmap.diy_13};
            clock = new int[]{
                    2,4,13};

        }else {
            dials = new int[]{R.mipmap.clock_2523_3,R.mipmap.clock_2523_4, R.mipmap.clock_2523_6,R.mipmap.clock_2523_7};
            clock = new int[]{3,4,6,7};
        }
        DIYAdapter diyAdapter = new DIYAdapter(dials);
        dialRv.setAdapter(diyAdapter);
        dialRv.setHasFixedSize(true);
        dialRv.setNestedScrollingEnabled(false);

        if (iDiyView!=null)
            iDiyView.setView(isCircle);


        diyAdapter.setOnItemClickListener(new DIYAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (iDiyView!=null){
                    iDiyView.setDialView(dials[position],clock[position]);
                }
            }
        });
    }
    private int i = 0;
    private byte datas[];
    @Override
    public void sendDial(Uri resultUri, int clock) {
        if (resultUri!=null){
            final int PAGENUM = 128;//分包长度
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis;
            try {
                fis = new FileInputStream(new File(resultUri.getPath()));
                byte[] buf = new byte[1024];
                int n;
                while (-1 != (n = fis.read(buf)))
                    baos.write(buf, 0, n);
            } catch (Exception e) {
                e.printStackTrace();
            }
            final byte[] datas = baos.toByteArray();
            int num = datas.length/PAGENUM;
            num = datas.length%PAGENUM==0?num:num+1;
            if (iDiyView!=null)
                iDiyView.setDialProgress(num);
            this.datas = datas;
            this.i = 0;
        }
        sendByte();
    }

    private void sendByte(){
        byte[] newDatas;
        int len = (datas.length- i >128)?128:(datas.length- i);
        newDatas = new byte[len];
        System.arraycopy(datas, i,newDatas,0,len);
        BleClient.getInstance().writeForSendPicture(1,0,0, i/128,newDatas);
        i+=128;
        if (i>=datas.length){
            BleClient.getInstance().writeForSendPicture(2,0,0,0,new byte[0]);
        }
    }

    @Override
    public void cropPhoto(Uri uri) {
        try {
            String[] spaceType = MyApplication.getInstance().getFaceType().split("\\*");
            Uri path = uri;
            //临时用一个名字用来保存裁剪后的图片
            String fileName = MyApplication.getInstance().getPrivatePath()+"crop.jpg";
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            Uri target = Uri.fromFile(file);
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(context.getResources().getColor(R.color.rayblue));
            options.setStatusBarColor(context.getResources().getColor(R.color.rayblue));
            options.setActiveWidgetColor(context.getResources().getColor(R.color.rayblue));
            options.setCompressionQuality(80);
            UCrop uCrop = UCrop.of(path, target)
                    .withAspectRatio(Float.valueOf(spaceType[0])/Float.valueOf(spaceType[1]), 1f)
                    .withMaxResultSize(Integer.valueOf(spaceType[0]), Integer.valueOf(spaceType[1]))
                    .withOptions(options);
            if (iDiyView!=null){
                iDiyView.getCropPhoto(uCrop);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void setViewDestory() {
        iDiyView = null;
    }
}
