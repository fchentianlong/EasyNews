package codeword.com.easynews.module.photo.presenter;

import codeword.com.easynews.base.BasePresenter;

/**
 * Created by Administrator on 2016/3/12.
 */
public interface IPhotoListPresenter extends BasePresenter{
    void refreshData();
    void loadMoreData();
}
