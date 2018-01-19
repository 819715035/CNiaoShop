package com.lanjian.cniaoshop.adapter;


import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.bean.Wares;
import com.lanjian.cniaoshop.utils.CartProvider;
import com.lanjian.cniaoshop.utils.ToastUtils;

import java.util.List;

/**
 * 热卖商品适配器
 */
public class HWAdapter extends SimpleAdapter<Wares>{
    private CartProvider cartProvider;

    public HWAdapter(Context context, List<Wares> datas) {
        super(context, datas, R.layout.template_hot_wares);
        cartProvider = CartProvider.getInstance(context);
    }

    @Override
    public void bindData(BaseViewHolder holder, final Wares wares) {

        TextView tvTitle = holder.getTextView(R.id.tv_title);
        TextView tvPrice = holder.getTextView(R.id.tv_price);
        Button button = holder.getButton(R.id.btn_add);
        ImageView draweeView = (ImageView) holder.getView(R.id.drawee_view);

        tvTitle.setText(wares.getName());
        tvPrice.setText("￥ " + wares.getPrice());
        Glide.with(draweeView.getContext()).load(wares.getImgUrl()).into(draweeView);

        if (button != null){

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加数据到购物车
                    //添加数据到购物车
                    cartProvider.put(wares);

                    ToastUtils.showToastShort(mContext.getString(R.string.has_add_cart));
                }
            });
        }

    }

    /**
     * 设置布局
     * @param layoutId
     */
    public void reSetLayout(int layoutId){
        this.mLayoutResId = layoutId;

        notifyItemRangeChanged(0, getDatas().size());
    }


}
