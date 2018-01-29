package com.lanjian.cniaoshop.utils;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lanjian.cniaoshop.bean.ShoppingCart;
import com.lanjian.cniaoshop.bean.Wares;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车管理类
 */
public class CollectionProvider {

    /**
     * SparseArray<ShoppingCart>存放购物车数据key-value值
     * SharedPreferences将购物车数据存入本地
     */
    private SparseArray<Wares> datas = null;
    private static Context mContext;
    public static final String CART_JSON = "collection_json";

    public static CollectionProvider getInstance(Context context) {
        mContext = context;
        return NewCartProvider.mInstance;
    }

    private static class NewCartProvider{
        private static CollectionProvider mInstance;
         static {
            mInstance = new CollectionProvider(mContext);
        }
    }

    private CollectionProvider(Context context) {
        this.mContext = context;

        datas = new SparseArray<>(10);

        listToSparse();
    }

    //存储SparseArray<ShoppingCart>数据，同时更新SharedPreferences的数据到本地
    public void put(Wares wares) {
        Wares cart = convertData(wares);
        update(cart);
        //将SparseArray<ShoppingCart>数据转换成List<ShoppingCart>数据保存在SharedPreferences中
    }

    //ShoppingCart子类不能强制转换成Wares父类，将Wres中数据添加到ShoppingCart
    public Wares convertData(Wares wares) {
        Wares cart = new Wares();
        cart.setId(wares.getId());
        cart.setDescription(wares.getDescription());
        cart.setName(wares.getName());
        cart.setImgUrl(wares.getImgUrl());
        cart.setPrice(wares.getPrice());

        return cart;
    }

    //保存SparseArray<ShoppingCart>里的数据到本地
    public void commit() {
        List<Wares> carts = sparseToList();

        SPUtils.put(CART_JSON, new Gson().toJson(carts));
    }

    //将保存的数据转换成List<ShoppingCart>
    private List<Wares> sparseToList() {

        int size = datas.size();
        List<Wares> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(datas.valueAt(i));
        }
        return list;
    }

    //更新数据
    public void update(Wares cart) {
        datas.put(cart.getId().intValue(), cart);
        commit();

    }

    //删除数据
    public void delete(Wares cart) {

        datas.delete(cart.getId().intValue());

        commit();
    }

    //删除数据
    public void delete(List<Wares> carts) {
        if (carts != null && carts.size() > 0) {
            for (Wares cart : carts) {
                delete(cart);
            }
        }
    }

    //从本地获取数据
    public List<Wares> getAll() {

        return getDataFromLocal();
    }

    //将本地数据存放在SparseArray<ShoppingCart>中
    private void listToSparse() {
        List<Wares> carts = getDataFromLocal();

        if (carts != null && carts.size() > 0) {
            for (Wares cart : carts) {
                datas.put(cart.getId().intValue(), cart);
            }
        } else {
            datas.clear();
        }
    }

    ///获取本地数据
    private List<Wares> getDataFromLocal() {

        String json = SPUtils.getValue(CART_JSON,"");

        List<Wares> carts = null;

        if (json != null) {
            carts = new Gson().fromJson(json, new TypeToken<List<Wares>>() {
            }.getType());
        }
        return carts;
    }
}
