package com.my.activiy;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.my.bean.UriBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    private  String  TAG="SplashActivity";
    private  RelativeLayout relativeLayout;
    //Internet Permissions
    private static final int REQUEST_INTERNET = 1;
    private static final int lOADMAIN = 1;
    private static final int SHOWUPDATEDIALOG = 2;
    private int versionCode;
    private String versionName;
    private TextView tv_version_name;
    private  long  startTime;
    private  UriBean  uriBean;
    private static String[] PERMISSIONS_INTERNET = {Manifest.permission.INTERNET};
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        goMainActivity();
                        break;
                    case 2:
                        AlertDialog.Builder ab= new AlertDialog.Builder(SplashActivity.this);
                        ab.setTitle("更新");
                        ab.setMessage(uriBean.getDesc());
                        ab.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tv_version_name.setText(uriBean.getVersionCode()+"");
                                Toast.makeText(SplashActivity.this,"更新成功",Toast.LENGTH_LONG).show();
                                goMainActivity();
                            }
                        }).setNegativeButton("取消",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SplashActivity.this,"下次更新",Toast.LENGTH_LONG).show();
                                goMainActivity();
                            }
                        }).show();

                        break;
                    default:
                        break;
                }
        }
    };
    public  void  goMainActivity(){
        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
    }
    /**
     * Checks if the app has permission
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_INTERNET,
                    REQUEST_INTERNET
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        verifyPermissions(SplashActivity.this);
        //1.初始化组件
        initView();
        //2启动动画
        initAmiation();
        //3.获取当前版本
        initData();
        //4.访问网络检查版本更新
        checkVersion();


    }
    public void  initData(){
        PackageManager  pm=getPackageManager();
        try {
            PackageInfo  packageInfo=pm.getPackageInfo(getPackageName(),0);
            versionCode=packageInfo.versionCode;
            versionName=packageInfo.versionName;
            tv_version_name.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void initView(){
        //1.找到父布局
        relativeLayout= (RelativeLayout) findViewById(R.id.relay_splash);
        tv_version_name=(TextView)findViewById(R.id.tv_splash_version_name);

    }

    public void initAmiation(){
        //为组件添加动画效果
        //播放缩放动画
        ScaleAnimation  sa=new ScaleAnimation(0.1f,1f,0.1f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //旋转动画
        RotateAnimation  ra=new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //透明动画
        AlphaAnimation aa=new AlphaAnimation(0,1);
        aa.setFillAfter(true);
        //创建动画set集合
        AnimationSet as=new AnimationSet(false);
        as.addAnimation(sa);
        as.addAnimation(ra);
        as.addAnimation(aa);
        as.setDuration(3000);
        relativeLayout.startAnimation(as);
    }
    public void checkVersion(){
        //耗时操作,开启线程完成
        new Thread(){
            @Override
            public void run() {

                try {
                    //设置一个线程开始的时间
                    startTime= SystemClock.currentThreadTimeMillis();
                    //1网络连接url
                    URL url=new URL("http://10.233.70.80:8080/myversion.json");
                    //获取网络连接
                    HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                    //设置响应
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setRequestMethod("GET");
                    //获取响应码
                    int responsecode=httpURLConnection.getResponseCode();
                    if(responsecode==200){
                        Log.i(TAG,"网络请求成功");
                        //获取连接字节流
                        InputStream  inputStream=httpURLConnection.getInputStream();
                        //转换字节流为字符流
                        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                        //读取一行信息
                        String line=bufferedReader.readLine();
                        //数据不为空则将数据放入缓冲区
                        StringBuilder  jsonString=new StringBuilder();
                        //不为空就读完
                        while(line!=null){
                            jsonString.append(line);
                            //继续读取行数据
                            line=bufferedReader.readLine();
                        }
                        //4.解析读取的json数据，保存在javabean中
                         uriBean=parseJson(jsonString);
                        //5解析到以后比对版本信息
                        isNewVersion(uriBean);
                        Log.i(TAG,uriBean.getDesc()+"呵呵");



                        bufferedReader.close();
                        inputStream.close();
                    }
                    else
                        Log.i(TAG,"呵呵连接失败！！！");

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
    //4.根据传递数据解析json
    public UriBean parseJson(StringBuilder jsonString){
        UriBean  uriBean=new UriBean();
        //1.将jsonString对象封装成字符串
        try {
            JSONObject  jsonObject=new JSONObject(jsonString+"");
            //解析字符串
            String uri=jsonObject.getString("url");

            int versionCode=jsonObject.getInt("versionCode");

            String  desc=jsonObject.getString("desc");
            uriBean.setUri(uri);
            uriBean.setVersionCode(versionCode);
            uriBean.setDesc(desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return uriBean;
    }
    public void isNewVersion(UriBean  uriBean){
        //获取服务器的版本,子线程调用方法
        int serverVersionCode=uriBean.getVersionCode();
        if(serverVersionCode==versionCode){
            long endTime=SystemClock.currentThreadTimeMillis();
            if(endTime-startTime<3000){
                SystemClock.sleep(3000-(endTime-startTime));
            }
            //如果与当前版本一致
            //向主线程发消息
            Message msg=Message.obtain();
            msg.what=lOADMAIN;
            handler.sendMessage(msg);
        }
        else{
            //如果不一致
            Message msg=Message.obtain();
            msg.what=SHOWUPDATEDIALOG;
            handler.sendMessage(msg);
        }



    }
}
