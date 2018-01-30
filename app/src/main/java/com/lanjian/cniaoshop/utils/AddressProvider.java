package com.lanjian.cniaoshop.utils;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lanjian.cniaoshop.bean.Address;
import com.lanjian.cniaoshop.bean.Wares;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车管理类
 */
public class AddressProvider {

    /**
     * SparseArray<ShoppingCart>存放购物车数据key-value值
     * SharedPreferences将购物车数据存入本地
     */
    private SparseArray<Address> datas = null;
    private static Context mContext;
    public static final String CART_JSON = "address_json";

    public static AddressProvider getInstance(Context context) {
        mContext = context;
        return NewCartProvider.mInstance;
    }

    private static class NewCartProvider{
        private static AddressProvider mInstance;
         static {
            mInstance = new AddressProvider(mContext);
        }
    }

    private AddressProvider(Context context) {
        this.mContext = context;

        datas = new SparseArray<>(10);

        listToSparse();
    }

    //存储SparseArray<ShoppingCart>数据，同时更新SharedPreferences的数据到本地
    public void put(Address wares) {
        Address cart = convertData(wares);
        update(cart);
        //将SparseArray<ShoppingCart>数据转换成List<ShoppingCart>数据保存在SharedPreferences中
    }

    //ShoppingCart子类不能强制转换成Wares父类，将Wres中数据添加到ShoppingCart
    public Address convertData(Address wares) {
        Address cart = new Address();
        cart.setId(wares.getId());
        cart.setAddr(wares.getAddr());
        cart.setConsignee(wares.getConsignee());
        cart.setIsDefault(wares.getIsDefault());
        cart.setPhone(wares.getPhone());
        cart.setZip_code(wares.getZip_code());
        cart.setUserId(wares.getUserId());
        return cart;
    }

    //保存SparseArray<ShoppingCart>里的数据到本地
    public void commit() {
        List<Address> carts = sparseToList();
        LogUtils.e(new Gson().toJson(carts));
        SPUtils.put(CART_JSON, new Gson().toJson(carts));
    }

    //将保存的数据转换成List<ShoppingCart>
    private List<Address> sparseToList() {

        int size = datas.size();
        List<Address> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(datas.valueAt(i));
        }
        return list;
    }

    //更新数据
    public void update(Address cart) {
        datas.put(cart.getId().intValue(), cart);
        commit();

    }
    //更新数据
    public void updateDefault(Address cart) {
        List<Address> addresss = getDataFromLocal();
        for (Address addr:addresss){
            LogUtils.e(cart.getId()+"==="+addr.getId());
            if (addr.getId().longValue() == cart.getId().longValue()){
                if (!addr.getIsDefault()){
                    addr.setIsDefault(true);
                }

            }else{
                addr.setIsDefault(false);
            }
            datas.put(addr.getId().intValue(), addr);
        }
        commit();

    }

    //删除数据
    public void delete(Address cart) {

        datas.delete(cart.getId().intValue());

        commit();
    }

    //删除数据
    public void delete(List<Address> carts) {
        if (carts != null && carts.size() > 0) {
            for (Address cart : carts) {
                delete(cart);
            }
        }
    }

    //从本地获取数据
    public List<Address> getAll() {

        return getDataFromLocal();
    }

    //将本地数据存放在SparseArray<ShoppingCart>中
    private void listToSparse() {
        List<Address> carts = getDataFromLocal();

        if (carts != null && carts.size() > 0) {
            for (Address cart : carts) {
                datas.put(cart.getId().intValue(), cart);
            }
        } else {
            datas.clear();
        }
    }

    ///获取本地数据
    private List<Address> getDataFromLocal() {

        String json = SPUtils.getValue(CART_JSON,"");

        List<Address> carts = null;

        if (json != null) {
            carts = new Gson().fromJson(json, new TypeToken<List<Address>>() {
            }.getType());
        }
        return carts;
    }
}
