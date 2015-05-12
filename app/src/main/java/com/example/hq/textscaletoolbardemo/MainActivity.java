package com.example.hq.textscaletoolbardemo;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener {

    //控件
    private ListView listView;
    private Toolbar toolbar;
    private TextView floatTitle;
    private View headerBg;
    //测量值
    private float headerHeight;//顶部高度
    private float minHeaderHeight;//顶部最低高度，即Bar的高度
    private float floatTitleLeftMargin;//header标题文字左偏移量
    private float floatTitleSize;//header标题文字大小
    private float floatTitleSizeLarge;//header标题文字大小（大号）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMeasure();
        initView();
        initListViewHeader();
        initListView();
        initEvent();
    }
    //初始化尺寸的参数
    private void initMeasure() {
        headerHeight = getResources().getDimension(R.dimen.header_height);//320dp
        minHeaderHeight = getResources().getDimension(R.dimen.abc_action_bar_default_height_material);//56dp
        floatTitleLeftMargin = getResources().getDimension(R.dimen.float_title_left_margin);//44dp
        floatTitleSize = getResources().getDimension(R.dimen.float_title_size);//20dp
        floatTitleSizeLarge = getResources().getDimension(R.dimen.float_title_size_large);//36sp
    }
    //为app的顶部设置toolbar，并初始化视图
    private void initView() {
        listView = (ListView) findViewById(R.id.lv_main);
        floatTitle = (TextView) findViewById(R.id.tv_main_title);
        toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);//将actionbar替换为toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //为listview填充数据(设置适配器)
    private void initListView() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            data.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.activity_list_item, android.R.id.text1, data);
        listView.setAdapter(adapter);
    }
    //为listview的头部添加一张背景图
    private void initListViewHeader() {
        View headerContainer = LayoutInflater.from(this).inflate(R.layout.header, listView, false);
        //view类的animate()的淡入淡出效果
        headerBg = headerContainer.findViewById(R.id.img_header_bg);
        //1.先可见，透明度为0即不显示
        headerBg.setAlpha(0);
        headerBg.setVisibility(View.VISIBLE);
        //2.设置时间和透明度为1即可见，setlistner里的监听器可以监听动画的开始结束，重复等动作
        headerBg.animate().setDuration(2000).alpha(1f).setListener(null);

        listView.addHeaderView(headerContainer);


    }
    //为listview添加滚动监听
    private void initEvent() {
        listView.setOnScrollListener(this);
        handler.postDelayed(run,2000);
    }
    Runnable run=new Runnable() {
        @Override
        public void run() {
            //前一张图片淡出
            headerBg.setAlpha(1f);
            headerBg.setVisibility(View.VISIBLE);
            headerBg.animate().setDuration(2000).alpha(0);
            //后一张淡入
            ImageView img=(ImageView)headerBg;
            img.setImageResource(R.mipmap.header_bg2);
            headerBg.setAlpha(0);
            headerBg.setVisibility(View.VISIBLE);

            headerBg.animate().setDuration(2000).alpha(1f).setListener(null);
           // handler.postDelayed(this,2000);
        }
    };
    private Handler handler=new Handler();
    //为菜单项设置子菜单menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //Y轴偏移量
        float scrollY = getScrollY(view);

        //变化率
        float headerBarOffsetY = headerHeight - minHeaderHeight;//Toolbar与header高度的差值
        float offset = 1 - Math.max((headerBarOffsetY - scrollY) / headerBarOffsetY, 0f);

        //Toolbar背景色透明度
        toolbar.setBackgroundColor(Color.argb((int) (offset * 255), 0, 0, 0));
        //header背景图Y轴偏移
        headerBg.setTranslationY(scrollY / 2);

        /*** 标题文字处理 ***/
        //标题文字缩放圆心（X轴）
        floatTitle.setPivotX(floatTitle.getLeft() + floatTitle.getPaddingLeft());
        //标题文字缩放比例
        float titleScale = floatTitleSize / floatTitleSizeLarge;
        //标题文字X轴偏移
        floatTitle.setTranslationX(floatTitleLeftMargin * offset);
        //标题文字Y轴偏移：（-缩放高度差 + 大文字与小文字高度差）/ 2 * 变化率 + Y轴滑动偏移
        floatTitle.setTranslationY(
                (-(floatTitle.getHeight() - minHeaderHeight) +//-缩放高度差
                        floatTitle.getHeight() * (1 - titleScale))//大文字与小文字高度差
                        / 2 * offset +
                        (headerHeight - floatTitle.getHeight()) * (1 - offset));//Y轴滑动偏移
        //标题文字X轴缩放
        floatTitle.setScaleX(1 - offset * (1 - titleScale));
        //标题文字Y轴缩放
        floatTitle.setScaleY(1 - offset * (1 - titleScale));
        //标题文字的颜色变化,透明度从255到0，颜色为绿色
        floatTitle.setTextColor(Color.argb((int) (255-offset * 255), 102, 153, 0));

        //判断标题文字的显示
        if (scrollY > headerBarOffsetY) {
            toolbar.setTitle(getResources().getString(R.string.toolbar_title));//显示标题
            toolbar.setTitleTextColor(Color.parseColor("#ff669900"));//设置标题颜色
            floatTitle.setVisibility(View.GONE);
        } else {
            toolbar.setTitle("");
            floatTitle.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 得到ListView在Y轴上的偏移
     */
    public float getScrollY(AbsListView view) {
        //获取“0”位置的视图即listview的头部视图img
        View c = view.getChildAt(0);

        if (c == null)
            return 0;//没有获取到该视图则返回0
        int top = c.getTop();//获取img它的实际顶部长度(px)

        //获取适配器里数据源的第一个数据的位置
        int firstVisiblePosition = view.getFirstVisiblePosition();


        float headerHeight = 0;
        //当第一个数据的顶部还有img视图显示时
        if (firstVisiblePosition >= 1)
            headerHeight = this.headerHeight;//赋予预设值高度

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }
}
