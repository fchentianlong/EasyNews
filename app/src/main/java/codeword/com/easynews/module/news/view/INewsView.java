package codeword.com.easynews.module.news.view;

import java.util.List;

import codeword.com.easynews.base.BaseView;
import codeword.com.easynews.greendao.NewsChannelTable;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface INewsView extends BaseView{
    void initViewPager(List<NewsChannelTable> newsChannels);
    void initRxBusEvent();
}
