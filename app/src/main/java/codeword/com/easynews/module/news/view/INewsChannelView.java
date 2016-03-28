package codeword.com.easynews.module.news.view;

import java.util.List;

import codeword.com.easynews.base.BaseView;
import codeword.com.easynews.greendao.NewsChannelTable;

/**
 * Created by Administrator on 2016/3/21.
 */
public interface INewsChannelView extends BaseView{
    void initTwoRecyclerView(List<NewsChannelTable> selectChannels,List<NewsChannelTable> unSelectChannels);
}
