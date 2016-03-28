package codeword.com.easynews.module.video.model;

import codeword.com.easynews.callback.RequestCallBack;
import rx.Subscription;

/**
 * Created by Administrator on 2016/3/12.
 */
public interface IPhotoDetailInteractor<T>{
    Subscription requestPhotoDetail(RequestCallBack<T> callBack,String id);
}
