package com.lanjian.cniaoshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.bean.User;
import com.lanjian.cniaoshop.comment.BaseApplication;
import com.lanjian.cniaoshop.msg.LoginRespMsg;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.Constants;
import com.lanjian.cniaoshop.utils.DESUtil;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.ClearEditText;
import com.lanjian.cniaoshop.weight.CommonTitleView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.et_phone)
    ClearEditText etPhone;
    @BindView(R.id.et_pwd)
    ClearEditText etPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_forget_pwd)
    TextView tvForgetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_login, R.id.tv_register, R.id.tv_forget_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login(view);
                break;
            case R.id.tv_register:
                register(view);
                break;
            case R.id.tv_forget_pwd:
                break;
        }
    }

    public void login(View view) {
        String phone = etPhone.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToastShort("请输入手机号码");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showToastShort( "请输入登录密码");
            return;
        }

        OkGo.<LoginRespMsg<User>>post(API.AUTH_LOGIN)
                .tag(this)
                .params("phone",phone)
                .params("password",DESUtil.encode(Constants.DES_KEY, pwd))
                .execute(new JsonCallBack<LoginRespMsg<User>>() {
                    @Override
                    public void SuccessData(Response<LoginRespMsg<User>> response) {
                        LoginRespMsg<User> result = response.body();
                        BaseApplication application = BaseApplication.application;
                        /**
                         * 根据登录意图判断是否已经登录
                         */
                        if (application.getIntent() == null && result.getData() != null && result.getToken() != null) {
                            application.putUser(result.getData(), result.getToken());

                            setResult(Constants.REQUEST_CODE);

                            ToastUtils.showToastShort( "登录成功");

                            finish();
                        } else {
                            ToastUtils.showToastShort( "登录失败");
                        }
                    }
                });

    }

    /**
     * 跳转到注册页面
     *
     * @param v
     */
    public void register(View v) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 1);
//        startActivity(new Intent(this, RegisterActivity.class));
    }
}
