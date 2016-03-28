package codeword.com.easynews.module.video.presenter;

import com.socks.library.KLog;

import java.util.List;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.bean.NeteastVideoSummary;
import codeword.com.easynews.common.DataLoadType;
import codeword.com.easynews.module.video.model.IVideoListInteractor;
import codeword.com.easynews.module.video.model.IVideoListInteractorImpl;
import codeword.com.easynews.module.video.view.IVideoListView;

/**
 * Created by Administrator on 2016/3/8.
 */
public class IVideoListPresenterImpl extends BasePresenterImpl<IVideoListView,List<NeteastVideoSummary>> implements IVideoListPresenter{

    private String mVideoId;
    private int mPosition;
    private boolean mIsRefresh = true;
    private boolean mHasInit;

    private IVideoListInteractor<List<NeteastVideoSummary>> mVideoListInteractor;

    public IVideoListPresenterImpl(IVideoListView view,String id,int position) {
        super(view);
        mVideoId=id;
        mPosition=position;
        mVideoListInteractor=new IVideoListInteractorImpl();
        mSubscription=mVideoListInteractor.requestVideoList(id,position,this);
    }

    @Override
    public void requestSuccess(List<NeteastVideoSummary> data) {
        mHasInit = true;
        if (data!=null&&data.size()>0){
            mPosition+=10;
        }

        mView.updateVideoList(data,mIsRefresh?DataLoadType.TYPE_REFRESH_SUCCESS:DataLoadType.TYPE_LOAD_MORE_SUCCESS);

    }

    @Override
    public void beforeRequest() {
        if (!mHasInit){
            mView.showProgress();
        }
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
        mView.updateVideoList(null,mIsRefresh? DataLoadType.TYPE_REFRESH_FAIL:DataLoadType.TYPE_LOAD_MORE_FAIL);
    }

    @Override
    public void requestComplete() {
        super.requestComplete();
    }


    @Override
    public void refreshData() {
        KLog.e("刷新数据》》》》》》》》》》》");
        mPosition=0;
        mIsRefresh=true;
        mSubscription=mVideoListInteractor.requestVideoList(mVideoId,mPosition,this);
    }

    @Override
    public void loadMoreData() {
        KLog.e("加载更多数据: " + mVideoId + ";" + mPosition);
        mIsRefresh=false;
        mSubscription=mVideoListInteractor.requestVideoList(mVideoId,mPosition,this);
    }
}
