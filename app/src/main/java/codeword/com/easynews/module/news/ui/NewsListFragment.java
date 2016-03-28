package codeword.com.easynews.module.news.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import codeword.com.easynews.R;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.base.BaseFragment;
import codeword.com.easynews.base.BaseRecyclerAdapter;
import codeword.com.easynews.base.BaseRecyclerViewHolder;
import codeword.com.easynews.base.BaseSpacesItemDecoration;
import codeword.com.easynews.bean.NeteastNewsSummary;
import codeword.com.easynews.bean.SinaPhotoDetail;
import codeword.com.easynews.callback.OnItemClickAdapter;
import codeword.com.easynews.common.DataLoadType;
import codeword.com.easynews.module.news.presenter.INewsListPresenter;
import codeword.com.easynews.module.news.presenter.INewsListPresenterImpl;
import codeword.com.easynews.module.news.presenter.INewsPresenter;
import codeword.com.easynews.module.news.view.INewsListView;
import codeword.com.easynews.module.photo.ui.PhotoDetailActivity;
import codeword.com.easynews.utils.MeasureUtil;
import codeword.com.easynews.widget.AutoLoadMoreRecyclerView;
import codeword.com.easynews.widget.ThreePointLoadingView;
import codeword.com.easynews.widget.refresh.RefreshLayout;

