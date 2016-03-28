package codeword.com.easynews.module.news.presenter;

import codeword.com.easynews.base.BasePresenter;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface INewsPresenter extends BasePresenter{
    /**
     * 频道排序或增删变化后调用此方法更新数据库
     */
    void operateChannelDb();
}
