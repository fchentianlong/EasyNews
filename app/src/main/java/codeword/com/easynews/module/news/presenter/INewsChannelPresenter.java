package codeword.com.easynews.module.news.presenter;

import codeword.com.easynews.base.BasePresenter;

/**
 * Created by Administrator on 2016/3/21.
 */
public interface INewsChannelPresenter extends BasePresenter{
    void onItemAddOrRemove(String channelName,boolean selectState);

    void  onItemSwap(int fromPos,int toPos);
}
