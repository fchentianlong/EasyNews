package codeword.com.easynews.module.video.view;

import java.util.List;

import codeword.com.easynews.base.BaseView;
import codeword.com.easynews.bean.NeteastVideoSummary;
import codeword.com.easynews.common.DataLoadType;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface IVideoListView extends BaseView{
    void updateVideoList(List<NeteastVideoSummary> data,DataLoadType type);
}
