package com.lanjian.cniaoshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.bean.Address;
import com.lanjian.cniaoshop.bean.JsonBean;
import com.lanjian.cniaoshop.comment.BaseApplication;
import com.lanjian.cniaoshop.msg.BaseResMsg;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.AddressProvider;
import com.lanjian.cniaoshop.utils.Constants;
import com.lanjian.cniaoshop.utils.GetJsonDataUtil;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.ClearEditText;
import com.lanjian.cniaoshop.weight.CommonTitleView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressAddActivity extends AppCompatActivity {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.et_consignee)
    ClearEditText etConsignee;
    @BindView(R.id.et_phone)
    ClearEditText etPhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.ll_city_picker)
    LinearLayout llCityPicker;
    @BindView(R.id.et_add_des)
    ClearEditText etAddDes;
    private int TAG;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);
        ButterKnife.bind(this);
        init();
        setToolbar();
    }

    private void setToolbar() {
        /**
         * 根据传入的TAG，toolbar显示相应布局
         */
        TAG = getIntent().getIntExtra("tag", -1);

        final Address address = (Address) getIntent().getExtras().getSerializable("addressBean");
        TextView rightView = (TextView) titleView.findViewById(R.id.right_iv);
        TextView title = (TextView) titleView.findViewById(R.id.title_tv);
        if (TAG == Constants.TAG_SAVE) {
            rightView.setText("保存");
            title.setText("添加新地址");
        } else if (TAG == Constants.TAG_COMPLETE) {
           rightView.setText("完成");
            title.setText("编辑地址");
            showAddress(address);
        }
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TAG == Constants.TAG_SAVE) {
                    //添加新地址
                    creatAddress();
                } else if (TAG == Constants.TAG_COMPLETE) {
                    final Address address = (Address) getIntent().getExtras().getSerializable("addressBean");
                    //编辑地址
                    updateAddress(address);
                }
                finish();
            }
        });
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void init() {
        context = AddressAddActivity.this;

        /**
         * 初始化省市数据
         */
        initJsonData();
    }

    /**
     * 显示添加地址页面
     */
    private void showAddress(Address address) {
        String addrArr[] = address.getAddr().split("-");
        etConsignee.setText(address.getConsignee());
        etPhone.setText(address.getPhone());
        tvAddress.setText(addrArr[0] == null ? "" : addrArr[0]);
        etAddDes.setText(addrArr[1] == null ? "" : addrArr[1]);
    }

    /**
     * 编辑地址
     */
    public void updateAddress(Address address) {
        check();

        String consignee = etConsignee.getText().toString();
        String phone = etPhone.getText().toString();
        String addr = tvAddress.getText().toString() + "-" + etAddDes.getText().toString();
        Address as = new Address(address.getId(),consignee,phone,addr,"000000");
        AddressProvider.getInstance(this).update(as);
        OkGo.<BaseResMsg>post(API.ADDR_UPDATE)
                .tag(this)
                .params("id",address.getId())
                .params("consignee",consignee)
                .params("phone",phone)
                .params("addr",addr)
                .params("zip_code",address.getZip_code())
                .params("is_default",address.getIsDefault())
                .params("token",BaseApplication.application.getToken())
                .execute(new JsonCallBack<BaseResMsg>() {
                    @Override
                    public void SuccessData(Response<BaseResMsg> response) {
                        if (response.body().getStatus() == BaseResMsg.STATUS_SUCCESS) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == Constants.ADDRESS_ADD){//添加地址
                creatAddress();
            }else if (requestCode == Constants.ADDRESS_EDIT){//编辑地址
                Address address = (Address) getIntent().getExtras().getSerializable("addressBean");
                updateAddress(address);
            }
        }
    }

    /**
     * 检查是否为空
     */
    private void check() {
        String name = etConsignee.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = tvAddress.getText().toString();
        String address_des = etAddDes.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            ToastUtils.showToastShort("请输入收件人姓名");
        }else if (TextUtils.isEmpty(phone)){
            ToastUtils.showToastShort("请输入收件人电话");
        }else if (TextUtils.isEmpty(address)){
            ToastUtils.showToastShort("请选择地区");
        }else if (TextUtils.isEmpty(address_des)){
            ToastUtils.showToastShort("请输入具体地址");
        }
    }

    /**
     * 添加新地址
     */
    private void creatAddress() {
        check();

        String consignee = etConsignee.getText().toString();
        String phone = etPhone.getText().toString();
        String address = tvAddress.getText().toString() + "-" + etAddDes.getText().toString();

        if (checkPhone(phone)) {
            Address addr = new Address(System.currentTimeMillis(),consignee,phone,address,"000000");
            AddressProvider.getInstance(this).put(addr);
            String userId = BaseApplication.application.getUser().getId() + "";
            String token = BaseApplication.application.getToken();
            OkGo.<BaseResMsg>post(API.ADDR_CREATE)
                    .tag(this)
                    .params("user_id",Long.parseLong(userId))
                    .params("consignee",consignee)
                    .params("phone",phone)
                    .params("addr",address)
                    .params("zip_code","000000")
                    .params("token",token)
                    .execute(new JsonCallBack<BaseResMsg>() {
                        @Override
                        public void SuccessData(Response<BaseResMsg> response) {
                            if (response.body().getStatus() == BaseResMsg.STATUS_SUCCESS) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                    });
        }

    }

    /**
     * 检验手机号码
     *
     * @param phone
     * @return
     */
    private boolean checkPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToastShort( "请输入手机号码");
            return false;
        }
        if (phone.length() != 11) {
            ToastUtils.showToastShort("手机号码长度不对");
            return false;
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            ToastUtils.showToastShort("您输入的手机号码格式不正确");
            return false;
        }

        return true;
    }

    private void ShowPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(AddressAddActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String address = options1Items.get(options1).getPickerViewText()+
                        options2Items.get(options1).get(options2)+
                        options3Items.get(options1).get(options2).get(options3);

                tvAddress.setText(address);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this,"province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i=0;i<jsonBean.size();i++){//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c=0; c<jsonBean.get(i).getCityList().size(); c++){//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        ||jsonBean.get(i).getCityList().get(c).getArea().size()==0) {
                    City_AreaList.add("");
                }else {

                    for (int d=0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

    }


    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @OnClick(R.id.ll_city_picker)
    public void showCityPickerView(View v) {
        //确认省市数据
        ShowPickerView();
    }
}
