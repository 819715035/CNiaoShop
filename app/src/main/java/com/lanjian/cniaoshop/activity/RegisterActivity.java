package com.lanjian.cniaoshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.comment.BaseActivity;
import com.lanjian.cniaoshop.utils.LogUtils;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.ClearEditText;
import com.lanjian.cniaoshop.weight.CommonTitleView;
import com.mob.MobSDK;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.tv_Country)
    TextView tvCountry;
    @BindView(R.id.tv_country_code)
    TextView tvCountryCode;
    @BindView(R.id.et_phone)
    ClearEditText etPhone;
    @BindView(R.id.et_pwd)
    ClearEditText etPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        setToolbar();
    }

    public void setToolbar() {
        TextView rightView = (TextView) titleView.findViewById(R.id.right_iv);
        rightView.setText(getString(R.string.register_next));
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim().replaceAll("\\s*", "");
                String countryCode = tvCountryCode.getText().toString().trim();

                if (checkPhoneNum(phone, countryCode)){
                    afterVerificationCodeRequested();
                }
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
     * 验证手机号码合法性
     *
     * @param phone
     * @param code
     */
    private boolean checkPhoneNum(String phone, String code) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToastShort( "请输入手机号码");
            return false;
        }

        if (code == "86") {
            if (phone.length() != 11) {
                ToastUtils.showToastShort( "手机号码长度不正确");
                return false;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            ToastUtils.showToastShort( "您输入的手机号码格式不正确");
            return false;
        }
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 传入国家代码，手机号码，密码并请求短信验证码，跳转到验证码填写页面
     */
    private void afterVerificationCodeRequested() {

        String phone = etPhone.getText().toString().trim().replaceAll("\\s*", "");
        String countryCode = tvCountryCode.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();

        if (etPwd.getText().toString().length() < 6 || etPwd.getText().toString().length() > 20) {
            ToastUtils.showToastShort( "密码长度必须大于6位小于20位");
            return;
        }

        if (countryCode.startsWith("+")) {
            countryCode = countryCode.substring(1);
        }

        Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("pwd", pwd);
        intent.putExtra("countryCode", countryCode);

        startActivity(intent);
    }
}
