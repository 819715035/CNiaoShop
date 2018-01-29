package com.lanjian.cniaoshop.adapter;


import android.content.Context;
import android.content.Intent;

import com.lanjian.cniaoshop.activity.WaresDetailsActivity;
import com.lanjian.cniaoshop.bean.Wares;
import com.lanjian.cniaoshop.utils.Constants;

import java.util.List;

public abstract class SimpleAdapter<T> extends BaseAdapter<T,BaseViewHolder>{

    public SimpleAdapter(Context context, List<T> datas, int layoutResId) {
        super(context, datas, layoutResId);
    }

    public SimpleAdapter(Context mContext, int mLayoutResId) {
        super(mContext, mLayoutResId);
    }

    //显示商品详情
    public void showDetail(Wares wares){

        Intent intent = new Intent(mContext, WaresDetailsActivity.class);

        intent.putExtra(Constants.WARES,wares);

        mContext.startActivity(intent);
    }
}
