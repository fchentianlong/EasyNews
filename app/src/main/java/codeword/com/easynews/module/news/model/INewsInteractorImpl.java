package codeword.com.easynews.module.news.model;

import java.util.Arrays;
import java.util.List;

import codeword.com.easynews.R;
import codeword.com.easynews.app.App;
import codeword.com.easynews.callback.RequestCallBack;
import codeword.com.easynews.greendao.NewsChannelTable;
import codeword.com.easynews.greendao.NewsChannelTableDao;
import codeword.com.easynews.http.Api;
import codeword.com.easynews.utils.SpUtil;
import de.greenrobot.dao.query.Query;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/3/17.
 */
public class INewsInteractorImpl implements INewsInteractor<List<NewsChannelTable>>{
    @Override
    public Subscription operateChannelDb(RequestCallBack<List<NewsChannelTable>> callBack) {
        return Observable.create(new Observable.OnSubscribe<List<NewsChannelTable>>() {
            @Override
            public void call(Subscriber<? super List<NewsChannelTable>> subscriber) {
                final NewsChannelTableDao dao=((App) App.getContext()).getmDaoSession().getNewsChannelTableDao();
                if (!SpUtil.readBoolean("initDb")){
                    List<String> channelNames= Arrays.asList(App.getContext().getResources().getStringArray(R.array.news_channel));
                    List<String> channelIds=Arrays.asList(App.getContext().getResources().getStringArray(R.array.news_channel_id));
                    for (int i=0;i<channelNames.size();i++){
                        NewsChannelTable table=new NewsChannelTable(channelNames.get(i),channelIds.get(i), Api.getType(channelIds.get(i)),i<=2,i,i<=2);
                        dao.insert(table);
                    }

                    SpUtil.writeBoolean("initDb",true);
                }

                final Query<NewsChannelTable> build=dao.queryBuilder().where(NewsChannelTableDao.Properties.NewsChannelSelect.eq(true))
                        .orderAsc(NewsChannelTableDao.Properties.NewsChannelIndex).build();
                subscriber.onNext(build.list());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(new Action0() {
            @Override
            public void call() {
                callBack.beforeRequest();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<NewsChannelTable>>() {
            @Override
            public void onCompleted() {
                callBack.requestComplete();
            }

            @Override
            public void onError(Throwable e) {
                callBack.requestError(e.getLocalizedMessage().toString());
            }

            @Override
            public void onNext(List<NewsChannelTable> newsChannelTables) {
                callBack.requestSuccess(newsChannelTables);
            }
        });
    }
}
