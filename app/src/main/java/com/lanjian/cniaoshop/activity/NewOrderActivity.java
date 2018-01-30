package com.lanjian.cniaoshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.adapter.FullyLinearLayoutManager;
import com.lanjian.cniaoshop.adapter.OrderItemAdapter;
import com.lanjian.cniaoshop.adapter.WareOrderAdapter;
import com.lanjian.cniaoshop.bean.Address;
import com.lanjian.cniaoshop.bean.OrderItem;
import com.lanjian.cniaoshop.bean.ShoppingCart;
import com.lanjian.cniaoshop.comment.BaseActivity;
import com.lanjian.cniaoshop.comment.BaseApplication;
import com.lanjian.cniaoshop.utils.AddressProvider;
import com.lanjian.cniaoshop.utils.CartProvider;
import com.lanjian.cniaoshop.utils.Constants;
import com.lanjian.cniaoshop.utils.LogUtils;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.CommonTitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewOrderActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_addr)
    TextView tvAddr;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.rl_addr)
    RelativeLayout rlAddr;
    @BindView(R.id.tv_order)
    TextView tvOrder;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.ll_items)
    LinearLayout llItems;
    @BindView(R.id.img_alipay)
    ImageView imgAlipay;
    @BindView(R.id.rb_alipay)
    RadioButton rbAlipay;
    @BindView(R.id.rl_alipay)
    RelativeLayout rlAlipay;
    @BindView(R.id.img_wechat)
    ImageView imgWechat;
    @BindView(R.id.rb_wechat)
    RadioButton rbWechat;
    @BindView(R.id.rl_wechat)
    RelativeLayout rlWechat;
    @BindView(R.id.img_bd)
    ImageView imgBd;
    @BindView(R.id.rb_bd)
    RadioButton rbBd;
    @BindView(R.id.rl_bd)
    RelativeLayout rlBd;
    @BindView(R.id.ll_pay)
    LinearLayout llPay;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.btn_createOrder)
    Button btnCreateOrder;
    private float amount;
    private String orderNum;
    private int SIGN;
    private WareOrderAdapter wareOrderAdapter;
    private OrderItemAdapter orderItemAdapter;
    /**
     * 银联支付渠道
     */
    private static final String CHANNEL_UPACP = "upacp";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 百度支付渠道
     */
    private static final String CHANNEL_BFB = "bfb";
    /**
     * 京东支付渠道
     */
    private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";
    private HashMap<String, RadioButton> channels = new HashMap<>(3);
    private String payChannel = CHANNEL_ALIPAY;//默认途径为支付宝
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        ButterKnife.bind(this);
        setToolbar();
        init();
    }

    public void init() {
        showData();
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewOrderActivity.this, AddressListActivity.class);
//                intent.putExtra("tag", Constants.TAG_ORDER_SAVE);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });

        initAddress();

       initPayChannels();
    }

    public void setToolbar() {
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 显示订单数据
     */
    public void showData() {

        SIGN = getIntent().getIntExtra("sign", -1);
        /**
         * 购物车商品数据
         */
        if (SIGN == Constants.CART) {
            List<ShoppingCart> carts = (List<ShoppingCart>) getIntent().getSerializableExtra("carts");
            wareOrderAdapter = new WareOrderAdapter(this, carts);
            FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
            layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
            recycleView.setLayoutManager(layoutManager);
            recycleView.setAdapter(wareOrderAdapter);
            /**
             * 我的订单再次购买点击商品显示
             */
        } else if (SIGN == Constants.ORDER) {
            List<OrderItem> orderItems = (List<OrderItem>) getIntent().getSerializableExtra("order");
            orderItemAdapter = new OrderItemAdapter(this, orderItems);
            FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
            layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
            recycleView.setLayoutManager(layoutManager);
            recycleView.setAdapter(orderItemAdapter);
        }

    }

    //请求服务端获取地址
    private void initAddress() {
        String userId = BaseApplication.application.getUser().getId() + "";

        if (!TextUtils.isEmpty(userId)) {
            showAddress(AddressProvider.getInstance(this).getAll());
        } else {
            ToastUtils.showToastShort("加载错误...");
        }
    }


    /**
     * 显示默认地址
     *
     * @param addresses
     */
    private void showAddress(List<Address> addresses) {

        /**
         * 购物车页面传递的数据显示地址
         */
        if (SIGN == Constants.CART) {
            for (Address address : addresses) {
                if (address.getIsDefault()) {
                    tvName.setText(address.getConsignee() + "(" + address.getPhone() + ")");
                    tvAddr.setText(address.getAddr());
                }
            }
            /**
             * 我的订单页面显示地址
             */
        } else if (SIGN == Constants.ORDER) {
            Address addressOrder = (Address) getIntent().getSerializableExtra("address");
            if (addressOrder != null) {
                System.out.println(addressOrder.getConsignee() + "::" + addressOrder.getPhone() + "::" + addressOrder.getAddr());
                tvName.setText(addressOrder.getConsignee() + "(" + addressOrder.getPhone() + ")");
                tvAddr.setText(addressOrder.getAddr());
            } else {//显示默认地址
                for (Address address : addresses) {
                    if (address.getIsDefault()) {
                        tvName.setText(address.getConsignee() + "(" + address.getPhone() + ")");
                        tvAddr.setText(address.getAddr());
                    }
                }
            }
        }

    }

    private void initPayChannels() {
        //保存RadioButton
        channels.put(CHANNEL_ALIPAY, rbAlipay);
        channels.put(CHANNEL_WECHAT, rbWechat);
        channels.put(CHANNEL_BFB, rbBd);

        rbAlipay.setOnClickListener(this);
        rbWechat.setOnClickListener(this);
        rbBd.setOnClickListener(this);


        if (SIGN == Constants.CART) {
            amount = wareOrderAdapter.getTotalPrice();
        } else if (SIGN == Constants.ORDER) {
//            amount = getIntent().getFloatExtra("price",-0.1f);

            amount = orderItemAdapter.getTotalPrice();

            System.out.println("price:::" + amount);
        }
        tvTotal.setText("应付款：￥" + amount);
    }

    @Override
    public void onClick(View v) {
        selectPayChannel(v.getTag().toString());
    }

    /**
     * 选择支付渠道以及RadioButton互斥功能
     *
     * @param payChannel
     */
    private void selectPayChannel(String payChannel) {

        for (Map.Entry<String, RadioButton> entry : channels.entrySet()) {

            this.payChannel = payChannel;


            //获取的RadioButton
            RadioButton rb = entry.getValue();

            //如果当前RadioButton被点击
            if (entry.getKey().equals(payChannel)) {

                LogUtils.e("payChannel=" + payChannel);
                //被选中
                rb.setChecked(true);

            } else {
                //其他的都改为未选中
                rb.setChecked(false);
            }
        }
    }


    /**
     * 提交订单
     *
     * @param view
     */
    public void postNewOrder(View view) {

        List<WareItem> items = new ArrayList<>();

        //判断购物车还是再次购买订单返回
        if (SIGN == Constants.CART) {
            postOrderByCart(items);
        } else if (SIGN == Constants.ORDER) {
            postOrderByMyOrder(items);
        }

    }

    private void postOrderByMyOrder(List<WareItem> items) {
        ToastUtils.showToastShort("正在完善中");
    }

    /**
     * 商品id和价格显示适配器
     */
    class WareItem {
        private Long ware_id;
        private int amount;

        public WareItem(Long ware_id, int amount) {
            this.ware_id = ware_id;
            this.amount = amount;
        }

        public Long getWare_id() {
            return ware_id;
        }

        public void setWare_id(Long ware_id) {
            this.ware_id = ware_id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    /**
     * 提交购物车订单
     *
     * @param items 商品集合
     */
    private void postOrderByCart(List<WareItem> items) {
        final List<ShoppingCart> carts = wareOrderAdapter.getDatas();
        //获取购物车数据
        for (ShoppingCart cart : carts) {
            WareItem item = new WareItem(cart.getId(), (int) Float.parseFloat(cart.getPrice()));
            items.add(item);
        }
        ToastUtils.showToastShort("正在完善中...");

        /**
         * 清空已购买商品
         */
        if (SIGN == Constants.CART) {
            CartProvider mCartProvider = CartProvider.getInstance(NewOrderActivity.this);
            mCartProvider.delete(carts);
        }
    }
}
