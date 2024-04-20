package com.elinkthings.ailinkmqttdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.lang.ref.WeakReference;


/**
 * xing
 * 2019/4/16 11:55
 * activity的基类, 共有的功能都可以写在这里
 */
public abstract class AppBaseActivity extends AppCompatActivity {
    /**
     * 上下文对象
     */
    protected Context mContext;
    protected String TAG = this.getClass().getName();
    protected Handler mHandler = new MyHandler(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() != 0) {
            initWindows();
            setContentView(getLayoutId());
            SaveActivityData(savedInstanceState);
            mContext = this;
            init();
        } else if (getLayoutView() != null) {
            initWindows();
            setContentView(getLayoutView());
            SaveActivityData(savedInstanceState);
            mContext = this;
            init();
        }

    }

    protected void SaveActivityData(Bundle savedInstanceState) {

    }


    /**
     * handler消息,使用弱引用,避免泄露问题
     */
    private static class MyHandler extends Handler {
        private WeakReference<AppBaseActivity> mActivity;

        MyHandler(AppBaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().uiHandlerMessage(msg);
        }
    }

    /**
     * handler消息处理
     */
    protected abstract void uiHandlerMessage(Message msg);

    /**
     * 在绑定布局前的操作（状态，任务栏等的设置）
     */
    protected void initWindows() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        //设置状态栏文字为黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    /**
     * 获取当前activity的布局
     *
     * @return int
     */
    protected abstract int getLayoutId();

    /**
     * 获取当前activity的布局
     *
     * @return View
     */
    protected View getLayoutView() {
        return null;
    }

    /**
     * 初始化
     */
    protected final void init() {
        addInit();
        initView();
        initData();
        initListener();
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getString(R.string.app_name) + getVersionName(this));
        }
    }


    /**
     * 版本号
     *
     * @param context 上下文
     * @return {@link String}
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if (pi != null) {
                return pi.versionName;
            }
            return "1.0.0";
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
    }


    protected void addInit() {

    }


    /**
     * 初始化事件
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化，绑定布局
     */
    protected abstract void initView();

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.public_toolbar_menu, menu);
//        MenuItem item = menu.findItem(R.id.img_public_right);
//        if (item != null) {
//            item.setIcon(R.drawable.me_manssage);
//        }
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            myFinish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            myFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回
     */
    protected void myFinish() {
        finish();
    }

    protected void onClickRight() {

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
