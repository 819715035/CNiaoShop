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

    public static final String WARES_DETAILS = BASE_URL + "wares/detail.html";
    //首页活动下的商品列表
    public static final String WARES_CAMPAIGN_LIST = BASE_URL + "wares/campaign/list";
    public static final String AUTH_LOGIN = BASE_URL + "auth/login";
    public static final String USER_DETAIL = BASE_URL + "user/get?id=1";
    public static final String AUTH_REG = BASE_URL + "auth/reg";
    public static final String ORDER_CREATE = BASE_URL + "order/create";
    public static final String ORDER_COMPLEPE=BASE_URL +"order/complete";
    public static final String ORDER_LIST = BASE_URL + "order/list";
    public static final String ADDR_CREATE = BASE_URL + "addr/create";
    public static final String ADDR_LIST = BASE_URL + "addr/list";
    public static final String ADDR_UPDATE = BASE_URL + "addr/update";
    public static final String ADDR_DEL = BASE_URL + "addr/del";
    public static final String FAVORITE_CREATE = BASE_URL + "favorite/create";
    public static final String FAVORITE_LIST = BASE_URL + "favorite/list";
    public static final String FAVORITE_DEL = BASE_URL + "favorite/del";
}
