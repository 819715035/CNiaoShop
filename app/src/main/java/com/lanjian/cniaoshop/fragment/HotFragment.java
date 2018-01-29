package com.lanjian.cniaoshop.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.adapter.BaseAdapter;
import com.lanjian.cniaoshop.adapter.HWAdapter;
import com.lanjian.cniaoshop.bean.HomeCampaign;
import com.lanjian.cniaoshop.bean.Page;
import com.lanjian.cniaoshop.bean.Wares;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.LogUtils;
import com.lanjian.cniaoshop.utils.SPUtils;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;


/**
 * Created by Ivan on 15/9/22.
 */
public class HotFragment extends Fragment{


    private View view;
    private RecyclerView mRecyclerView;
    private HWAdapter mAdapter;
    private int curPage = 1;
    private int pageSize = 10;
    private MaterialRefreshLayout materialRefreshLayout;
    private final int STATE_NORMAL = 0;
    private final int STATE_REFRESH = 1;
    private final int STATE_MORE = 2;
    private int state = STATE_NORMAL;
    private int totalCount;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         view= inflater.inflate(R.layout.fragment_hot,container,false);
        initView();
        getDataFromNet();
        setLoadMore();
        return view ;

    }

    private void setLoadMore() {
        materialRefreshLayout.setLoadMore(true);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //下拉刷新...
                refresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (curPage * pageSize < totalCount) {
                    loadMore();
                } else {
                    ToastUtils.showToastShort( "没有更多数据...");
                    materialRefreshLayout.finishRefreshLoadMore();
                    materialRefreshLayout.setLoadMore(false);
                }
            }
        });
    }

    private void loadMore() {
        LogUtils.e("loadmore");
        curPage = ++curPage;
        state = STATE_MORE;
        getDataFromNet();
    }

    private void refresh() {
        curPage = 1;
        state = STATE_REFRESH;
        getDataFromNet();
        materialRefreshLayout.setLoadMore(true);
    }

    private void getDataFromNet() {
        LogUtils.e("getDataFromNet");
        OkGo.<Page<Wares>>post(API.HOT_URL)
                .tag(getActivity())
                .params("curPage",curPage)
                .params("pageSize",pageSize)
                .execute(new JsonCallBack<Page<Wares>>() {
                    @Override
                    public void SuccessData(Response<Page<Wares>> response) {
                        totalCount = response.body().getTotalCount();
                        showData(response.body());
                        String gson = new Gson().toJson(response.body());
                        SPUtils.put("HOT_URL",gson);
                    }

                    @Override
                    public void onError(Response<Page<Wares>> response) {
                        super.onError(response);
                        String data = SPUtils.getValue("HOT_URL","");
                        if (!TextUtils.isEmpty(data)){
                            Page<Wares> hots = new Gson().fromJson(data,new TypeToken<Page<Wares>>(){}.getType());
                            showData(hots);
                        }
                    }
                });
    }

    private void showData(Page<Wares> body) {
        switch (state){
            case STATE_NORMAL:
                setAdapter(body.getList());
                break;
            case STATE_MORE:
                mAdapter.addData(mAdapter.getDatas().size(), body.getList());
                mRecyclerView.scrollToPosition(mAdapter.getDatas().size());
                materialRefreshLayout.finishRefreshLoadMore();
                break;
            case STATE_REFRESH:
                mAdapter.clearData();
                mAdapter.addData(body.getList());
                mRecyclerView.scrollToPosition(0);
                materialRefreshLayout.finishRefresh();
                break;
        }

    }

    private void setAdapter(List<Wares> body) {
        mAdapter = new HWAdapter(getActivity(),body);
        mAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
            @Override
            public void onItemClick(View view, int position) {
                Wares wares = mAdapter.getItem(position);
                mAdapter.showDetail(wares);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initView() {
        mRecyclerView = view.findViewById(R.id.recyclerview);
        materialRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
    }

}
