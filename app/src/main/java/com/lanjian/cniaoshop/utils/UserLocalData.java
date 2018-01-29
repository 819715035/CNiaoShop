package com.lanjian.cniaoshop.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lanjian.cniaoshop.bean.User;

/**
 * 用户管理类
 */
public class UserLocalData {

    //存储用户数据
    public static void putUser(Context context, User user){
        String user_json = new Gson().toJson(user);
        SPUtils.put(Constants.USER_JSON,user_json);
    }

    //存储Token
    public static void putToken(Context context, String token){
        SPUtils.put(Constants.TOKEN,token);
    }

    //获取用户数据
    public static User getUser(Context context){
        String user_json = SPUtils.getValue(Constants.USER_JSON,"");
        if (!TextUtils.isEmpty(user_json)){
            return new Gson().fromJson(user_json,User.class);
        }
        return null;
    }

    //获取token
    public static String getToken(Context context){
        String token_json = SPUtils.getValue(Constants.TOKEN,"");
        return token_json;
    }

    //清除用户数据
    public static void clearUser(Context context){
        SPUtils.put(Constants.USER_JSON,"");
    }

    //清除token
    public static void clearToken(Context context){
        SPUtils.put(Constants.TOKEN,"");
    }
}
