package codeword.com.easynews.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.app.App;

/**
 * Created by Administrator on 2016/3/8.
 */
public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements BaseView,View.OnClickListener{
    protected T mPresenter;
    protected View fragmentRootView;
    protected int mContentViewId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentRootView==null){
            if (getClass().isAnnotationPresent(ActivityFragmentInject.class)){
                ActivityFragmentInject annotation=getClass().getAnnotation(ActivityFragmentInject.class);
                mContentViewId=annotation.contentViewId();
            }else {
                throw new RuntimeException(
                        "Class must add annotations of ActivityFragmentInitParams.class");
            }

            fragmentRootView=inflater.inflate(mContentViewId,container,false);
            initView(fragmentRootView);
        }

        ButterKnife.bind(this,fragmentRootView);

        return fragmentRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter!=null){
            mPresenter.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 使用 RefWatcher 监控 Fragment
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent=(ViewGroup)fragmentRootView.getParent();
        if (parent!=null){
            parent.removeView(fragmentRootView);
        }

        ButterKnife.unbind(this);

    }

    protected abstract void initView(View fragmentRootView);



    /**
     * 当通过changeFragment()显示时会被调用(类似于onResume)
     */
    public void onChange() {
    }

    protected void showSnackbar(String msg) {
        Snackbar.make(fragmentRootView, msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void showSnackbar(int id) {
        Snackbar.make(fragmentRootView, id, Snackbar.LENGTH_SHORT).show();
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
