package com.lanjian.cniaoshop.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.adapter.BaseAdapter;
import com.lanjian.cniaoshop.adapter.HWAdapter;
import com.lanjian.cniaoshop.bean.Page;
import com.lanjian.cniaoshop.bean.Wares;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.Constants;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.CommonTitleView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaresListActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.tv_summary)
    TextView tvSummary;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.refresh_layout)
    MaterialRefreshLayout refreshLayout;
    private HWAdapter waresAdapter;
    private long campaignId = 0;
    private int orderBy = 0;

    private static final int TAG_DEFAULT = 0;
    private static final int TAG_SALE = 1;
    private static final int TAG_PRICE = 2;

    private static final int ACTION_LIST = 1;
    private static final int ACTION_GRID = 2;
    private int totalCount;
    private int curPage = 1;
    private int pageSize = 10;
    private final int STATE_NORMAL = 0;
    private final int STATE_REFRESH = 1;
    private final int STATE_MORE = 2;
    private int state = STATE_NORMAL;
    private List<Wares> datas = new ArrayList<>();
    private View rightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wares_list);
        ButterKnife.bind(this);
        campaignId = getIntent().getLongExtra(Constants.CAMPAIGN_ID, 0);
        setToolbar();
        //初始化Tab
        initTab();

        initRefreshLayout();

        //获取数据
        getData();
    }

    public void setToolbar() {
        rightView = titleView.findViewById(R.id.right_iv);
        rightView.setBackgroundResource(R.mipmap.icon_grid_32);
        rightView.setTag(ACTION_LIST);
        rightView.setOnClickListener(this);
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {
        OkGo.<Page<Wares>>post(API.WARES_CAMPAIGN_LIST)
                .tag(this)
                .params("campaignId",campaignId)
                .params("orderBy",orderBy)
                .params("curPage",curPage)
                .params("pageSize",pageSize)
                .execute(new JsonCallBack<Page<Wares>>() {
                    @Override
                    public void SuccessData(Response<Page<Wares>> response) {
                        Page<Wares> result = response.body();
                        curPage = result.getCurrentPage();
                        pageSize = result.getPageSize();
                        totalCount = result.getTotalCount();
                        datas = result.getList();
                        showData();
                    }
                });
    }

    private void showData() {
        switch (state) {
            case STATE_NORMAL:
                tvSummary.setText("共有" + totalCount + "件商品");
                waresAdapter = new HWAdapter(this, datas);

                waresAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                    @Override
                    public void onItemClick(View view, int position) {
                        waresAdapter.showDetail(waresAdapter.getItem(position));
                    }
                });

                recycleView.setAdapter(waresAdapter);
                recycleView.setLayoutManager(new LinearLayoutManager(this));
                recycleView.setItemAnimator(new DefaultItemAnimator());
                break;
            case STATE_MORE:
                waresAdapter.loadMore(datas);
                refreshLayout.finishRefreshLoadMore();
                break;
            case STATE_REFRESH:
                waresAdapter.refreshData(datas);
                recycleView.scrollToPosition(0);
                refreshLayout.finishRefresh();
                break;
        }
    }
    private void initRefreshLayout() {
        refreshLayout.setLoadMore(true);
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (curPage * pageSize < totalCount) {
                    loadMoreData();
                } else {
                    ToastUtils.showToastShort( "没有更多数据...");
                    refreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        curPage = 1;
        state = STATE_REFRESH;
        getData();
    }


    /**
     * 加载更多
     */
    private void loadMoreData() {
        curPage = ++curPage;
        state = STATE_MORE;
        getData();
    }

    private void initTab() {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(R.string.defaults);
        tab.setTag(TAG_DEFAULT);
        tabLayout.addTab(tab);

        tab = tabLayout.newTab();
        tab.setText(R.string.sales);
        tab.setTag(TAG_SALE);
        tabLayout.addTab(tab);

        tab = tabLayout.newTab();
        tab.setText(R.string.price);
        tab.setTag(TAG_PRICE);
        tabLayout.addTab(tab);

        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        orderBy = (int) tab.getTag();
        curPage = 1;
        state = STATE_NORMAL;
        getData();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View v) {

        int action = (int) v.getTag();

        if (ACTION_LIST == action) {
            //更改图标，布局，tag
            rightView.setBackgroundResource(R.mipmap.icon_list_32);
            rightView.setTag(ACTION_GRID);
            waresAdapter.reSetLayout(R.layout.template_grid_wares);
            recycleView.setLayoutManager(new GridLayoutManager(this, 2));
            recycleView.setAdapter(waresAdapter);
        } else if (ACTION_GRID == action) {
            rightView.setBackgroundResource(R.mipmap.icon_grid_32);
            rightView.setTag(ACTION_LIST);
            waresAdapter.reSetLayout(R.layout.template_hot_wares);
            recycleView.setLayoutManager(new LinearLayoutManager(this));
            recycleView.setAdapter(waresAdapter);

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
