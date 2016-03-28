package codeword.com.easynews.module.news.presenter;

import java.util.List;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.bean.NeteastNewsSummary;
import codeword.com.easynews.common.DataLoadType;
import codeword.com.easynews.module.news.model.INewsListInteractor;
import codeword.com.easynews.module.news.model.INewsListInteractorImpl;
import codeword.com.easynews.module.news.view.INewsListView;

/**
 * Created by Administrator on 2016/3/21.
 */
public class INewsListPresenterImpl extends BasePresenterImpl<INewsListView,List<NeteastNewsSummary>> implements INewsListPresenter{
    private String mNewsType;
    private String mNewsId;
    private int mStartPage;

    private boolean mIsRefresh=true;
    private boolean mIsInit;

    private INewsListInteractor<List<NeteastNewsSummary>> mNewsListInteractor;
    public INewsListPresenterImpl(INewsListView view,String newsId,String newsType) {
        super(view);
        mNewsType=newsType;
        mNewsId=newsId;
        mNewsListInteractor=new INewsListInteractorImpl();
        mSubscription=mNewsListInteractor.requestNewsList(this,mNewsType,mNewsId,mStartPage);

    }

    @Override
    public void refreshData() {
        mStartPage=0;
        mIsRefresh=true;
        mSubscription=mNewsListInteractor.requestNewsList(this,mNewsType,mNewsId,mStartPage);
    }

    @Override
    public void loadMoreData() {
        mIsRefresh=false;
        mSubscription=mNewsListInteractor.requestNewsList(this,mNewsType,mNewsId,mStartPage);
    }

    @Override
    public void beforeRequest() {
       if (!mIsInit){
           mView.showProgress();
       }
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
        mView.updateNewsList(null,mIsRefresh? DataLoadType.TYPE_REFRESH_FAIL:DataLoadType.TYPE_LOAD_MORE_FAIL);
    }

    @Override
    public void requestSuccess(List<NeteastNewsSummary> data) {
        mIsInit=true;
        if (data!=null){
           mStartPage+=20;
        }
        mView.updateNewsList(data,mIsRefresh?DataLoadType.TYPE_REFRESH_SUCCESS:DataLoadType.TYPE_LOAD_MORE_SUCCESS);
    }
}
