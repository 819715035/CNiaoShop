package com.lanjian.cniaoshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.adapter.BaseAdapter;
import com.lanjian.cniaoshop.adapter.CardViewtemDecortion;
import com.lanjian.cniaoshop.adapter.MyOrderAdapter;
import com.lanjian.cniaoshop.bean.Order;
import com.lanjian.cniaoshop.comment.BaseActivity;
import com.lanjian.cniaoshop.comment.BaseApplication;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.Constants;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.CommonTitleView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrderActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    public static final int STATUS_ALL = 1000;
    public static final int STATUS_SUCCESS = 1; //支付成功的订单
    public static final int STATUS_PAY_FAIL = -2; //支付失败的订单
    public static final int STATUS_PAY_WAIT = 0; //：待支付的订单
    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    private int status = STATUS_ALL;
    private MyOrderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        /**
         * 初始化Tab
         */
        initTab();
        /**
         * 获取订单数据
         */
        getOrders();
        setToolbar();
    }

    public void setToolbar() {
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //初始化tab
    private void initTab() {

        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText("全部");
        tab.setTag(STATUS_ALL);
        tabLayout.addTab(tab);

        tab = tabLayout.newTab();
        tab.setText("支付成功");
        tab.setTag(STATUS_SUCCESS);
        tabLayout.addTab(tab);

        tab = tabLayout.newTab();
        tab.setText("待支付");
        tab.setTag(STATUS_PAY_WAIT);
        tabLayout.addTab(tab);

        tab = tabLayout.newTab();
        tab.setText("支付失败");
        tab.setTag(STATUS_PAY_FAIL);
        tabLayout.addTab(tab);

        tabLayout.setOnTabSelectedListener(this);

    }

    /**
     * 获取订单数据
     */
    private void getOrders() {
        String userId = BaseApplication.application.getUser().getId() + "";

        if (!TextUtils.isEmpty(userId)) {
            OkGo.<List<Order>>post(API.ORDER_LIST)
                    .tag(this)
                    .params("user_id",Long.parseLong(userId))
                    .params("status",status)
                    .params("token",BaseApplication.application.getToken())
                    .execute(new JsonCallBack<List<Order>>() {
                        @Override
                        public void SuccessData(Response<List<Order>> response) {
                            showOrders(response.body());
                        }
                    });
        }
    }

    /**
     * 显示订单数据
     *
     * @param orders
     */
    private void showOrders(List<Order> orders) {
        if (mAdapter == null) {
            mAdapter = new MyOrderAdapter(MyOrderActivity.this, orders, new MyOrderAdapter.OnItemWaresClickListener() {
                @Override
                public void onItemWaresClickListener(View v, Order order) {
                    /**
                     * 再次购买点击事件，跳转到支付页面
                     * 将商品和地址以及总金额传入
                     */
                    Intent intent = new Intent(MyOrderActivity.this, NewOrderActivity.class);
                    intent.putExtra("order", (Serializable) order.getItems());
                    intent.putExtra("sign", Constants.ORDER);
                    intent.putExtra("price", order.getAmount());
                    startActivity(intent, true);
                }
            });
            recycleView.setAdapter(mAdapter);
            recycleView.setLayoutManager(new LinearLayoutManager(this));
            recycleView.addItemDecoration(new CardViewtemDecortion());

            mAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                @Override
                public void onItemClick(View view, int position) {
                    ToastUtils.showToastShort( "功能正在完善...");
//                    toDetailActivity(position);
                }
            });
        } else {
            mAdapter.refreshData(orders);
            recycleView.setAdapter(mAdapter);
        }
    }

    private void toDetailActivity(int position) {

//        Order order = mAdapter.getItem(position);
//
//        System.out.println(order.getAmount()+"::"+order.getOrderNum()+"::"+order.getAddress().getConsignee());
    }

    /**
     * tablayout三个点击事件
     *
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        status = (int) tab.getTag();
        getOrders();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
