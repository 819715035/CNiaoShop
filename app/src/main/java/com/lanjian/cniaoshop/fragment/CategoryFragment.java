package com.lanjian.cniaoshop.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.adapter.BaseAdapter;
import com.lanjian.cniaoshop.adapter.CategoryAdapter;
import com.lanjian.cniaoshop.adapter.CategoryWaresAdapter;
import com.lanjian.cniaoshop.adapter.DividerItemDecortion;
import com.lanjian.cniaoshop.bean.BannerData;
import com.lanjian.cniaoshop.bean.Category;
import com.lanjian.cniaoshop.bean.Page;
import com.lanjian.cniaoshop.bean.Wares;
import com.lanjian.cniaoshop.net.JsonCallBack;
import com.lanjian.cniaoshop.utils.API;
import com.lanjian.cniaoshop.utils.SPUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by Ivan on 15/9/22.
 */
public class CategoryFragment extends Fragment {

    @BindView(R.id.recyclerview_category)
    RecyclerView recyclerviewCategory;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refresh)
    MaterialRefreshLayout refresh;
    Unbinder unbinder;
    private CategoryAdapter mCategoryAdapter;
    private long category_id = 0;//左部导航id
    private int curPage = 1;
    private int totalPage = 1;
    private int totalCount = 28;
    private int pageSize = 10;
    private final int STATE_NORMAL = 0;
    private final int STATE_REFRESH = 1;
    private final int STATE_MORE = 2;
    private int state = STATE_NORMAL;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private List<Wares> mDatas;
    private CategoryWaresAdapter mWaresAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        requestCategoryData();
        requestBannerData();
        initRefreshLayout();
    }

    /**
     * wares数据刷新
     */
    private void initRefreshLayout() {
        refresh.setLoadMore(true);

        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (curPage * pageSize < totalCount)
                    loadMoreData();
                else {
                    Toast.makeText(getContext(), "没有数据了...", Toast.LENGTH_SHORT).show();
                    refresh.finishRefreshLoadMore();
                }
            }
        });
    }

    private void loadMoreData() {
        curPage = ++curPage;
        state = STATE_MORE;
        requestWares(category_id);
    }

    private void refreshData() {
        curPage = 1;
        state = STATE_REFRESH;
        requestWares(category_id);
    }

    /**
     * 请求wares数据，并传入列表id
     * @param categoryId 传入的点击的列表id显示该id对应商品
     */
    private void requestWares(long categoryId) {
        OkGo.<Page<Wares>>post(API.WARSLIST_URL)
                .tag(getActivity())
                .params("categoryId",categoryId)
                .params("curPage",curPage)
                .params("pageSize",pageSize)
                .execute(new JsonCallBack<Page<Wares>>() {
                    @Override
                    public void SuccessData(Response<Page<Wares>> result) {

                        mDatas = result.body().getList();

                        curPage = result.body().getCurrentPage();

                        totalPage = result.body().getTotalPage();

                        totalCount = result.body().getTotalCount();

                        showCategoryWaresData();
                        String gson = new Gson().toJson(result.body());
                        SPUtils.put("WARSLIST_URL",gson);
                    }

                    @Override
                    public void onError(Response<Page<Wares>> response) {
                        super.onError(response);

                        String data = SPUtils.getValue("WARSLIST_URL","");
                        if (!TextUtils.isEmpty(data)){
                            Page<Wares> wares = new Gson().fromJson(data, new TypeToken<Page<Wares>>() {
                            }.getType());
                            mDatas = wares.getList();
                            curPage = wares.getCurrentPage();
                            totalPage = wares.getTotalPage();
                            totalCount = wares.getTotalCount();
                            showCategoryWaresData();
                        }
                    }
                });
    }

    private void showCategoryWaresData() {
        switch (state) {
            case STATE_NORMAL:
                if (mWaresAdapter == null) {
                    mWaresAdapter = new CategoryWaresAdapter(getContext(), mDatas);
                    mWaresAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                        @Override
                        public void onItemClick(View view, int position) {
                            //mWaresAdapter.showDetail(mWaresAdapter.getItem(position));
                        }
                    });
                    recyclerview.setAdapter(mWaresAdapter);
                    recyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                } else {
                    mWaresAdapter.clearData();
                    mWaresAdapter.addData(mDatas);
                }
                break;
            case STATE_MORE:
                mWaresAdapter.addData(mWaresAdapter.getDatas().size(), mDatas);
                recyclerview.scrollToPosition(mWaresAdapter.getDatas().size());
                refresh.finishRefreshLoadMore();
                break;
            case STATE_REFRESH:
                mWaresAdapter.clearData();
                mWaresAdapter.addData(mDatas);
                recyclerview.setAdapter(mWaresAdapter);
                recyclerview.scrollToPosition(0);
                refresh.finishRefresh();
                break;
        }
    }

    /**
     * 请求轮播导航数据
     */
    private void requestBannerData() {
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

    /**
     * 显示轮播数据
     */
    private void setBannerData(List<BannerData> banners) {
        for (int i=0;i<banners.size();i++){
            images.add(banners.get(i).getImgUrl());
            titles.add(banners.get(i).getName());
            setBanner();
        }
    }

    private void setBanner() {
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器
        banner.setImageLoader(new CategoryFragment.GlideImageLoader());
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



    /**
     * 请求左部导航菜单数据
     */
    private void requestCategoryData() {
        OkGo.<List<Category>>post(API.CATEGORY_URL)
                .tag(getActivity())
                .execute(new JsonCallBack<List<Category>>() {
                    @Override
                    public void SuccessData(Response<List<Category>> response) {
                        showCategoryData(response.body());
                        String gson = new Gson().toJson(response.body());
                        SPUtils.put("CATEGORY_URL",gson);
                        if (response.body() != null && response.body().size() > 0)
                            category_id = response.body().get(0).getId();

                        requestWares(category_id);
                    }

                    @Override
                    public void onError(Response<List<Category>> response) {
                        super.onError(response);
                        String data = SPUtils.getValue("CATEGORY_URL","");
                        if (!TextUtils.isEmpty(data)){
                            List<Category> categories = new Gson().fromJson(data, new TypeToken<List<Category>>() {
                            }.getType());
                            showCategoryData(categories);
                            if (categories != null && categories.size() > 0)
                                category_id = categories.get(0).getId();
                            requestWares(category_id);
                        }
                    }
                });
    }


    /**
     * 左部导航
     * @param categories 导航列表
     */
    private void showCategoryData(List<Category> categories) {
        mCategoryAdapter = new CategoryAdapter(getContext(), categories);
        recyclerviewCategory.setAdapter(mCategoryAdapter);
        mCategoryAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
            @Override
            public void onItemClick(View view, int position) {

                //获取列表数据
                Category category = mCategoryAdapter.getItem(position);

                //获取列表数据id
                category_id = category.getId();

                curPage = 1;
                state = STATE_NORMAL;
                requestWares(category_id);

            }
        });
        recyclerviewCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerviewCategory.setItemAnimator(new DefaultItemAnimator());
        recyclerviewCategory.addItemDecoration(new DividerItemDecortion(getContext(), DividerItemDecortion.VERTICAL_LIST));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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



