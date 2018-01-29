package com.lanjian.cniaoshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.activity.AddressListActivity;
import com.lanjian.cniaoshop.activity.LoginActivity;
import com.lanjian.cniaoshop.activity.MyFavoriteActivity;
import com.lanjian.cniaoshop.activity.MyOrderActivity;
import com.lanjian.cniaoshop.bean.User;
import com.lanjian.cniaoshop.comment.BaseApplication;
import com.lanjian.cniaoshop.utils.Constants;
import com.lanjian.cniaoshop.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Ivan on 15/9/22.
 */
public class MineFragment extends Fragment {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_my_order)
    TextView tvMyOrder;
    @BindView(R.id.tv_favorite)
    TextView tvFavorite;
    @BindView(R.id.tv_addr)
    TextView tvAddr;
    @BindView(R.id.btn_loginOut)
    Button btnLoginOut;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        initUser();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.profile_image, R.id.tv_username})
    public void onViewClicked(View view) {
        /**
         * 判断是否已经登录，若已登录，则提示，未登录，则跳转
         */
        User user = BaseApplication.application.getUser();
        if (user != null) {
            ToastUtils.showToastShort("您已登录");
            profileImage.setClickable(false);
            tvUsername.setClickable(false);
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
        }
    }

    /**
     * 退出登录
     * @param view
     */
    @OnClick(R.id.btn_loginOut)
    public void loginOut(View view) {
        BaseApplication.application.clearUser();
        showUser(null);
    }

    /**
     * 登录跳转返回结果
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        initUser();
    }

    /**
     * 刚进入我的页面就要初始化用户数据
     */
    private void initUser() {
        User user = BaseApplication.application.getUser();
        showUser(user);
    }
    /**
     * 显示用户数据
     * @param user
     */
    private void showUser(User user) {

        if (user != null) {
            tvUsername.setText(user.getUsername());

            if (!TextUtils.isEmpty(user.getLogo_url()))
                Glide.with(getActivity()).load(user.getLogo_url()).into(profileImage);

            System.out.println("Username------------"+user.getUsername());
            btnLoginOut.setVisibility(View.VISIBLE);
        } else {
            tvUsername.setText(R.string.to_login);
            btnLoginOut.setVisibility(View.GONE);
            profileImage.setClickable(true);
            tvUsername.setClickable(true);
        }

    }

    /**
     * 地址按钮点击事件
     * @param view
     */
    @OnClick(R.id.tv_addr)
    public void showAddress(View view) {
        Intent intent = new Intent(getActivity(), AddressListActivity.class);
        startActivity(intent, true);
    }

    /**
     * 我的订单显示。需先判断是否已经登录
     * @param view
     */
    @OnClick(R.id.tv_my_order)
    public void showMyOrder(View view){
        Intent intent = new Intent(getActivity(), MyOrderActivity.class);
        startActivity(intent, true);
    }

    /**
     * 收藏夹点击事件
     * @param view
     */
    @OnClick(R.id.tv_favorite)
    public void showMyFavorite(View view){
        Intent intent = new Intent(getActivity(), MyFavoriteActivity.class);
        startActivity(intent, true);
    }

    /**
     * 启动目标activity
     * @param intent 跳转意图
     * @param isNeedLogin 是否需要登录
     */
    public void startActivity(Intent intent, boolean isNeedLogin) {

        if (isNeedLogin) {
            User user = BaseApplication.application.getUser();

            if (user != null) {
                super.startActivity(intent);
            } else {
                BaseApplication.application.putIntent(intent);
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                super.startActivity(loginIntent);
            }
        }else {
            super.startActivity(intent);
        }
    }
}
