package codeword.com.easynews.module.photo.view;

import codeword.com.easynews.base.BaseView;
import codeword.com.easynews.bean.SinaPhotoDetail;

/**
 * Created by Administrator on 2016/3/12.
 */
public interface IPhotoDetailView extends BaseView{
    void initViewPager(SinaPhotoDetail photoList);
}
