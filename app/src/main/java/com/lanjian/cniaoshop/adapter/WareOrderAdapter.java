package com.lanjian.cniaoshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.bean.ShoppingCart;

import java.util.List;

/**
 * 订单商品
 */
public class WareOrderAdapter extends SimpleAdapter<ShoppingCart> {

    public WareOrderAdapter(Context context, List<ShoppingCart> datas) {
        super(context, datas, R.layout.template_order_wares);
    }

    @Override
    public void bindData(BaseViewHolder holder, ShoppingCart shoppingCart) {

        ImageView draweeView = (ImageView) holder.getView(R.id.drawee_view);
        Glide.with(draweeView.getContext()).load(shoppingCart.getImgUrl()).into(draweeView);
    }

    public float getTotalPrice() {

        float sum = 0;
        if (!isNull()) {
            return sum;
        }

        for (ShoppingCart cart : mDatas) {
            if (cart.isChecked())
                sum += cart.getCount() * Float.parseFloat(cart.getPrice());
        }

        return sum;
    }

    private boolean isNull() {
        return (mDatas != null && mDatas.size() > 0);
    }

}
