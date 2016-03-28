package codeword.com.easynews.module.news.view;

import java.util.List;

import codeword.com.easynews.base.BaseView;
import codeword.com.easynews.bean.NeteastNewsSummary;
import codeword.com.easynews.common.DataLoadType;

/**
 * Created by Administrator on 2016/3/17.
 */
public interface INewsListView extends BaseView{
    void updateNewsList(List<NeteastNewsSummary> data,DataLoadType type);
}
