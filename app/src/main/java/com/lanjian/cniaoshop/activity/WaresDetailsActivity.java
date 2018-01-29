package com.lanjian.cniaoshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.bean.User;
import com.lanjian.cniaoshop.bean.Wares;
import com.lanjian.cniaoshop.comment.BaseApplication;
import com.lanjian.cniaoshop.msg.BaseResMsg;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.CartProvider;
import com.lanjian.cniaoshop.utils.CollectionProvider;
import com.lanjian.cniaoshop.utils.Constants;
import com.lanjian.cniaoshop.utils.ToastUtils;
import com.lanjian.cniaoshop.weight.CommonTitleView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class WaresDetailsActivity extends AppCompatActivity {

    @BindView(R.id.title_view)
    CommonTitleView titleView;
    @BindView(R.id.webView)
    WebView webView;
    private WebAppInterface mAppInterface;
    private WebClient mWebClient;
    private Wares mWares;
    private CartProvider mCartProvider;
    private SpotsDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wares_details);
        ButterKnife.bind(this);
        init();
    }

    /**
     * 初始化WebView
     * 1.设置允许执⾏JS脚本：
     * webSettings.setJavaScriptEnabled(true);
     * 2.添加通信接口
     * webView.addJavascriptInterface(Interface,”InterfaceName”)
     * 3.JS调⽤Android
     * InterfaceName.MethodName
     * 4.Android调⽤JS
     * webView.loadUrl("javascript:functionName()");
     */
    private void initWebView() {
        WebSettings settings = webView.getSettings();
        //1、设置允许执行Js脚本
        settings.setJavaScriptEnabled(true);
        //默认为true，无法加载页面图片
        settings.setBlockNetworkImage(false);
        //设置允许缓存
        settings.setAppCacheEnabled(true);

        webView.loadUrl(API.WARES_DETAILS);

        mAppInterface = new WebAppInterface(this);
        mWebClient = new WebClient();

        //2.添加通信接口 name和web页面名称一致
        webView.addJavascriptInterface(mAppInterface, "appInterface");

        webView.setWebViewClient(mWebClient);
    }

    public void init() {
        Serializable serializable = getIntent().getSerializableExtra(Constants.WARES);
        if (serializable == null)
            this.finish();

        mDialog = new SpotsDialog(this, "loading...");
        mDialog.show();

        mWares = (Wares) serializable;
        mCartProvider = CartProvider.getInstance(this);

        initWebView();
        setToolbar();
    }

    public void setToolbar() {
        TextView rightView = (TextView) titleView.findViewById(R.id.right_iv);
        rightView.setText(getString(R.string.share));
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
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
     * 显示分享界面
     */
    private void showShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,API.WARES_DETAILS);
        startActivity(Intent.createChooser(shareIntent, "分享到"));//设置分享列表的标题
    }


    /**
     * 页面加载完之后才调用方法进行显示数据
     * 需要实现一个监听判断页面是否加载完
     */
    class WebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
            //显示详情
            mAppInterface.showDetail();
        }

    }


    /**
     * 定义接口进行通讯
     */
    class WebAppInterface {

        private Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        /**
         * 方法名和js代码中必须一直
         * 显示详情页
         */
        @JavascriptInterface
        private void showDetail() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //调用js代码
                    webView.loadUrl("javascript:showDetail(" + mWares.getId() + ")");
                }
            });
        }

        /**
         * 添加到购物车
         *
         * @param id 商品id
         */
        @JavascriptInterface
        public void buy(long id) {
            mCartProvider.put(mWares);

            ToastUtils.showToastShort(getString(R.string.has_add_cart));

        }

        /**
         * 添加到收藏夹
         *
         * @param id 商品id
         */
        @JavascriptInterface
        public void addToCart(long id) {
            addToFavorite();
        }
    }

    /**
     * 添加到收藏夹
     */
    private void addToFavorite() {
        User user = BaseApplication.application.getUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        String userId = BaseApplication.application.getUser().getId() + "";

        if (!TextUtils.isEmpty(userId)) {

            OkGo.<BaseResMsg>post(API.FAVORITE_CREATE)
                    .tag(this)
                    .params("user_id",Long.parseLong(userId))
                    .params("ware_id",mWares.getId())
                    .params("token",BaseApplication.application.getToken())
                    .execute(new JsonCallBack<BaseResMsg>() {
                        @Override
                        public void SuccessData(Response<BaseResMsg> response) {
                            CollectionProvider.getInstance(WaresDetailsActivity.this).put(mWares);
                            ToastUtils.showToastShort(getString(R.string.has_add_favorite));
                        }
                    });

        }else {
            ToastUtils.showToastShort("加载错误...");
        }
    }
}
