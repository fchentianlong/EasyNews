package codeword.com.easynews.module.photo.presenter;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.module.photo.view.IPhotoView;

/**
 * Created by Administrator on 2016/3/12.
 */
public class IPhotoPresenterImpl extends BasePresenterImpl<IPhotoView,Void> implements IPhotoPresenter {
    public IPhotoPresenterImpl(IPhotoView view) {
        super(view);
        mView.initViewPager();
    }
}
