package codeword.com.easynews.module.news.presenter;

import java.util.List;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.greendao.NewsChannelTable;
import codeword.com.easynews.module.news.model.INewsInteractor;
import codeword.com.easynews.module.news.model.INewsInteractorImpl;
import codeword.com.easynews.module.news.view.INewsView;

/**
 * Created by Administrator on 2016/3/17.
 */
public class INewsPresenterImpl extends BasePresenterImpl<INewsView,List<NewsChannelTable>> implements INewsPresenter{
    private INewsInteractor<List<NewsChannelTable>> mNewsInteractor;
    public INewsPresenterImpl(INewsView view) {
        super(view);
        mNewsInteractor=new INewsInteractorImpl();
        mSubscription=mNewsInteractor.operateChannelDb(this);
        mView.initRxBusEvent();
    }

    @Override
    public void requestSuccess(List<NewsChannelTable> data) {
        mView.initViewPager(data);
    }

    @Override
    public void operateChannelDb() {
        mSubscription=mNewsInteractor.operateChannelDb(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
