package codeword.com.easynews.module.video.model;

import codeword.com.easynews.callback.RequestCallBack;
import rx.Subscription;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface IVideoListInteractor<T> {
    Subscription requestVideoList(String videoId,int position,RequestCallBack<T> callback);
}
