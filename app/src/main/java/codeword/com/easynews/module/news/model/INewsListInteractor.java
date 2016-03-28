package codeword.com.easynews.module.news.model;

import codeword.com.easynews.callback.RequestCallBack;
import rx.Subscription;

/**
 * Created by Administrator on 2016/3/21.
 */
public interface INewsListInteractor<T> {
    Subscription requestNewsList(RequestCallBack<T> callBack,String newsType,String newsId,int startId);
}
