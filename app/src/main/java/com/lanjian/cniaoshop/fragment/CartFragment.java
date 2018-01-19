package com.lanjian.cniaoshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.adapter.CartAdapter;
import com.lanjian.cniaoshop.adapter.DividerItemDecortion;
import com.lanjian.cniaoshop.bean.ShoppingCart;
import com.lanjian.cniaoshop.utils.CartProvider;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.CommonTitleView;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by Ivan on 15/9/22.
 */
public class CartFragment extends Fragment {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.recyclerview_cart)
    RecyclerView recyclerviewCart;
    @BindView(R.id.checkbox_all)
    CheckBox checkboxAll;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.btn_order)
    Button btnOrder;
    @BindView(R.id.btn_del)
    Button btnDel;
    Unbinder unbinder;
    private static final int ACTION_EDIT = 1;
    private static final int ACTION_CAMPLATE = 2;
    private CartProvider mCartProvider;
    private CartAdapter mAdapter;
    private TextView rightTitle;
    private int action = ACTION_EDIT;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        rightTitle = titleView.findViewById(R.id.right_iv);
        rightTitle.setText(R.string.edit);
        titleView.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTION_EDIT == action) {
                    showDelControl();

                } else{//完成
                    hideDelControl();
                }
            }
        });
        mCartProvider = CartProvider.getInstance(getContext());
        showData();
    }

    /**
     * 隐藏删除按钮
     */
    private void hideDelControl() {
        rightTitle.setText("编辑");
        tvTotal.setVisibility(View.VISIBLE);

        btnDel.setVisibility(View.GONE);
        //设置为编辑
        action = ACTION_EDIT;
        mAdapter.checkAll_None(true);
        checkboxAll.setChecked(true);
        mAdapter.showTotalPrice();
    }

    /**
     * 显示删除按钮
     */
    private void showDelControl() {
        rightTitle.setText(R.string.finish);
        tvTotal.setVisibility(View.GONE);
        btnDel.setVisibility(View.VISIBLE);
        //设置为完成
        action = ACTION_CAMPLATE;
        mAdapter.checkAll_None(false);
        checkboxAll.setChecked(false);
    }


    /**
     * 显示购物车数据
     */
    private void showData() {
        List<ShoppingCart> carts = mCartProvider.getAll();
        mAdapter = new CartAdapter(getContext(), carts, checkboxAll, tvTotal);
        recyclerviewCart.setAdapter(mAdapter);
        recyclerviewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerviewCart.addItemDecoration(new DividerItemDecortion(getContext(), DividerItemDecortion.VERTICAL_LIST));

    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        if (mAdapter!=null) {
            mAdapter.clearData();
            List<ShoppingCart> carts = mCartProvider.getAll();
            mAdapter.addData(carts);

            mAdapter.showTotalPrice();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_del)
    public void delCart(View v) {
        mAdapter.delCart();
    }


    /**
     * 结算按钮点击事件
     *
     * @param v
     */
    @OnClick(R.id.btn_order)
    public void toOrder(View v) {
/*
        if (mAdapter.getCheckData() != null && mAdapter.getCheckData().size() > 0) {
            Intent intent = new Intent(getActivity(), NewOrderActivity.class);
            intent.putExtra("carts", (Serializable) mAdapter.getCheckData());
            intent.putExtra("sign", Constants.CART);
            startActivity(intent, true);
        } else {
            ToastUtils.show(getContext(), "请选择要购买的商品");
        }*/
    }
}
