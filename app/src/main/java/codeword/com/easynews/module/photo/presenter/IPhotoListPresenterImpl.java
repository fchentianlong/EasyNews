package codeword.com.easynews.module.photo.presenter;

import java.util.List;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.bean.SinaPhotoList;
import codeword.com.easynews.common.DataLoadType;
import codeword.com.easynews.module.photo.model.IPhotoListInteractor;
import codeword.com.easynews.module.photo.model.IPhotoListInteractorImpl;
import codeword.com.easynews.module.photo.view.IPhotoListView;

/**
 * Created by Administrator on 2016/3/12.
 */
public class IPhotoListPresenterImpl extends BasePresenterImpl<IPhotoListView,List<SinaPhotoList.DataEntity.PhotoListEntity>> implements IPhotoListPresenter{
    private String mPhotoId;
    private int mStartPage;
    private boolean mIsRefresh=true;
    private boolean mHasInit;

    private IPhotoListInteractor<List<SinaPhotoList.DataEntity.PhotoListEntity>> mPhotoListInteractor;

    public IPhotoListPresenterImpl(IPhotoListView view,String photoId,int startPage) {
        super(view);
        mPhotoId=photoId;
        mStartPage=startPage;
        mPhotoListInteractor=new IPhotoListInteractorImpl();
        mSubscription=mPhotoListInteractor.requestPhotoList(this,mPhotoId,mStartPage);
    }

    @Override
    public void refreshData() {
        mStartPage=1;
        mIsRefresh=true;
        mSubscription=mPhotoListInteractor.requestPhotoList(this,mPhotoId,mStartPage);
    }

    @Override
    public void loadMoreData() {
        mIsRefresh=false;
        mSubscription=mPhotoListInteractor.requestPhotoList(this,mPhotoId,mStartPage);
    }

    @Override
    public void beforeRequest() {
        if (!mHasInit){
            mView.showProgress();
        }
    }

    @Override
    public void requestSuccess(List<SinaPhotoList.DataEntity.PhotoListEntity> data) {
         mHasInit=true;
        if (data!=null&&data.size()>0){
            mStartPage+=10;
        }

        mView.updatePhotoList(data,mIsRefresh? DataLoadType.TYPE_REFRESH_SUCCESS:DataLoadType.TYPE_LOAD_MORE_SUCCESS);
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
        mView.updatePhotoList(null,mIsRefresh?DataLoadType.TYPE_REFRESH_FAIL:DataLoadType.TYPE_LOAD_MORE_FAIL);
    }

    @Override
    public void requestComplete() {
        super.requestComplete();
    }
}
