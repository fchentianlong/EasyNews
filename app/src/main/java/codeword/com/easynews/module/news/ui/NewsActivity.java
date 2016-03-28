package codeword.com.easynews.module.news.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.norbsoft.typefacehelper.TypefaceHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import codeword.com.easynews.R;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.app.AppManager;
import codeword.com.easynews.base.BaseActivity;
import codeword.com.easynews.base.BaseFragment;
import codeword.com.easynews.base.BaseFragmentAdapter;
import codeword.com.easynews.base.BasePresenter;
import codeword.com.easynews.bean.NeteastNewsSummary;
import codeword.com.easynews.greendao.NewsChannelTable;
import codeword.com.easynews.module.news.presenter.INewsPresenter;
import codeword.com.easynews.module.news.presenter.INewsPresenterImpl;
import codeword.com.easynews.module.news.view.INewsView;
import codeword.com.easynews.utils.NetUtil;
import codeword.com.easynews.utils.RxBus;
import codeword.com.easynews.utils.ViewUtil;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/3/4.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_news,
        menuId = R.menu.menu_news,
        hasNavigationView = true,
        toolbarTitle = R.string.news,
        toolbarIndicator = R.drawable.ic_list_white,
        menuDefaultCheckedItem = R.id.action_news)
public class NewsActivity extends BaseActivity<INewsPresenter> implements INewsView{

    @Bind(R.id.tabs)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    private Observable<Boolean> mChannelObservable;
    @Override
    protected void initView() {
        AppManager.getInstance().orderNavActivity(getClass().getName(),false);
        mPresenter=new INewsPresenterImpl(this);
    }

    @Override
    public void initViewPager(List<NewsChannelTable> newsChannels) {
        List<BaseFragment> fragments=new ArrayList<>();
        List<String> titles=new ArrayList<>();
        if (newsChannels!=null){
            for (NewsChannelTable news:newsChannels){
                final NewsListFragment fragment=NewsListFragment.newInstance(news.getNewsChannelId(),news.getNewsChannelType());
                fragments.add(fragment);
                titles.add(news.getNewsChannelName());
            }

            if (viewPager.getAdapter()==null){
                BaseFragmentAdapter adapter=new BaseFragmentAdapter(getSupportFragmentManager(),fragments,titles);
                viewPager.setAdapter(adapter);

                // 22.2.0 TabLayout的一个Bug：对Tab做删减，二次调用setupWithViewPager报 Tab belongs to a different TabLayout，还有持有的sTabPool内存泄露
                // 解决方法：https://code.google.com/p/android/issues/detail?id=201827
                // sTabPool的size只有16，现在直接创建16个无用的tab存到sTabPool，因为sTabPool在TabLayout.removeAllTabs()调用release方法貌似是清空不了它持有的数据，
                // 于是后面在tabLayout.setupWithViewPager时不会再被它重用导致 Tab belongs to a different TabLayout问题了
                TabLayout.Tab uselessTab;
                for (int j = 0; j < 16; j++) {
                    uselessTab = tabLayout.newTab();
                }
            }else {
                final BaseFragmentAdapter adapter= (BaseFragmentAdapter) viewPager.getAdapter();
                adapter.updateFragments(fragments,titles);
            }

            viewPager.setCurrentItem(0);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setScrollPosition(0, 0, true);
            // 根据Tab的长度动态设置TabLayout的模式
            ViewUtil.dynamicSetTablayoutMode(tabLayout);
        }else {
            toast("数据异常");
        }
    }

    @Override
    public void initRxBusEvent() {
        mChannelObservable=RxBus.get().register("channelChange",Boolean.class);
        mChannelObservable.subscribe((Action1<Boolean>) change->{
            if (change){
                mPresenter.operateChannelDb();
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister("channelChange",mChannelObservable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_channel_manage){
            showActivity(this,new Intent(this,NewsChannelActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
