package com.lanjian.cniaoshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.bean.Favorite;
import com.lanjian.cniaoshop.bean.Wares;
import com.lanjian.cniaoshop.utils.ToastUtils;

import java.util.List;

/**
 * 收藏
 */
public class FavoriteAdapter extends SimpleAdapter<Wares> {

    private FavoriteLisneter mFavoriteLisneter;

    public FavoriteAdapter(Context context, List<Wares> datas, FavoriteLisneter favoriteLisneter) {
        super(context, datas, R.layout.template_favorite_item);
        this.mFavoriteLisneter = favoriteLisneter;
    }

    @Override
    public void bindData(BaseViewHolder holder, final Wares wares) {
        holder.getTextView(R.id.tv_title).setText(wares.getName());
        holder.getTextView(R.id.tv_price).setText("￥ " + wares.getPrice());

        ImageView draweeView = (ImageView) holder.getView(R.id.drawee_view);
        Glide.with(draweeView.getContext()).load(wares.getImgUrl()).into(draweeView);
        Button buttonRemove = holder.getButton(R.id.btn_remove);
        Button buttonLike = holder.getButton(R.id.btn_like);

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavoriteLisneter != null)
                    mFavoriteLisneter.onClickDelete(wares);
            }
        });

        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToastShort( "功能正在完善...");
            }
        });


    }

    public interface FavoriteLisneter {

        void onClickDelete(Wares favorite);

    }
}
