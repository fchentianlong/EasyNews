package codeword.com.easynews.module.photo.presenter;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.bean.SinaPhotoDetail;
import codeword.com.easynews.module.photo.view.IPhotoDetailView;
import codeword.com.easynews.module.video.model.IPhotoDetailInteractor;
import codeword.com.easynews.module.video.model.IPhotoDetailInteractorImpl;

/**
 * Created by Administrator on 2016/3/12.
 */
public class IPhotoDetailPresenterImpl extends BasePresenterImpl<IPhotoDetailView,SinaPhotoDetail> implements IPhotoDetailPresenter{

    private boolean mIsInit;
    private IPhotoDetailInteractor<SinaPhotoDetail> mDetailInteractor;

    public IPhotoDetailPresenterImpl(IPhotoDetailView view,String id,SinaPhotoDetail data) {
        super(view);
        mDetailInteractor=new IPhotoDetailInteractorImpl();
        if (data!=null){
            mView.initViewPager(data);
        }else {
            mSubscription=mDetailInteractor.requestPhotoDetail(this,id);
        }

    }

    @Override
    public void beforeRequest() {
        if (!mIsInit){
            mView.showProgress();
        }
    }

    @Override
    public void requestSuccess(SinaPhotoDetail data) {
        mIsInit=true;
        mView.initViewPager(data);
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
    }

    @Override
    public void requestComplete() {
        super.requestComplete();
    }
}
