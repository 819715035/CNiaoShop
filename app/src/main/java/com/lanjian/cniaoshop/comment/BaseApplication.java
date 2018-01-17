package com.lanjian.cniaoshop.comment;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.lzy.okgo.OkGo;

/**
 * Created by Administrator on 2017/8/28 0028.
 */

public class BaseApplication extends Application {
    public static BaseApplication application;


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
    }

}