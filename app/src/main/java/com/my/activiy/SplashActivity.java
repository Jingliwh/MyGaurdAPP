package com.my.activiy;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private  String  TAG="SplashActivity";
    private  RelativeLayout relativeLayout;
    //Internet Permissions
    private static final int REQUEST_INTERNET = 1;

    private static final int lOADMAIN = 1;
    private static final int SHOWUPDATEDIALOG = 2;
    private static final int ERROR =3 ;

    //版本号
    private int versionCode;
    //版本名称
    private String versionName;
    private TextView tv_version_name;
    private  long  startTime;
    private  UriBean  uriBean;
    private  int errorCode = -1;
    private static String[] PERMISSIONS_INTERNET = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};
    private Handler handler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        goMainActivity();
                        break;
                    case 2:
                        newDialog();
                        break;
                    case ERROR:
                        switch (msg.arg1){
                            case 4011:
                               Toast.makeText(SplashActivity.this,"uri地址有误",Toast.LENGTH_SHORT).show();

                                break;
                            case 4012:
                                Toast.makeText(SplashActivity.this,"json数据解析出错",Toast.LENGTH_SHORT).show();

                                break;
                            case 4013:
                                Toast.makeText(SplashActivity.this,"获取网络连接失败",Toast.LENGTH_SHORT).show();

                                break;
                            case 404:
                                Toast.makeText(SplashActivity.this,"404未找到文件",Toast.LENGTH_SHORT).show();

                                break;
                            default:
                                break;

                        }
                        goMainActivity();
                        break;
                    default:
                        break;
                }
        }
    };
    public  void  newDialog(){
        AlertDialog.Builder ab= new AlertDialog.Builder(SplashActivity.this);
        ab.setTitle("更新apk");
        ab.setMessage(uriBean.getDesc());
        ab.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                goMainActivity();
            }
        }).setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadNewVersion();
                tv_version_name.setText(versionName);
                goMainActivity();
            }
        }).setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SplashActivity.this,"取消更新",Toast.LENGTH_LONG).show();
               goMainActivity();
            }
        }).show();

    }

    public  void downLoadNewVersion(){
        //初始化环境
        x.Ext.init(getApplication());
        RequestParams params = new RequestParams(uriBean.getUri());
        Log.i(TAG,""+uriBean.getUri());
        //断点下载
        params.setAutoRename(true);
        params.setSaveFilePath(Environment.getExternalStorageDirectory()+"/guard.apk");
        params.isAutoRename();
        Toast.makeText(SplashActivity.this,"下载",Toast.LENGTH_LONG).show();
        //自动为文件命名
        x.http().get(params, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onWaiting() {
                        Log.i(TAG, "onWaiting()");
                    }

                    @Override
                    public void onStarted() {
                        Log.i(TAG, "onStarted()");
                    }

                    @Override
                    public void onLoading(long l, long l1, boolean b) {
                        Log.i(TAG, "onLoading()");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i(TAG, "onSuccess()");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "guard.apk")), "application/vnd.android.package-archive");
                        startActivityForResult(intent,1);
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Log.i(TAG, "onError()"+throwable.toString());


                    }

                    @Override
                    public void onCancelled(CancelledException e) {
                        Log.i(TAG, "onCancelled()");
                    }

                    @Override
                    public void onFinished() {
                        Log.i(TAG, "onFinished()");
                    }

                });
                       /*******************sdk20以前
                        //下载新版本xutil2.3.6 4.4及以下版本
                       // HttpUtils  httpUtils=new HttpUtils();
                        String storg="/sdcard/myguard.apk";
                        httpUtils.download(uriBean.getUri(),storg, new RequestCallBack<File>() {
                            @Override
                            public void onSuccess(ResponseInfo<File> responseInfo) {
                                Log.i(TAG,""+uriBean.getUri());
                                Toast.makeText(SplashActivity.this,"请求成功",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                Log.i(TAG,""+uriBean.getUri());
                                Toast.makeText(SplashActivity.this,"请求失败",Toast.LENGTH_LONG).show();
                            }
                        });
                         **************/
    }

    public  void  goMainActivity(){
        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * Checks if the app has permission
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * current activity
     */
    public static void verifyPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
            PackageInfo packageInfo=pm.getPackageInfo(getPackageName(),0);

            versionCode=packageInfo.versionCode;
            versionName=packageInfo.versionName;
            tv_version_name.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG,"未找到包名",e);
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

                    //设置一个线程开始的时间
                    startTime= SystemClock.currentThreadTimeMillis();
                    HttpURLConnection httpURLConnection=null;
                    BufferedReader bufferedReader=null;
                    InputStream inputStream=null;
                   URL url;
                    //1网络连接url
                try {

                    url = new URL("http://10.233.70.80:8080/myversion.json");
                    //获取网络连接
                     httpURLConnection = (HttpURLConnection) url.openConnection();
                    //设置响应
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);

                    httpURLConnection.setRequestMethod("GET");
                    //获取响应码
                    int responsecode = httpURLConnection.getResponseCode();
                    if (responsecode == 200) {
                        Log.i(TAG, "网络请求成功");
                        //获取连接字节流
                         inputStream = httpURLConnection.getInputStream();
                        //转换字节流为字符流
                         bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        //读取一行信息
                        String line = bufferedReader.readLine();
                        //数据不为空则将数据放入缓冲区
                        StringBuilder jsonString = new StringBuilder();
                        //不为空就读完
                        while (line != null) {
                            jsonString.append(line);
                            //继续读取行数据
                            line = bufferedReader.readLine();
                        }
                        //4.解析读取的json数据，保存在javabean中

                            uriBean = parseJson(jsonString);

                        //5解析到以后比对版本信息
                        isNewVersion(uriBean);
                        Log.i(TAG, uriBean.getDesc() + "");
                    }
                    else{
                        errorCode=404;
                        Log.i(TAG, "未找到"+errorCode);
                    }

                }
                catch (MalformedURLException e) {//uri错误
                    errorCode=4011;
                    Log.i(TAG, "uri错误"+e.toString());
                }
                catch (JSONException e) {//json格式错误
                    errorCode=4012;
                    Log.i(TAG, "json解析出错"+e.toString());
                }
                catch (IOException e) {//网络错误
                    errorCode=4013;
                    Log.i(TAG, "网络连接错误"+e.toString());
                }finally {

                    Message ms=Message.obtain();
                    //发送消息处理事件
                    if(errorCode==-1){
                        //获取服务器版本后发送消息
                        ms.what=isNewVersion(uriBean);
                    }
                    else{
                        //发送错误消息
                        ms.what=ERROR;
                        //返回错误码
                        ms.arg1=errorCode;
                    }
                    long endTime=SystemClock.currentThreadTimeMillis();
                    if(endTime-startTime<3000){
                        SystemClock.sleep(3000-(endTime-startTime));
                    }
                    handler.sendMessage(ms);

                    try {
                        if(httpURLConnection!=null)
                           httpURLConnection.disconnect();
                        if(bufferedReader!=null)
                              bufferedReader.close();
                        if(inputStream!=null)
                             inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }
    //4.根据传递数据解析json
    public UriBean parseJson(StringBuilder jsonString)throws JSONException{
        UriBean  uriBean=new UriBean();
        //1.将jsonString对象封装成字符串

            JSONObject  jsonObject=new JSONObject(jsonString+"");
            //解析字符串
            String uri=jsonObject.getString("url");
            int versionCode=jsonObject.getInt("versionCode");
            String  desc=jsonObject.getString("desc");
            uriBean.setUri(uri);
            uriBean.setVersionCode(versionCode);
            uriBean.setDesc(desc);

        return uriBean;
    }
    public int isNewVersion(UriBean  uriBean) {
        //获取服务器的版本,子线程调用方法
        int serverVersionCode = uriBean.getVersionCode();
        if (serverVersionCode != versionCode){
            return SHOWUPDATEDIALOG;

       }
    else
            return lOADMAIN;
    }

}
