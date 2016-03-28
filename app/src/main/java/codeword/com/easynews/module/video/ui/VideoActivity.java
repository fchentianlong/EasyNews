package codeword.com.easynews.module.video.ui;

import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import codeword.com.easynews.R;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.base.BaseActivity;
import codeword.com.easynews.base.BaseFragment;
import codeword.com.easynews.base.BaseFragmentAdapter;
import codeword.com.easynews.http.Api;
import codeword.com.easynews.module.video.presenter.IVideoPresenter;
import codeword.com.easynews.module.video.presenter.IVideoPresenterImpl;
import codeword.com.easynews.module.video.view.IVideoView;
import codeword.com.easynews.utils.ViewUtil;

/**
 * Created by Administrator on 2016/3/7.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_video,
        menuId = R.menu.menu_video,
        hasNavigationView = true,
        toolbarTitle = R.string.video,
        toolbarIndicator = R.drawable.ic_list_white,
        menuDefaultCheckedItem = R.id.action_video)
public class VideoActivity extends BaseActivity<IVideoPresenter> implements IVideoView {

    @Bind(R.id.tabs)
    TabLayout tablayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void initView() {
        mPresenter=new IVideoPresenterImpl(this);
    }

    @Override
    public void initViewPager() {
        List<BaseFragment> fragments=new ArrayList<>();
        List<String> titles= Arrays.asList("热点", "娱乐", "搞笑", "精品");
        fragments.add(VideoListFragment.newInstance(Api.VIDEO_HOT_ID,0));
        fragments.add(VideoListFragment.newInstance(Api.VIDEO_ENTERTAINMENT_ID,1));
        fragments.add(VideoListFragment.newInstance(Api.VIDEO_FUN_ID,2));
        fragments.add(VideoListFragment.newInstance(Api.VIDEO_CHOICE_ID,3));

        BaseFragmentAdapter adapter=new BaseFragmentAdapter(getSupportFragmentManager(),fragments,titles);
        viewPager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewPager);
        ViewUtil.dynamicSetTablayoutMode(tablayout);

    }
}
