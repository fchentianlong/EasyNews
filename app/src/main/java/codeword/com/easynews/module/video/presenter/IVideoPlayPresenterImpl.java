package codeword.com.easynews.module.video.presenter;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.module.video.view.IVideoPlayView;

/**
 * Created by Administrator on 2016/3/11.
 */
public class IVideoPlayPresenterImpl extends BasePresenterImpl<IVideoPlayView,Void> implements IVideoPlayPresenter{
    public IVideoPlayPresenterImpl(IVideoPlayView view,String path) {
        super(view);
        mView.playVideo(path);
        mView.showProgress();
    }
}