/**
 * Created by Administrator on 2016/3/17.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragmnet_news_list)
public class NewsListFragment extends BaseFragment<INewsListPresenter> implements INewsListView{

    protected String mNewsId;
    protected static final String NEWS_ID = "news_id";
    protected String mNewsType;
    protected static final String NEWS_TYPE = "news_type";

    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)
    AutoLoadMoreRecyclerView mRecyclerView;
    @Bind(R.id.tpl_view)
    ThreePointLoadingView mLoadingView;


    private BaseRecyclerAdapter<NeteastNewsSummary> mAdapter;
    private SinaPhotoDetail mSinaPhotoDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            mNewsId=getArguments().getString(NEWS_ID);
            mNewsType=getArguments().getString(NEWS_TYPE);
        }
    }

    public static NewsListFragment newInstance(String newsId,String newsType){
        NewsListFragment fragment=new NewsListFragment();
        Bundle bundle=new Bundle();
        bundle.putString(NEWS_ID,newsId);
        bundle.putString(NEWS_TYPE,newsType);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    protected void initView(View fragmentRootView) {
        mPresenter=new INewsListPresenterImpl(this, mNewsId, mNewsType);
    }

    @Override
    public void updateNewsList(final List<NeteastNewsSummary> data, DataLoadType type) {
        switch (type){
            case TYPE_REFRESH_SUCCESS:
                mRefreshLayout.refreshFinish();
                if (mAdapter==null){
                    initNewsList(data);
                }else {
                    mAdapter.setData(data);
                }
                if (mRecyclerView.isAllLoaded()){
                    mRecyclerView.notifyMoreLoaded();
                }
                break;
            case TYPE_REFRESH_FAIL:
                mRefreshLayout.refreshFinish();
                break;
            case TYPE_LOAD_MORE_SUCCESS:
                mAdapter.hideFooter();
                if (data==null||data.size()==0){
                    mRecyclerView.notifyAllLoaded();
                    toast("全部加载完了");
                }else {
                    mAdapter.addMoreData(data);
                    mRecyclerView.notifyMoreLoaded();
                }
                break;
            case TYPE_LOAD_MORE_FAIL:
                mAdapter.hideFooter();
                break;
        }
    }


    private void initNewsList(List<NeteastNewsSummary> data){
        mAdapter=new BaseRecyclerAdapter<NeteastNewsSummary>(getActivity(),data) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_news_summary;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, NeteastNewsSummary item) {

                Glide.with(getActivity()).load(item.imgsrc).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_loading_small_bg).error(R.drawable.ic_fail)
                        .into(holder.getImageView(R.id.iv_news_summary_photo));
                holder.getTextView(R.id.tv_news_summary_title).setText(item.title);
                holder.getTextView(R.id.tv_news_summary_digest).setText(item.digest);
                holder.getTextView(R.id.tv_news_summary_ptime).setText(item.ptime);
            }
        };

        mAdapter.setOnItemClickListener(new OnItemClickAdapter() {
            @Override
            public void onItemClick(View view, int position) {
               view=view.findViewById(R.id.iv_news_summary_photo);
                if (mAdapter.getData().get(position).postid==null){
                    toast("此新闻浏览不了哎╮(╯Д╰)╭");
                    return;
                }

                // 跳转到新闻详情
                if (!TextUtils.isEmpty(mAdapter.getData().get(position).digest)){
                    Intent intent=new Intent(getActivity(),NewsDetailActivity.class);
                    intent.putExtra("postid", mAdapter.getData().get(position).postid);
                    intent.putExtra("imgsrc", mAdapter.getData().get(position).imgsrc);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(getActivity(),view.findViewById(R.id.iv_news_summary_photo),"photos");
                        getActivity().startActivity(intent,options.toBundle());
                    }else {
                        //让新的Activity从一个小的范围扩大到全屏
                        ActivityOptionsCompat options=ActivityOptionsCompat.makeScaleUpAnimation(view,view.getWidth()/2,view.getHeight()/2,0,0);
                        ActivityCompat.startActivity(getActivity(),intent,options.toBundle());
                    }

                }else {
                    // 以下将数据封装成新浪需要的格式，用于点击跳转到图片浏览
                    mSinaPhotoDetail = new SinaPhotoDetail();
                    mSinaPhotoDetail.data = new SinaPhotoDetail.SinaPhotoDetailDataEntity();
                    mSinaPhotoDetail.data.title = data.get(position).title;
                    mSinaPhotoDetail.data.content = "";
                    mSinaPhotoDetail.data.pics = new ArrayList<>();
                    // 天啊，什么格式都有 --__--
                    if (data.get(position).ads != null) {
                        for (NeteastNewsSummary.AdsEntity entiity : data.get(position).ads) {
                            SinaPhotoDetail.SinaPhotoDetailPicsEntity sinaPicsEntity = new SinaPhotoDetail.SinaPhotoDetailPicsEntity();
                            sinaPicsEntity.pic = entiity.imgsrc;
                            sinaPicsEntity.alt = entiity.title;
                            sinaPicsEntity.kpic = entiity.imgsrc;
                            mSinaPhotoDetail.data.pics.add(sinaPicsEntity);
                        }
                    } else if (data.get(position).imgextra != null) {
                        for (NeteastNewsSummary.ImgextraEntity entiity : data
                                .get(position).imgextra) {
                            SinaPhotoDetail.SinaPhotoDetailPicsEntity sinaPicsEntity = new SinaPhotoDetail.SinaPhotoDetailPicsEntity();
                            sinaPicsEntity.pic = entiity.imgsrc;
                            sinaPicsEntity.kpic = entiity.imgsrc;
                            mSinaPhotoDetail.data.pics.add(sinaPicsEntity);
                        }
                    }

                    Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                    intent.putExtra("neteast", mSinaPhotoDetail);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2,
                                    0, 0);
                    ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                }
            }
        });

        final LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setAutoLayoutManager(layoutManager).setAutoHasFixedSize(true)
                .addAutoItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(getActivity(),4)))
                .setAutoItemAnimator(new DefaultItemAnimator()).setAutoAdapter(mAdapter);

        mRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                mPresenter.loadMoreData();
                mAdapter.showFooter();
                mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
            }
        });

        mRefreshLayout.setRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefreshing() {
                mPresenter.refreshData();
            }
        });
    }

    @Override
    public void showProgress() {
        mLoadingView.play();
    }

    @Override
    public void hideProgress() {
        mLoadingView.stop();
    }
}
