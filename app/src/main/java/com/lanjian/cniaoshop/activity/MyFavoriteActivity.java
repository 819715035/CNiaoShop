package com.lanjian.cniaoshop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.adapter.BaseAdapter;
import com.lanjian.cniaoshop.adapter.CardViewtemDecortion;
import com.lanjian.cniaoshop.adapter.FavoriteAdapter;
import com.lanjian.cniaoshop.bean.Favorite;
import com.lanjian.cniaoshop.bean.Wares;
import com.lanjian.cniaoshop.comment.BaseApplication;
import com.lanjian.cniaoshop.msg.BaseResMsg;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.CollectionProvider;
import com.lanjian.cniaoshop.weight.CommonTitleView;
import com.lanjian.cniaoshop.weight.CustomDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFavoriteActivity extends AppCompatActivity {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    private FavoriteAdapter mAdapter;
    private CustomDialog mDialog;
    private List<Wares> wares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        setToolbar();
        initFavorite();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initFavorite();
    }

    public void setToolbar() {
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFavorite() {

        /*String userId = BaseApplication.application.getUser().getId() + "";

        if (!TextUtils.isEmpty(userId)) {
            OkGo.<List<Favorite>>get(API.FAVORITE_LIST)
                    .tag(this)
                    .params("user_id",Long.parseLong(userId))
                    .params("token",BaseApplication.application.getToken())
                    .execute(new JsonCallBack<List<Favorite>>() {
                        @Override
                        public void SuccessData(Response<List<Favorite>> response) {
                            showFavorite(response.body());
                        }
                    });
        }*/
        wares = CollectionProvider.getInstance(this).getAll();
        showFavorite(wares);
    }

    /**
     * 显示删除提示对话框
     *
     * @param favorite
     */
    private void showDialog(final Wares favorite) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您确定删除该商品吗？");
        builder.setTitle("友情提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteFavorite(favorite);
                initFavorite();

                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialog.isShowing())
                            mDialog.dismiss();
                    }
                });

        mDialog = builder.create();
        mDialog.show();
    }

    private void deleteFavorite(Wares favorite) {

        CollectionProvider.getInstance(this).delete(favorite);
        OkGo.<BaseResMsg>post(API.FAVORITE_DEL)
                .tag(this)
                .params("id",favorite.getId())
                .params("token",BaseApplication.application.getToken())
                .execute(new JsonCallBack<BaseResMsg>() {
                    @Override
                    public void SuccessData(Response<BaseResMsg> response) {
                        if (response.body().getStatus() == response.body().STATUS_SUCCESS) {
                            setResult(RESULT_OK);
                            if (mDialog.isShowing())
                                mDialog.dismiss();
                        }
                    }
                });
    }

    private void showFavorite(final List<Wares> favorites) {

        if (mAdapter == null) {
            mAdapter = new FavoriteAdapter(this, favorites, new FavoriteAdapter.FavoriteLisneter() {
                @Override
                public void onClickDelete(Wares wares) {
                    showDialog(wares);
                }
            });
            recycleView.setAdapter(mAdapter);
            recycleView.setLayoutManager(new LinearLayoutManager(this));
            recycleView.addItemDecoration(new CardViewtemDecortion());

            mAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                @Override
                public void onItemClick(View view, int position) {
                    mAdapter.showDetail(favorites.get(position));
                }
            });
        } else {
            mAdapter.refreshData(favorites);
            recycleView.setAdapter(mAdapter);
        }
    }

}
