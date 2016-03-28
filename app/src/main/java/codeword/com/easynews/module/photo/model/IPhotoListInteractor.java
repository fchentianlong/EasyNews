package codeword.com.easynews.module.photo.model;

import codeword.com.easynews.callback.RequestCallBack;
import rx.Subscription;

/**
 * Created by Administrator on 2016/3/12.
 */
public interface IPhotoListInteractor<T> {
    Subscription requestPhotoList(RequestCallBack<T> callback,String id,int startPage);
}
