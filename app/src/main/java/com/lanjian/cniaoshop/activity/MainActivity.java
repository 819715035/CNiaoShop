package com.lanjian.cniaoshop.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.lanjian.cniaoshop.R;
import com.lanjian.cniaoshop.comment.BaseActivity;
import com.lanjian.cniaoshop.fragment.CartFragment;
import com.lanjian.cniaoshop.fragment.CategoryFragment;
import com.lanjian.cniaoshop.fragment.HomeFragment;
import com.lanjian.cniaoshop.fragment.HotFragment;
import com.lanjian.cniaoshop.fragment.MineFragment;

public class MainActivity extends BaseActivity {

    private BottomNavigationBar bottomNavigationBar;
    private FragmentTransaction transaction;
    private HomeFragment homeFragment;
    private HotFragment hotFragment;
    private CartFragment cartFragment;
    private CategoryFragment categoryFragment;
    private MineFragment mineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initTab();
        initView();
        setListener();
        initbottomNavigationBar();
    }

    private void initView() {
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
    }

    /**
     * 选中哪个fragment
     * @param selectPosition
     */
    private void selectFragment(int selectPosition) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        //隐藏fragment
        hiddleFragment();
        switch (selectPosition){
            case 0:
                //选中首页
                if (homeFragment==null){
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.fl_main,homeFragment);
                }
                transaction.show(homeFragment);
                break;
            case 1:
                //选中热卖
                if (hotFragment==null){
                    hotFragment = new HotFragment();
                    transaction.add(R.id.fl_main,hotFragment);
                }
                transaction.show(hotFragment);
                break;
            case 2:
                //选中收藏
                if (categoryFragment==null){
                    categoryFragment = new CategoryFragment();
                    transaction.add(R.id.fl_main,categoryFragment);
                }
                transaction.show(categoryFragment);
                break;
            case 3:
                //选中购物车
                if (cartFragment==null){
                    cartFragment = new CartFragment();
                    transaction.add(R.id.fl_main,cartFragment);
                }
                transaction.show(cartFragment);
                break;
            case 4:
                //选中购物车
                if (mineFragment==null){
                    mineFragment = new MineFragment();
                    transaction.add(R.id.fl_main,mineFragment);
                }
                transaction.show(mineFragment);
                break;
        }
        transaction.commit();
    }

    /**
     * 隐藏所有的fragment
     */
    private void hiddleFragment() {
        if(homeFragment!=null){
            transaction.hide(homeFragment);
        }
        if(hotFragment!=null){
            transaction.hide(hotFragment);
        }
        if(categoryFragment!=null){
            transaction.hide(categoryFragment);
        }
        if(cartFragment!=null){
            transaction.hide(cartFragment);
        }
        if (mineFragment!=null){
            transaction.hide(mineFragment);
        }
    }
    private void setListener() {
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                selectFragment(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    private void initbottomNavigationBar() {
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.icon_home, R.string.home).setActiveColor("#FF107FFD"))
                .addItem(new BottomNavigationItem(R.drawable.selector_icon_hot, R.string.hot).setActiveColor("#ee82ee"))
                .addItem(new BottomNavigationItem(R.drawable.selector_icon_category, R.string.catagory).setActiveColor("#cd853f"))
                .addItem(new BottomNavigationItem(R.drawable.selector_icon_cart, R.string.cart).setActiveColor("#bdb76b"))
                .addItem(new BottomNavigationItem(R.drawable.selector_icon_mine, R.string.mine).setActiveColor("#c71585"))
                .setMode(BottomNavigationBar.MODE_FIXED)
                /**
                 *  setMode() 内的参数有三种模式类型：
                 *  MODE_DEFAULT 自动模式：导航栏Item的个数<=3 用 MODE_FIXED 模式，否则用 MODE_SHIFTING 模式
                 *  MODE_FIXED 固定模式：未选中的Item显示文字，无切换动画效果。
                 *  MODE_SHIFTING 切换模式：未选中的Item不显示文字，选中的显示文字，有切换动画效果。
                 */

                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
                /**
                 *  setBackgroundStyle() 内的参数有三种样式
                 *  BACKGROUND_STYLE_DEFAULT: 默认样式 如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC
                 *                                    如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                 *  BACKGROUND_STYLE_STATIC: 静态样式 点击无波纹效果
                 *  BACKGROUND_STYLE_RIPPLE: 波纹样式 点击有波纹效果
                 */

                .setActiveColor("#FF107FFD") //选中颜色
                .setInActiveColor("#e9e6e6") //未选中颜色
                .setBarBackgroundColor("#ff0000")//导航栏背景色
                .initialise();
        bottomNavigationBar.selectTab(0);


    }

   /* private void initTab() {


        Tab tab_home = new Tab(HomeFragment.class,R.string.home,R.drawable.selector_icon_home);
        Tab tab_hot = new Tab(HotFragment.class,R.string.hot,R.drawable.selector_icon_hot);
        Tab tab_category = new Tab(CategoryFragment.class,R.string.catagory,R.drawable.selector_icon_category);
        Tab tab_cart = new Tab(CartFragment.class,R.string.cart,R.drawable.selector_icon_cart);
        Tab tab_mine = new Tab(MineFragment.class,R.string.mine,R.drawable.selector_icon_mine);

        mTabs.add(tab_home);
        mTabs.add(tab_hot);
        mTabs.add(tab_category);
        mTabs.add(tab_cart);
        mTabs.add(tab_mine);



        mInflater = LayoutInflater.from(this);
        mTabhost = (FragmentTabHost) this.findViewById(android.R.id.tabhost);
        mTabhost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);

        for (Tab tab : mTabs){

            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(getString(tab.getTitle()));

            tabSpec.setIndicator(buildIndicator(tab));

            mTabhost.addTab(tabSpec,tab.getFragment(),null);

        }

        //显示分割线
        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        //选中第一个tag
        mTabhost.setCurrentTab(0);
    }


    private  View buildIndicator(Tab tab){


        View view =mInflater.inflate(R.layout.tab_indicator,null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);

        img.setBackgroundResource(tab.getIcon());
        text.setText(tab.getTitle());

        return  view;
    }*/


}


