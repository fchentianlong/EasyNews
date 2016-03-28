package codeword.com.easynews.module.news.presenter;

import codeword.com.easynews.base.BasePresenter;

/**
 * Created by Administrator on 2016/3/17.
 */
public interface INewsListPresenter extends BasePresenter{
    void refreshData();
    void loadMoreData();
}
