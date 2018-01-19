package com.lanjian.cniaoshop.utils;

/**
 * @author lanjian
 * @email 819715035@qq.com
 * creat at $date$
 * description
 */
public class API {
    public static String BASE_URL = "http://112.124.22.238:8081/course_api/";
    //获取轮播图
    public static String BANNER_URL = BASE_URL+"banner/query";
    //首页产品campaign
    public static String CAMPAIGN_URL = BASE_URL+"campaign/recommend";

    //热卖链接
    public static String HOT_URL = BASE_URL+"wares/hot";

    //分类导航
    public static String CATEGORY_URL = BASE_URL+"category/list";

    //分类产品列表
    public static String WARSLIST_URL = BASE_URL+"wares/list";


}
