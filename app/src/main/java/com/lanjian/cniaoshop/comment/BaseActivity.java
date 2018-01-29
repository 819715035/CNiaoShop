package com.lanjian.cniaoshop.comment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.activity.LoginActivity;
import com.lanjian.cniaoshop.bean.User;
import com.lanjian.cniaoshop.utils.ScreenUtils;

/**
 * Created by Administrator on 2017/8/28 0028.
 */

public abstract class BaseActivity extends AppCompatActivity{

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 竖屏锁定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        // 添加Activity到堆栈
        ActivityManager.getInstance().add(this);
        ScreenUtils.setWindowStatusBarColor(this, R.color.colorPrimary);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //销毁当前的Activity
    public void removeCurrentActivity(){
        ActivityManager.getInstance().removeCurrent();
    }

    //销毁所有的activity
    public void removeAll(){
        ActivityManager.getInstance().removeAll();
    }

    /**
     * 某个activity变得“容易”被系统销毁时，该activity的onSaveInstanceState就会被执行，
     * 除非该activity是被用户主动销毁的。
     * (按下HOME键？长按HOME键，选择运行其他的程序?按下电源按键?activity切换？屏幕方向切换？)
     * 此方法常常用来做一些应用中非持久性的存储
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 该activity被恢复时执行(前提是该activity的确已被销毁,即此方法与onSaveInstanceState()方法不一定对等成对调用)
     * ,且savedInstanceState参数还会传递到onCreate()内。 此方法常常用来做一些应用中非持久性的存储的恢复。
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 此方法与清单中android:configChanges对等调用(不指定,配置改变时将重调用onCreate方法,指定后,
     * 指定情况下将不调用onCreate方法而来调用此方法,故你懂的啦)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * toast 短 int
     *
     * @param pResId 字符串的id
     */
    protected void showToast(int pResId) {
        showToast(getString(pResId));
    }

    /**
     * toast 长 int
     *
     * @param pResId 字符串的id
     */
    protected void showLongToast(int pResId) {
        showLongToast(getString(pResId));
    }

    /**
     * toast 短 String
     *
     * @param pMsg
     */
    protected void showToast(String pMsg) {
        if (toast == null) {
            initToast();
        }
        toast.setText(pMsg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * toast 长 string
     *
     * @param pMsg
     */
    protected void showLongToast(String pMsg) {
        if (toast == null) {
            initToast();
        }
        toast.setText(pMsg);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    @SuppressLint("ShowToast")
    private void initToast() {
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    }

    /**
     * Activity跳转
     *
     * @param pClass
     */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * Activity跳转
     *
     * @param pClass
     * @param pBundle
     */
    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        openActivity(pClass, pBundle, null);
    }

    /**
     * Activity跳转
     *
     * @param pClass
     * @param pBundle
     * @param uri
     */
    protected void openActivity(Class<?> pClass, Bundle pBundle, Uri uri) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        if (uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
    }

    /**
     * Activity跳转
     *
     * @param pAction
     */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    /**
     * Activity跳转
     *
     * @param pAction
     * @param pBundle
     */
    protected void openActivity(String pAction, Bundle pBundle) {
        openActivity(pAction, pBundle, null);
    }

    /**
     * Activity跳转
     *
     * @param pAction
     * @param pBundle
     * @param uri
     */
    protected void openActivity(String pAction, Bundle pBundle, Uri uri) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        if (uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
    }


    /**
     * 检查当前网络是否可用
     * @return
     */

    protected boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void startActivity(Intent intent, boolean isNeedLogin) {

        if (isNeedLogin) {
            User user = BaseApplication.application.getUser();

            if (user != null) {
                super.startActivity(intent);
            } else {
               BaseApplication.application.putIntent(intent);
                Intent loginIntent = new Intent(this, LoginActivity.class);
                super.startActivity(loginIntent);
            }
        } else {
            super.startActivity(intent);
        }
    }
}
