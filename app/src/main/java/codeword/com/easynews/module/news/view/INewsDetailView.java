package codeword.com.easynews.module.news.view;

import codeword.com.easynews.base.BaseView;
import codeword.com.easynews.bean.NeteastNewsDetail;

/**
 * Created by Administrator on 2016/3/25.
 */
public interface INewsDetailView extends BaseView{
    void initDetailNews(NeteastNewsDetail data);
}
