package com.lanjian.cniaoshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.bean.OrderItem;

import java.util.List;

/**
 * 订单Item
 */
public class OrderItemAdapter extends SimpleAdapter<OrderItem> {

    public OrderItemAdapter(Context context, List<OrderItem> datas) {
        super(context, datas, R.layout.template_order_wares);
    }

    @Override
    public void bindData(BaseViewHolder holder, OrderItem orderItem) {

        ImageView draweeView = (ImageView) holder.getView(R.id.drawee_view);
        Glide.with(draweeView.getContext()).load(orderItem.getWares().getImgUrl()).into(draweeView);
    }

    public float getTotalPrice() {

        float sum = 0;
        if (!isNull()) {
            return sum;
        }

        for (OrderItem orderItem : mDatas) {
                sum += Float.parseFloat(orderItem.getWares().getPrice());
//                sum += orderItem.getAmount();

            System.out.println("sum-----"+sum);
        }

        return sum;
    }

    private boolean isNull() {
        return (mDatas != null && mDatas.size() > 0);
    }

}
