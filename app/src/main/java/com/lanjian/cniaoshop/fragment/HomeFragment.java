package com.lanjian.cniaoshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.activity.WaresListActivity;
import com.lanjian.cniaoshop.adapter.HomeCampaignAdapter;
import com.lanjian.cniaoshop.bean.BannerData;
import com.lanjian.cniaoshop.bean.Campaign;
import com.lanjian.cniaoshop.bean.HomeCampaign;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.Constants;
import com.lanjian.cniaoshop.utils.SPUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ivan on 15/9/25.
 */
public class HomeFragment extends Fragment {

    private View view;
    private Banner banner;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private HomeCampaignAdapter mAdatper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        initRecyclerView();
        getBannerDataFormNet();
        getCampaignDataForNet();
    }

    private void getCampaignDataForNet() {
        OkGo.<List<HomeCampaign>>post(API.CAMPAIGN_URL)
                .tag(getActivity())
                .execute(new JsonCallBack<List<HomeCampaign>>() {
                    @Override
                    public void SuccessData(Response<List<HomeCampaign>> response) {
                        mAdatper.setDatas(response.body());
                        String gson = new Gson().toJson(response.body());
                        SPUtils.put("CAMPAIGN_URL",gson);
                    }

                    @Override
                    public void onError(Response<List<HomeCampaign>> response) {
                        super.onError(response);
                        String data = SPUtils.getValue("CAMPAIGN_URL","");
                        if (!TextUtils.isEmpty(data)){
                            List<HomeCampaign> homeCampaigns = new Gson().fromJson(data,new TypeToken<List<HomeCampaign>>(){}.getType());
                            mAdatper.setDatas(homeCampaigns);
                        }
                    }
                });
    }

    private void initRecyclerView() {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        mAdatper = new HomeCampaignAdapter(getActivity());
        mAdatper.setOnCampaignClickListener(new HomeCampaignAdapter.OnCampaignClickListener() {
            @Override
            public void onClick(View view, Campaign campaign) {
                Intent intent = new Intent(getActivity(), WaresListActivity.class);
                intent.putExtra(Constants.CAMPAIGN_ID,campaign.getId());
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdatper);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    private void initView() {
        banner = view.findViewById(R.id.banner);
        mRecyclerView = view.findViewById(R.id.recyclerview);
    }

    private void setBannerData(List<BannerData> banners) {
        for (int i=0;i<banners.size();i++){
            images.add(banners.get(i).getImgUrl());
            titles.add(banners.get(i).getName());
            setBanner();
        }
    }
    private void getBannerDataFormNet() {

        OkGo.<List<BannerData>>post(API.BANNER_URL)
        .tag(getActivity())
        .params("type","1")
        .execute(new JsonCallBack<List<BannerData>>() {
            @Override
            public void SuccessData(Response<List<BannerData>> response) {
                setBannerData(response.body());
                String gson = new Gson().toJson(response.body());
                SPUtils.put("BANNER_URL",gson);
            }

            @Override
            public void onError(Response<List<BannerData>> response) {
                super.onError(response);
                String data = SPUtils.getValue("BANNER_URL","");
                if (!TextUtils.isEmpty(data)){
                    List<BannerData> bannerdata = new Gson().fromJson(data,new TypeToken<List<BannerData>>(){}.getType());
                    setBannerData(bannerdata);
                }
            }
        });
    }

    private void setBanner() {
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        //开始轮播
        banner.stopAutoPlay();
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */

            //Glide 加载图片简单用法
            Glide.with(context).load(path).into(imageView);


            //用fresco加载图片简单用法，记得要写下面的createImageView方法
            Uri uri = Uri.parse((String) path);
            imageView.setImageURI(uri);
        }
    }
}
