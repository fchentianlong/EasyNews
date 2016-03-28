package codeword.com.easynews.module.photo.model;

import java.util.List;

import codeword.com.easynews.bean.SinaPhotoList;
import codeword.com.easynews.callback.RequestCallBack;
import codeword.com.easynews.http.HostType;
import codeword.com.easynews.http.manager.RetrofitManager;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Administrator on 2016/3/12.
 */
public class IPhotoListInteractorImpl implements IPhotoListInteractor<List<SinaPhotoList.DataEntity.PhotoListEntity>>{
    @Override
    public Subscription requestPhotoList(RequestCallBack<List<SinaPhotoList.DataEntity.PhotoListEntity>> callback, String id, int startPage) {
        return RetrofitManager.builder(HostType.SINANEWS_PHOTO).getSinaPhotoListObservable(id,startPage)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        callback.beforeRequest();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread()).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.requestError(throwable.getLocalizedMessage().toString());
                    }
                })
                .flatMap(new Func1<SinaPhotoList, Observable<? extends SinaPhotoList.DataEntity.PhotoListEntity>>() {
                    @Override
                    public Observable<? extends SinaPhotoList.DataEntity.PhotoListEntity> call(SinaPhotoList sinaPhotoList) {
                        return Observable.from(sinaPhotoList.data.list);
                    }
                })
                .toSortedList(new Func2<SinaPhotoList.DataEntity.PhotoListEntity, SinaPhotoList.DataEntity.PhotoListEntity, Integer>() {
                    @Override
                    public Integer call(SinaPhotoList.DataEntity.PhotoListEntity photoListEntity, SinaPhotoList.DataEntity.PhotoListEntity photoListEntity2) {
                        return photoListEntity.pubDate > photoListEntity2.pubDate ? 1 : photoListEntity.pubDate == photoListEntity2.pubDate ? 0 : -1;
                    }
                })
                .subscribe(new Subscriber<List<SinaPhotoList.DataEntity.PhotoListEntity>>() {
                    @Override
                    public void onCompleted() {
                        callback.requestComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.requestError(e.getLocalizedMessage().toString());
                    }

                    @Override
                    public void onNext(List<SinaPhotoList.DataEntity.PhotoListEntity> photoListEntities) {
                        callback.requestSuccess(photoListEntities);
                    }
                });
    }
}
