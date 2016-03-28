package codeword.com.easynews.base;

import codeword.com.easynews.callback.RequestCallBack;
import rx.Subscription;

/**
 * Created by Administrator on 2016/3/4.
 */
public class BasePresenterImpl<T extends BaseView,V> implements BasePresenter,RequestCallBack<V> {
    protected T mView;

    protected Subscription mSubscription;

    public BasePresenterImpl(T view){
        mView=view;
    }
    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        if (mSubscription!=null&&!mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
        mView=null;
    }

    @Override
    public void beforeRequest() {
        mView.showProgress();
    }

    @Override
    public void requestError(String msg) {
        mView.toast(msg);
    }

    @Override
    public void requestComplete() {
        mView.hideProgress();
    }

    @Override
    public void requestSuccess(V data) {

    }
}
