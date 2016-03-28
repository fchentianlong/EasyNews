package codeword.com.easynews.module.photo.view;

import java.util.List;

import codeword.com.easynews.base.BaseView;
import codeword.com.easynews.bean.SinaPhotoList;
import codeword.com.easynews.common.DataLoadType;

/**
 * Created by Administrator on 2016/3/12.
 */
public interface IPhotoListView extends BaseView{
    void updatePhotoList(List<SinaPhotoList.DataEntity.PhotoListEntity> data,DataLoadType type);
}
