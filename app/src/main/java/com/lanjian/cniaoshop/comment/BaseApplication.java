package com.lanjian.cniaoshop.comment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.lanjian.cniaoshop.bean.User;
import com.lanjian.cniaoshop.utils.UserLocalData;
import com.lzy.okgo.OkGo;

/**
 * Created by Administrator on 2017/8/28 0028.
 */

public class BaseApplication extends Application {
    public static BaseApplication application;
    private User user;

    //在整个应用执行过程中，需要提供的变量
    public static Context context;//需要使用的上下文对象
    public static Handler handler;//需要使用的handler
    public static Thread mainThread;////提供主线程对象
    public static int mainThreadId;//提供主线程对象的id

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = getApplicationContext();
        handler = new Handler();
        mainThread = Thread.currentThread();//实例化当前Application的线程即为主线程
        mainThreadId = android.os.Process.myTid();//获取当前线程的id
        //初始化全局捕捉异常
        //CrashHandler.getInstance().init();
        OkGo.getInstance().init(this);
        initUser();
    }
    private void initUser() {
        this.user = UserLocalData.getUser(this);
    }

    public User getUser() {
        return user;
    }

    /**
     * 获取token信息
     * @return
     */
    public String getToken() {
        return UserLocalData.getToken(this);
    }

    /**
     * 保存用户信息和token信息到本地
     * @param user
     * @param token
     */
    public void putUser(User user, String token) {
        this.user = user;
        UserLocalData.putUser(this, user);
        UserLocalData.putToken(this, token);
    }

    /**
     * 清空用户信息和token信息
     */
    public void clearUser() {
        this.user = null;
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);
    }



    private Intent intent;

    /**
     * 保存登录意图
     */
    public void putIntent(Intent intent) {
        this.intent = intent;
    }

    /**
     * 获取登录意图
     * @return
     */
    public Intent getIntent() {
        return this.intent;
    }

    public void jumpToTargetActivity(Context context) {
        context.startActivity(intent);
        this.putIntent(null);
    }
}