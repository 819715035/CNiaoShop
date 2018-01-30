package com.lanjian.cniaoshop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.adapter.AddressAdapter;
import com.lanjian.cniaoshop.adapter.DividerItemDecortion;
import com.lanjian.cniaoshop.bean.Address;
import com.lanjian.cniaoshop.comment.BaseApplication;
import com.lanjian.cniaoshop.msg.BaseResMsg;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.AddressProvider;
import com.lanjian.cniaoshop.utils.Constants;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.CommonTitleView;
import com.lanjian.cniaoshop.weight.CustomDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressListActivity extends AppCompatActivity {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private AddressAdapter mAdapter;
    private CustomDialog mDialog;
    private List<Address> addresss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initAddress();
        setToolbar();
    }

    private void setToolbar() {
        TextView rightView = (TextView) titleView.findViewById(R.id.right_iv);
        rightView.setBackgroundResource(R.mipmap.icon_add_w);
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddActivity();
            }
        });
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 显示删除提示对话框
     *
     * @param address
     */
    private void showDialog(final Address address) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您确定删除该地址吗？");
        builder.setTitle("友情提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteAddress(address);
                initAddress();

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

    /**
     * 删除地址
     *
     * @param address
     */
    private void deleteAddress(Address address) {

        AddressProvider.getInstance(this).delete(address);
        mAdapter.refreshData(AddressProvider.getInstance(this).getAll());
        OkGo.<BaseResMsg>post(API.ADDR_DEL)
                .tag(this)
                .params("id",address.getId())
                .params("token", BaseApplication.application.getToken())
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

    /**
     * 跳转到添加地址页面
     * 点击右上角添加按钮，传入TAG_SAVE,更改添加地址页面toolbar显示
     */
    private void toAddActivity() {
        Intent intent = new Intent(this, AddressAddActivity.class);
        intent.putExtra("tag", Constants.TAG_SAVE);
        startActivityForResult(intent, Constants.ADDRESS_ADD);
    }

    /**
     * 跳转AddressAddActivity页面结果处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initAddress();
    }

    /**
     * 初始化地址页面
     */
    private void initAddress() {
        String userId = BaseApplication.application.getUser().getId() + "";
        addresss = AddressProvider.getInstance(this).getAll();
        if (addresss!=null && addresss.size()>0){
            showAddress(addresss);
        }
        if (!TextUtils.isEmpty(userId)) {
            OkGo.<List<Address>>get(API.ADDR_LIST)
                    .tag(this)
                    .params("user_id",Long.parseLong(userId))
                    .params("token",BaseApplication.application.getToken())
                    .execute(new JsonCallBack<List<Address>>() {
                        @Override
                        public void SuccessData(Response<List<Address>> response) {
                            showAddress(response.body());
                        }
                    });
        } else {
            ToastUtils.showToastShort( "加载错误...");
        }
    }

    /**
     * 显示地址列表
     *
     * @param addresses
     */
    private void showAddress(final List<Address> addresses) {

        Collections.sort(addresses);
        if (mAdapter == null) {
            mAdapter = new AddressAdapter(this, addresses, new AddressAdapter.AddressLisneter() {
                @Override
                public void setDefault(Address address) {
                    setResult(RESULT_OK);
                    //更改地址
                    AddressProvider.getInstance(AddressListActivity.this).updateDefault(address);
                }

                @Override
                public void onClickEdit(Address address) {
                    editAddress(address);
                }

                @Override
                public void onClickDelete(Address address) {
                    showDialog(address);
                    mDialog.show();
                }
            });
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
            recyclerView.addItemDecoration(new DividerItemDecortion(this, DividerItemDecortion.VERTICAL_LIST));
        } else {
            mAdapter.refreshData(addresses);
            recyclerView.setAdapter(mAdapter);
        }

    }

    /**
     * 编辑地址
     * 传入TAG_COMPLETE更改AddressAddActivitytoolbar显示
     *
     * @param address
     */
    private void editAddress(Address address) {
        Intent intent = new Intent(this, AddressAddActivity.class);
        intent.putExtra("tag", Constants.TAG_COMPLETE);
        intent.putExtra("addressBean", address);

        startActivityForResult(intent, Constants.ADDRESS_EDIT);
    }

    /**
     * 更新地址
     *
     * @param address
     */
    public void updateAddress(Address address) {
        initAddress();
        AddressProvider.getInstance(this).update(address);
        OkGo.<BaseResMsg>post(API.ADDR_UPDATE)
                .tag(this)
                .params("id",address.getId())
                .params("consignee",address.getConsignee())
                .params("phone",address.getPhone())
                .params("addr",address.getAddr())
                .params("zip_code",address.getZip_code())
                .params("is_default",address.getIsDefault())
                .params("token",BaseApplication.application.getToken())
                .execute(new JsonCallBack<BaseResMsg>() {
                    @Override
                    public void SuccessData(Response<BaseResMsg> response) {
                        if (response.body().getStatus() == response.body().STATUS_SUCCESS) {
                            //从服务端更新地址
                            initAddress();
                        }
                    }
                });
    }
}
