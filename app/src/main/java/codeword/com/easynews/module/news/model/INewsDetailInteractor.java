package codeword.com.easynews.module.news.model;

import codeword.com.easynews.callback.RequestCallBack;
import rx.Subscription;

/**
 * Created by Administrator on 2016/3/25.
 */
public interface INewsDetailInteractor<T> {
    Subscription requestNewsDetail(RequestCallBack<T> callBack,String id);
}
