package com.lanjian.cniaoshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.utils.LogUtils;
import com.lanjian.cniaoshop.utils.ManifestUtil;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.ClearEditText;
import com.lanjian.cniaoshop.weight.CommonTitleView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class RegisterActivity extends AppCompatActivity {

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
    private SMSEventHandler eventHandler;
    private String TAG = "RegisterActivity";
    private static final String DEFAULT_COUNTRY_ID = "42";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        setToolbar();
        SMSSDK.initSDK(this, ManifestUtil.getMetaDataValue(this, "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(this, "mob_sms_appSectret"));

        eventHandler = new SMSEventHandler();
        SMSSDK.registerEventHandler(eventHandler);


        /**
         * 获取国家代码
         */
        String[] country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        if (country != null) {
            tvCountry.setText(country[0]);
            tvCountryCode.setText("+" + country[1]);
        }
    }

    public void setToolbar() {
        TextView rightView = (TextView) titleView.findViewById(R.id.right_iv);
        rightView.setText(getString(R.string.register_next));
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
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
    private void checkPhoneNum(String phone, String code) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToastShort( "请输入手机号码");
            return;
        }

        if (code == "86") {
            if (phone.length() != 11) {
                ToastUtils.showToastShort("手机号码长度不正确");
                return;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            ToastUtils.showToastShort("您输入的手机号码格式不正确");
            return;
        }
    }

    /**
     * 提交手机号码和国家代码
     */
    private void getCode() {
        String phone = etPhone.getText().toString().trim().replaceAll("\\s*", "");
        String countryCode = tvCountryCode.getText().toString().trim();

        checkPhoneNum(phone, countryCode);

        //请求验证码，如果请求成功，则在EventHandler中回调并跳转到下一个注册页面
        SMSSDK.getVerificationCode(countryCode, phone);
    }


//    //通过SIM卡获取国家
//    private String[] getCurrentCountry() {
//        String mcc = this.getMCC();
//        String[] country = null;
//        if (!TextUtils.isEmpty(mcc)) {
//            country = SMSSDK.getCountryByMCC(mcc);
//        }
//
//        if (country == null) {
//            Log.w("SMSSDK", "no country found by MCC: " + mcc);
//            country = SMSSDK.getCountry("42");
//        }
//
//        return country;
//    }
//
//    private String getMCC() {
//        TelephonyManager tm = (TelephonyManager) this.getSystemService("phone");
//        String networkOperator = tm.getNetworkOperator();
//        return !TextUtils.isEmpty(networkOperator) ? networkOperator : tm.getSimOperator();
//    }


    class SMSEventHandler extends EventHandler {

        @Override
        public void afterEvent(final int event, final int result, final Object data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {

                            onCountryListGot((ArrayList<HashMap<String, Object>>) data);

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {

                            //请求验证码后，跳转到验证码填写页面
                            afterVerificationCodeRequested((Boolean) data);
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                        }
                    } else {

                        //根据服务器返回的网络错误，给toast提示

                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = null;
                            object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                ToastUtils.showToastShort(des);
                                return;
                            }
                        } catch (Exception e) {
                            LogUtils.e(e.toString());
                        }
                    }
                }
            });
        }
    }

    /**
     * 获取国家代码
     *
     * @param countries
     */
    private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
        for (HashMap<String, Object> country : countries) {
            String code = (String) country.get("zone");
            String rule = (String) country.get("rule");

            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }

            Log.d(TAG, "code=" + code + ",rule=" + rule);
        }
    }

    /**
     * 传入国家代码，手机号码，密码并请求短信验证码，跳转到验证码填写页面
     */
    private void afterVerificationCodeRequested(boolean smart) {

        String phone = etPhone.getText().toString().trim().replaceAll("\\s*", "");
        String countryCode = tvCountryCode.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();

        if (etPwd.getText().toString().length() < 6 || etPwd.getText().toString().length() > 20) {
            ToastUtils.showToastShort("密码长度必须大于6位小于20位");
            return;
        }

        if (countryCode.startsWith("+")) {
            countryCode = countryCode.substring(1);
        }

        Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("pwd", pwd);
        intent.putExtra("countryCode", countryCode);

        startActivityForResult(intent,1);
        setResult(2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SMSSDK.unregisterEventHandler(eventHandler);
    }
}
