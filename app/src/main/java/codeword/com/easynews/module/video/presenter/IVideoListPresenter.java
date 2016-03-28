package codeword.com.easynews.module.video.presenter;

import codeword.com.easynews.base.BasePresenter;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface IVideoListPresenter extends BasePresenter{
    void refreshData();
    void loadMoreData();
}
