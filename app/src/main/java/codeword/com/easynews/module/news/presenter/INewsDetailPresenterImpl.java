package codeword.com.easynews.module.news.presenter;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.bean.NeteastNewsDetail;
import codeword.com.easynews.module.news.model.INewsDetailInteractor;
import codeword.com.easynews.module.news.model.INewsDetailInteractorImpl;
import codeword.com.easynews.module.news.view.INewsDetailView;

/**
 * Created by Administrator on 2016/3/25.
 */
public class INewsDetailPresenterImpl extends BasePresenterImpl<INewsDetailView,NeteastNewsDetail> implements INewsDetailPresenter{
    private boolean isInit;
    private INewsDetailInteractor<NeteastNewsDetail> mINewsDetailInteractor;
    public INewsDetailPresenterImpl(INewsDetailView view,String postId) {
        super(view);
        mINewsDetailInteractor=new INewsDetailInteractorImpl();
        mSubscription=mINewsDetailInteractor.requestNewsDetail(this,postId);

    }

    @Override
    public void beforeRequest() {
        if (!isInit){
            mView.showProgress();
        }
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
    }

    @Override
    public void requestSuccess(NeteastNewsDetail data) {
       isInit=true;
        mView.initDetailNews(data);
    }

    @Override
    public void requestComplete() {
        super.requestComplete();
    }
}
