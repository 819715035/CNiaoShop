package com.lanjian.cniaoshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.bean.Wares;

import java.util.List;

/**
 * 分类右部商品显示适配器
 */
public class CategoryWaresAdapter extends SimpleAdapter<Wares> {

    public CategoryWaresAdapter(Context context, List<Wares> datas) {
        super(context, datas, R.layout.template_grid_wares);
    }

    @Override
    public void bindData(BaseViewHolder holder, Wares wares) {

        holder.getTextView(R.id.tv_title).setText(wares.getName());
        holder.getTextView(R.id.tv_price).setText("￥ " + wares.getPrice());
        ImageView draweeView = (ImageView) holder.getView(R.id.drawee_view);
        Glide.with(holder.itemView.getContext()).load(wares.getImgUrl()).into(draweeView);
    }
}
