package com.my.activiy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.my.adapter.MyGridItemAdapter;
import com.my.util.MD5Util;
import com.my.util.MyConstant;
import com.my.util.SharePreferrenceUtil;
import com.my.view.MarqueeTextView;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private AlertDialog dialog;
    private String[] function_name_list=new String[]{
            "手机防盗","通讯卫士","软件管家","进程管理","流量统计","病毒查杀","缓存清理","高级工具","设置中心"
    };
    private int[] function_icon_list=new int[]{
            R.mipmap.safe,R.mipmap.callmsgsafe,R.mipmap.phone
            ,R.mipmap.taskmanager,R.mipmap.netmanager,R.mipmap.trojan
            ,R.mipmap.sysoptimize,R.mipmap.atools,R.mipmap.settings
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化组件
        initView();
        //初始化数据
        initData();
        //初始化各个点击事件
        initEvent();
    }



    private void initData() {
        MyGridItemAdapter myadapter=new MyGridItemAdapter(getApplicationContext(),function_name_list,function_icon_list);
        gridView.setAdapter(myadapter);

    }

    private void initView() {
        MarqueeTextView tv= (MarqueeTextView) findViewById(R.id.main_tv_marquee);
        //设置单行显示
        tv.setSingleLine();
        //grid
         gridView= (GridView) findViewById(R.id.main_grid_function);

    }
    private void initEvent() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        if(TextUtils.isEmpty(SharePreferrenceUtil.getString(getApplicationContext(),"password",""))){
                            //如果为空设置密码
                            showPasswprdDialog();
                            Toast.makeText(MainActivity.this,"为空"+position,Toast.LENGTH_LONG).show();
                        }
                        else{
                            //如果不为空读取密码
                            showInputPassword();
                            Toast.makeText(MainActivity.this,"不为空"+position,Toast.LENGTH_LONG).show();
                        }

                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showInputPassword() {
        AlertDialog.Builder abuilder2=new AlertDialog.Builder(MainActivity.this);
        View view= View.inflate(MainActivity.this, R.layout.inputsavedpasswpord, null);
        final EditText ed_password= (EditText) view.findViewById(R.id.edit_password_1);
        Button  bt_ok1= (Button) view.findViewById(R.id.bt_ok);
        Button  bt_cancel1= (Button) view.findViewById(R.id.bt_cancel);
        abuilder2.setView(view);
        dialog = abuilder2.create();
        dialog.show();
        bt_ok1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  password=ed_password.getText().toString().trim();
                password=MD5Util.EncoderByMd5(MD5Util.EncoderByMd5(password));
                //1判断两次输入的密码是否一致为空，return掉，不一致也return掉
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(MainActivity.this,"密码为空",Toast.LENGTH_LONG).show();
                    return;
                }
                else if(password.equals(SharePreferrenceUtil.getString(getApplicationContext(),MyConstant.PASSWORD,""))){
                    //如果密码相同
                    dialog.dismiss();//关闭对话框
                    Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(intent);

                }
                else {
                    Toast.makeText(MainActivity.this,"密码错误",Toast.LENGTH_LONG).show();
                    ed_password.setText("");
                    return;
                }
            }
        });
        //点击取消响应
        bt_cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//关闭对话框
            }
        });


    }

    private void showPasswprdDialog() {
        AlertDialog.Builder abuilder=new AlertDialog.Builder(MainActivity.this);
        //显示自定义对话框--加载自定义布局
        View view= View.inflate(MainActivity.this, R.layout.passworddialog, null);
        //加载界面组件
        final EditText ed_password= (EditText) view.findViewById(R.id.edit_password);
        final EditText ed_passwordfirm= (EditText) view.findViewById(R.id.edit_passwordfirm);
        Button  bt_ok= (Button) view.findViewById(R.id.dialog_bt_ok);
        Button  bt_cancel= (Button) view.findViewById(R.id.dialog_bt_cancel);
        abuilder.setView(view);
        dialog = abuilder.create();
        dialog.show();
        //点击确定响应
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  password=ed_password.getText().toString().trim();
                String  passwordfirm=ed_passwordfirm.getText().toString().trim();
                //1判断两次输入的密码是否一致为空，return掉，不一致也return掉
                if(TextUtils.isEmpty(password)||TextUtils.isEmpty(passwordfirm)){
                    Toast.makeText(MainActivity.this,"密码不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                else if(!password.equals(passwordfirm)){
                    Toast.makeText(MainActivity.this,"密码输入不一致",Toast.LENGTH_LONG).show();
                    return;
                }
                //2一致的话保存密码
                else {
                    //md5加密两次，利用工具类保存密码
                    password = MD5Util.EncoderByMd5(MD5Util.EncoderByMd5(password));
                    SharePreferrenceUtil.putString(getApplicationContext(),MyConstant.PASSWORD,password);
                    Toast.makeText(MainActivity.this, "密码已保存", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
        //点击取消响应
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//关闭对话框
            }
        });
    }
}
