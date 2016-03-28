package codeword.com.easynews.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.norbsoft.typefacehelper.ActionBarHelper;
import com.norbsoft.typefacehelper.TypefaceCollection;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.socks.library.KLog;
import com.zhy.changeskin.SkinManager;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import codeword.com.easynews.BuildConfig;
import codeword.com.easynews.R;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.app.AppManager;
import codeword.com.easynews.module.news.ui.NewsActivity;
import codeword.com.easynews.module.photo.ui.PhotoActivity;
import codeword.com.easynews.module.setting.ui.SettingActivity;
import codeword.com.easynews.module.video.ui.VideoActivity;
import codeword.com.easynews.utils.GlideCircleTransform;
import codeword.com.easynews.utils.RxBus;
import codeword.com.easynews.utils.SlidrUtil;
import codeword.com.easynews.utils.SpUtil;
import codeword.com.easynews.utils.ViewUtil;
import codeword.com.easynews.utils.slidr.model.SlidrInterface;
import rx.Observable;
import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

/**
 * Created by Administrator on 2016/3/4.
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements View.OnClickListener,BaseView {
    /**
     * 将代理类通用行为抽出来
     */
    protected T mPresenter;
    /**
     * 标示该activity是否可滑动退出,默认false
     */
    protected boolean mEnableSlidr;
    /**
     * 布局的id
     */
    protected int mContentViewId;

    /**
     * 是否存在NavigationView
     */
    protected boolean mHasNavigationView;

    /**
     *跳转activity的class
     */
    private Class mClass;

    /**
     * 菜单的id
     */
    private int mMenuId;

    /**
     * Toolbar标题
     */
    private int mToolbarTitle;

    /**
     * 默认选中的菜单项
     */
    private int mMenuDefaultCheckedItem;

    /**
     * Toolbar左侧按钮的样式
     */
    private int mToolbarIndicator;
    /**
     * 控制滑动与否的接口
     */
    protected SlidrInterface mSlidrInterface;
    /**
     * 结束Activity的可观测对象
     */
    private Observable<Boolean> mFinishObservable;

    public static final long TWO_SECOND = 2 * 1000;
    long preTime;


    protected Toolbar toolbar;
    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)){
            ActivityFragmentInject annotation=getClass().getAnnotation(ActivityFragmentInject.class);
            mContentViewId=annotation.contentViewId();
            mEnableSlidr=annotation.enableSlidr();
            mHasNavigationView=annotation.hasNavigationView();
            mMenuId=annotation.menuId();
            mToolbarTitle=annotation.toolbarTitle();
            mToolbarIndicator=annotation.toolbarIndicator();
            mMenuDefaultCheckedItem=annotation.menuDefaultCheckedItem();

        }else {
            throw  new RuntimeException("Class must add annotations of ActivityFragmentInitParams.class");
        }

        if (BuildConfig.DEBUG){
//          StrictMode(android.os.StrictMode)。这个类可以用来帮助开发者改进他们编写的应用，
//          并且提供了各种的策略，这些策略能随时检查和报告开发者开发应用中存在的问题，
//          比如可以监视那些本不应该在主线程中完成的工作或者其他的一些不规范和不好的代码。
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

        if (this instanceof SettingActivity){
            SkinManager.getInstance().register(this);
        }

        if (!mEnableSlidr&&mHasNavigationView){
            setTheme(SpUtil.readBoolean("enableNightMode")? R.style.BaseAppThemeNight_AppTheme:R.style.BaseAppTheme_AppTheme);
        }else {
            setTheme(SpUtil.readBoolean("enableNightMode")?R.style.BaseAppThemeNight_SlidrTheme:R.style.BaseAppTheme_SlidrTheme);
        }

        //设置rootView
        setContentView(mContentViewId);

        //初始化 butterknife
        ButterKnife.bind(this);

        if (mEnableSlidr&&!SpUtil.readBoolean("disableSlide")){
            // 默认开启侧滑，默认是整个页码侧滑
            mSlidrInterface= SlidrUtil.initSlidrDefaultConfig(this, SpUtil.readBoolean("enableSlideEdge"));
        }

        //初始化toolbar
        initToolbar();

        if (mHasNavigationView){
            initNavigationView();
            //只在导航页添加观察者
            initFinishRxBus();
        }

        initView();

        // Apply custom typefaces!
        TypefaceHelper.typeface(this);


    }


    /**
     * toolbar
     */
    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (mToolbarTitle!=-1){
                setToolbarTitle(mToolbarTitle);
            }
            if (mToolbarIndicator!=-1){
                setToolbarIndicator(mToolbarIndicator);
            }else {
                setToolbarIndicator(R.drawable.ic_menu_back);
            }
        }

    }

    protected void setToolbarTitle(int resId){
        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle(resId);
    }
    protected void setToolbarTitle(String str){
        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle(str);
    }

    protected void setToolbarIndicator(int resId){
        if (getSupportActionBar()!=null){
            getSupportActionBar().setHomeAsUpIndicator(resId);
        }
    }

    /**
     * 初始化具体的view，由子类实现
     */
    protected abstract void initView();

    /**
     *
     */
    private void initNavigationView(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        // 为4.4透明状态栏布局延伸到状态栏做适配
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            mDrawerLayout.setFitsSystemWindows(false);
            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(
                    R.id.coordinator_layout);
            if (coordinatorLayout != null) {
                // CoordinatorLayout设为true才能把布局延伸到状态栏
                coordinatorLayout.setFitsSystemWindows(true);
            }
        }

        if (mMenuDefaultCheckedItem!=-1){
            mNavigationView.setCheckedItem(mMenuDefaultCheckedItem);
        }


        /**
         * java8新写法
         */
        mNavigationView.setNavigationItemSelectedListener(item -> {
            if (item.isChecked()) return true;
            switch (item.getItemId()) {
                case R.id.action_news:
                    mClass = NewsActivity.class;
                    break;
                case R.id.action_video:
                    mClass = VideoActivity.class;
                    break;
                case R.id.action_photo:
                    mClass = PhotoActivity.class;
                    break;
                case R.id.action_settings:
                    mClass = SettingActivity.class;
                    break;
            }
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return false;
        });

        /**
         * 设置头像
         */
        mNavigationView.post(new Runnable() {
            @Override
            public void run() {
                final ImageView imageView = (ImageView) findViewById(R.id.avatar);
                Glide.with(mNavigationView.getContext()).load(R.drawable.ic_header).crossFade().transform(new GlideCircleTransform(mNavigationView.getContext())).into(imageView);
                final TextView email= (TextView) findViewById(R.id.email);
                typeface(email);
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mClass != null) {
                    showActivityReorderToFront(BaseActivity.this, mClass, false);
                }

                mClass = null;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    /**
     * 订阅结束自己的事件，这里使用来结束导航的Activity(处理换肤问题)
     * 观察者模式
     */
    private void initFinishRxBus(){
        mFinishObservable= RxBus.get().register("finish",Boolean.class);
        mFinishObservable.subscribe(themeChange -> {
            try {
                if (themeChange && !AppManager.getInstance().getCurrentNavActivity().getName().equals(this.getClass().getName())) {
                    //  切换皮肤的做法是设置页面通过鸿洋大大的不重启换肤，其他后台导航页面的统统干掉，跳转回去的时候，
                    //  因为用了FLAG_ACTIVITY_REORDER_TO_FRONT，发现栈中无之前的activity存在了，就重启设置了主题，
                    // 这样一来就不会所有都做无重启去刷新控件造成的卡顿现象
                    finish();
                } else if (!themeChange) {
                    finish();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                KLog.e("找不到此类");
            }
        });
    }

    private void showActivityReorderToFront(Activity act,Class<?> cls,boolean backPress){
        AppManager.getInstance().orderNavActivity(cls.getName(),backPress);
        Intent intent=new Intent();
        intent.setClass(act,cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        act.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void showActivity(Activity aty, Intent it) {
        aty.startActivity(it);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(mMenuId, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerLayout != null && item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        } else if (item.getItemId() == android.R.id.home && mToolbarIndicator == -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            if (mDrawerLayout!=null&&mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                // 返回键时未关闭侧栏时关闭侧栏
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }else if (!(this instanceof NewsActivity)&&mHasNavigationView){
                try{
                    showActivityReorderToFront(this,AppManager.getInstance().getLastNavActivity(),true);
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                    KLog.e("找不到类名啊");
                }

            }else if (this instanceof NewsActivity){
                long currentTime = new Date().getTime();

                // 如果时间间隔大于2秒, 不处理
                if ((currentTime - preTime) > TWO_SECOND) {
                    // 显示消息
                    showSnackbar("再按一次退出应用程序");

                    // 更新时间
                    preTime = currentTime;

                    // 截获事件,不再处理
                    return true;
                } else {
                    RxBus.get().post("finish",false);
                    return true;
                }

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter!=null){
            mPresenter.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.onDestroy();
        }

        if(mFinishObservable!=null){
            RxBus.get().unregister("finish", mFinishObservable);
        }

        if (this instanceof SettingActivity){
            SkinManager.getInstance().unregister(this);
        }


        ViewUtil.fixInputMethodManagerLeak(this);
        ButterKnife.unbind(this);
    }


    protected ActionBar getToolbar(){
        return getSupportActionBar();
    }

    protected View getDecorView(){
        return getWindow().getDecorView();
    }

    protected void showSnackbar(String msg){
        Snackbar.make(getDecorView(),msg,Snackbar.LENGTH_SHORT).show();
    }

    protected void showSnackbar(int resId){
        Snackbar.make(getDecorView(),resId,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void toast(String msg) {
        showSnackbar(msg);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onClick(View v) {

    }

}
