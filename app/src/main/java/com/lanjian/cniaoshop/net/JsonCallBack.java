package com.lanjian.cniaoshop.net;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author lanjian
 * @email 819715035@qq.com
 * creat at $date$
 * description
 */
public abstract class JsonCallBack <T> extends AbsCallback<T>{

    private Type type;
    private Class<T> clazz;

    public JsonCallBack() {
    }

    public JsonCallBack(Type type) {
        this.type = type;
    }

    public JsonCallBack(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        if (response.body() == null){
            return null;
        }
        T data = null;
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(response.body().charStream());
        if (type!=null){
            data = gson.fromJson(reader,type);
        }else if (clazz!=null){
            data = gson.fromJson(reader,clazz);
        }else{
            Type getType = getClass().getGenericSuperclass();
            Type type = ((ParameterizedType)getType).getActualTypeArguments()[0];
            data  = gson.fromJson(reader,type);
        }
        return data;
    }


    public abstract void SuccessData(Response<T> response);

    @Override
    public void onSuccess(Response<T> response) {
        SuccessData(response);
    }
}
