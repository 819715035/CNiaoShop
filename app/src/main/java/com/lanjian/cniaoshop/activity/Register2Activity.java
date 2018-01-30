package com.lanjian.cniaoshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
import com.lanjian.cniaoshop.utils.CountTimerView;
import com.lanjian.cniaoshop.utils.DESUtil;
import com.lanjian.cniaoshop.utils.LogUtils;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.ClearEditText;
import com.lanjian.cniaoshop.weight.CommonTitleView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import dmax.dialog.SpotsDialog;

public class Register2Activity extends AppCompatActivity {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.et_code)
    ClearEditText etCode;
    @BindView(R.id.btn_reSend)
    Button btnReSend;

    private SpotsDialog mDialog;

    private String phone;
    private String countryCode;
    private String pwd;
    private CountTimerView timerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        ButterKnife.bind(this);
        setToolbar();
        init();
    }

    public void init() {
        //获取手机号码，密码，验证码
        phone = getIntent().getStringExtra("phone");
        pwd = getIntent().getStringExtra("pwd");
        countryCode = getIntent().getStringExtra("countryCode");
        String formatedPhone = "+" + countryCode + " " + splitPhoneNum(phone);
        String tip = "验证码已经发送到" + formatedPhone;
        tvTip.setText(Html.fromHtml(tip));
        //倒计时功能
        timerView = new CountTimerView(btnReSend);
        mDialog = new SpotsDialog(this, "正在校验验证码");
        SMSSDK.registerEventHandler(new SMSEventHandler());
    }

    public void setToolbar() {
        TextView rightView = (TextView) titleView.findViewById(R.id.right_iv);
        rightView.setText("完成");
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
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
     * 提交验证信息
     */
    private void submitCode() {

        //获取验证码
        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showToastShort("验证码不能为空");
            return;
        }
        //提交验证信息，提交之后会在EventHandler回调提交成功处理
        submitCode(countryCode, phone, code);
        mDialog.show();
    }

    /**
     * 分割电话号码
     *
     * @param phone
     * @return
     */
    private String splitPhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        //每四个用空格进行切割
        for (int i = 4; i < builder.length(); i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();

        System.out.println("builder=" + builder.toString());

        return builder.toString();
    }

    // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
    public void sendCode(String country, String phone) {
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果

        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    /**
     * 注册
     */
    private void doRegister() {
        OkGo.<LoginRespMsg<User>>post(API.AUTH_REG)
                .tag(this)
                .params("phone", phone)
                .params("password", DESUtil.encode(Constants.DES_KEY, pwd))
                .execute(new JsonCallBack<LoginRespMsg<User>>() {
                    @Override
                    public void SuccessData(Response<LoginRespMsg<User>> response) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        LoginRespMsg<User> userLoginRespMsg = response.body();
                        //注册失败
                        if (userLoginRespMsg.getStatus() == LoginRespMsg.STATUS_ERROR) {
                            ToastUtils.showToastShort("注册失败" + userLoginRespMsg.getMessage());
                            return;
                        }
                        //token为null，已经注册
                        if (TextUtils.isEmpty(userLoginRespMsg.getToken())) {
                            ToastUtils.showToastShort("您已经注册");
                            return;
                        }
                        //保存用户信息
                        BaseApplication.application.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());
                        ToastUtils.showToastShort("注册成功");
                        //跳转到登录页面
                        startActivity(new Intent(Register2Activity.this, MainActivity.class));
                    }

                    @Override
                    public void onError(Response<LoginRespMsg<User>> response) {
                        super.onError(response);
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        ToastUtils.showToastShort("注册失败");
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
    }

    @OnClick(R.id.btn_reSend)
    public void onViewClicked() {
        sendCode(countryCode,phone);
        timerView.start();
    }

    class SMSEventHandler extends EventHandler {

        @Override
        public void afterEvent(final int event, final int result, final Object data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mDialog != null && mDialog.isShowing())
                        mDialog.dismiss();

                    /**
                     * 请求验证码回调
                     */
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        /**
                         * 注册回调
                         */
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                            //回调验证信息
                            doRegister();

                            mDialog.setMessage("正在提交验证信息");
                            mDialog.show();

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {

                            if (mDialog != null && mDialog.isShowing())
                                mDialog.dismiss();
                        }
                    } else {
                        //根据服务器返回的网络错误，给toast提示
                        ToastUtils.showToastShort("短信接口发生错误");
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = null;
                            object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                LogUtils.e(des);
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                            LogUtils.e(e.toString());
                        }
                    }
                }
            });
        }
    }
}
