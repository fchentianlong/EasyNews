package codeword.com.easynews.module.news.model;

import codeword.com.easynews.callback.RequestCallBack;
import rx.Subscription;

/**
 * Created by Administrator on 2016/3/21.
 */
public interface INewsChannelInteractor<T> {
    /**
     * 初始化查询或增删频道或排序，更新数据库
     *
     * @param callback
     * @param channelName
     * @param selectState null时为初始化查询，true为选中插入数据库，false为移除出数据库
     * @return
     */
    Subscription channelDbOperate(RequestCallBack<T> callback, String channelName, Boolean selectState);

    /**
     * 拖拽时候更新数据库
     *
     * @param callback
     * @param fromPos
     * @param toPos
     * @return
     */
    Subscription channelDbSwap(RequestCallBack<T> callback, int fromPos, int toPos);
}
