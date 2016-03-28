package codeword.com.easynews.module.setting.presenter;

import codeword.com.easynews.base.BasePresenterImpl;
import codeword.com.easynews.module.setting.view.ISettingView;

/**
 * Created by Administrator on 2016/3/7.
 */
public class ISettingPresenterImpl extends BasePresenterImpl<ISettingView,Void> implements ISettingPresenter{
    public ISettingPresenterImpl(ISettingView view) {
        super(view);
        view.initItemState();
    }
}
