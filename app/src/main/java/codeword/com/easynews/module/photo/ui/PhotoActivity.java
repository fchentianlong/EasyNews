package codeword.com.easynews.module.photo.ui;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

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
import codeword.com.easynews.module.photo.presenter.IPhotoPresenter;
import codeword.com.easynews.module.photo.presenter.IPhotoPresenterImpl;
import codeword.com.easynews.module.photo.view.IPhotoView;
import codeword.com.easynews.utils.ViewUtil;

/**
 * Created by Administrator on 2016/3/7.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_photo,
                        menuId = R.menu.menu_photo,
                        hasNavigationView = true,
                        toolbarTitle = R.string.photo,
                        toolbarIndicator = R.drawable.ic_list_white,
                        menuDefaultCheckedItem = R.id.action_photo)
public class PhotoActivity extends BaseActivity<IPhotoPresenter> implements IPhotoView {

    @Bind(R.id.tabs)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Override
    protected void initView() {
        mPresenter=new IPhotoPresenterImpl(this);
    }

    @Override
    public void initViewPager() {
        List<BaseFragment> fragments=new ArrayList<>();
        List<String> titles= Arrays.asList("精选", "趣图", "美图", "故事");
        fragments.add(PhotoListFragment.newInstance(Api.SINA_PHOTO_CHOICE_ID));
        fragments.add(PhotoListFragment.newInstance(Api.SINAD_PHOTO_FUN_ID));
        fragments.add(PhotoListFragment.newInstance(Api.SINAD_PHOTO_PRETTY_ID));
        fragments.add(PhotoListFragment.newInstance(Api.SINA_PHOTO_STORY_ID));

        BaseFragmentAdapter adapter=new BaseFragmentAdapter(getSupportFragmentManager(),fragments,titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        ViewUtil.dynamicSetTablayoutMode(tabLayout);


    }
}
