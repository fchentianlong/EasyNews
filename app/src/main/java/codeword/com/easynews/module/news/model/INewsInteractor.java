package codeword.com.easynews.module.news.model;

import codeword.com.easynews.callback.RequestCallBack;
import rx.Subscription;

/**
 * Created by Administrator on 2016/3/17.
 */
public interface INewsInteractor<T> {
    Subscription operateChannelDb(RequestCallBack<T> callBack);
}
