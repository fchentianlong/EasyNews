package codeword.com.easynews.module.video.model;

import codeword.com.easynews.bean.SinaPhotoDetail;
import codeword.com.easynews.callback.RequestCallBack;
import codeword.com.easynews.http.HostType;
import codeword.com.easynews.http.manager.RetrofitManager;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by Administrator on 2016/3/12.
 */
public class IPhotoDetailInteractorImpl implements IPhotoDetailInteractor{
    @Override
    public Subscription requestPhotoDetail(RequestCallBack callBack, String id) {
        return RetrofitManager.builder(HostType.SINANEWS_PHOTO).getSinaPhotoDetailObservable(id)
                .doOnSubscribe(new Action0() {
                      @Override
                      public void call() {
                          callBack.beforeRequest();
                      }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SinaPhotoDetail>() {
                    @Override
                    public void onCompleted() {
                        callBack.requestComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.requestError(e.getLocalizedMessage().toString());
                    }

                    @Override
                    public void onNext(SinaPhotoDetail sinaPhotoDetail) {
                        callBack.requestSuccess(sinaPhotoDetail);
                    }
                });
    }
}
