package codeword.com.easynews.module.video.presenter;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.module.video.view.IVideoView;

/**
 * Created by Administrator on 2016/3/8.
 */
public class IVideoPresenterImpl extends BasePresenterImpl<IVideoView,Void> implements IVideoPresenter{

    public IVideoPresenterImpl(IVideoView view) {
        super(view);
        view.initViewPager();
    }
}
