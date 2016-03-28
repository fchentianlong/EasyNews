package codeword.com.easynews.module.video.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import codeword.com.easynews.R;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.base.BaseFragment;
import codeword.com.easynews.base.BaseRecyclerAdapter;
import codeword.com.easynews.base.BaseRecyclerViewHolder;
import codeword.com.easynews.base.BaseSpacesItemDecoration;
import codeword.com.easynews.bean.NeteastVideoSummary;
import codeword.com.easynews.callback.OnItemClickAdapter;
import codeword.com.easynews.common.DataLoadType;
import codeword.com.easynews.module.video.presenter.IVideoListPresenter;
import codeword.com.easynews.module.video.presenter.IVideoListPresenterImpl;
import codeword.com.easynews.module.video.view.IVideoListView;
import codeword.com.easynews.utils.MeasureUtil;
import codeword.com.easynews.utils.NetUtil;
import codeword.com.easynews.widget.AutoLoadMoreRecyclerView;
import codeword.com.easynews.widget.ThreePointLoadingView;
import codeword.com.easynews.widget.refresh.RefreshLayout;

/**
 * Created by Administrator on 2016/3/8.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_video_list)
public class VideoListFragment extends BaseFragment<IVideoListPresenter> implements IVideoListView{

    private String mVideoId;
    private int mPosition;

    protected static String VIDEO_ID="video_id";
    protected static String POSITION="position";

    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)
    AutoLoadMoreRecyclerView mRecyclerView;
    @Bind(R.id.tpl_view)
    ThreePointLoadingView mLoadingView;


    private BaseRecyclerAdapter<NeteastVideoSummary> adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()!=null){
            mVideoId=getArguments().getString(VIDEO_ID);
            mPosition=getArguments().getInt(POSITION);
        }
    }

    /**
     *
     * @param videoId
     * @param position
     * @return
     */
    public static VideoListFragment newInstance(String videoId,int position){
        VideoListFragment fragment=new VideoListFragment();
        Bundle bundle=new Bundle();
        bundle.putString(VIDEO_ID,videoId);
        bundle.putInt(POSITION,position);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected void initView(View fragmentRootView) {
        mPresenter=new IVideoListPresenterImpl(this,mVideoId,0);
    }

    @Override
    public void updateVideoList(List<NeteastVideoSummary> data, DataLoadType type) {
            switch (type){
                case TYPE_REFRESH_SUCCESS:
                    mRefreshLayout.refreshFinish();
                    if (adapter==null){
                        initVideoList(data);
                    }else {
                        adapter.setData(data);
                    }
                    if (mRecyclerView.isAllLoaded()){
                        // 之前全部加载完了的话，这里把状态改回来供底部加载用
                        mRecyclerView.notifyMoreLoaded();
                    }
                    break;
                case TYPE_REFRESH_FAIL:
                    mRefreshLayout.refreshFinish();
                    break;
                case TYPE_LOAD_MORE_SUCCESS:
                    // 隐藏尾部加载
                    adapter.hideFooter();
                    if (data==null||data.size()==0){
                        mRecyclerView.isAllLoaded();
                        toast("全部加载完毕噜(☆＿☆)");
                    }else {
                        adapter.addMoreData(data);
                        mRecyclerView.notifyMoreLoaded();
                    }
                    break;
                case TYPE_LOAD_MORE_FAIL:
                    adapter.hideFooter();
                    mRecyclerView.notifyMoreLoaded();
                    break;
            }
    }

    /**
     *
     * @param data
     */
    private void initVideoList(List<NeteastVideoSummary> data){
        final StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        adapter=new BaseRecyclerAdapter<NeteastVideoSummary>(getActivity(),data,true,layoutManager) {
            Random mRandom=new Random();
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_video_summary;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, NeteastVideoSummary item) {
                final ImageView imageView=holder.getImageView(R.id.iv_video_summary);
                final ViewGroup.LayoutParams params=imageView.getLayoutParams();
                if (item.picWidth==-1&&item.picHeight==-1){
                    item.picWidth = MeasureUtil.getScreenSize(getActivity()).x / 2 - MeasureUtil
                            .dip2px(getActivity(), 4) * 2 - MeasureUtil.dip2px(getActivity(), 2);
                    item.picHeight = (int) (item.picWidth * (mRandom.nextFloat() / 2 + 0.7));
                }
                params.width=item.picWidth;
                params.height=item.picHeight;
                imageView.setLayoutParams(params);

                Glide.with(getActivity()).load(item.cover).asBitmap().placeholder(R.drawable.ic_loading_small_bg).error(R.drawable.ic_fail)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                holder.getTextView(R.id.tv_video_summary).setText(item.title);
            }
        };

        adapter.setOnItemClickListener(new OnItemClickAdapter() {
            @Override
            public void onItemClick(View view, int position) {

                if (!NetUtil.isConnected(getActivity())){
                    toast("网络异常，请检查网络播放视频");
                    return;
                }

                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                intent.putExtra("videoUrl", adapter.getData().get(position).mp4Url);
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0,
                                0);
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        });

        mRecyclerView.setAutoLayoutManager(layoutManager)
                .addAutoItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(getActivity(), 4)))
                .setAutoItemAnimator(new DefaultItemAnimator()).setAutoItemAnimatorDuration(250).setAutoAdapter(adapter);

        mRecyclerView.setOnLoadMoreListener(() -> {
            // 状态停止，并且滑动到最后一位
            mPresenter.loadMoreData();
            // 显示尾部加载
            // KLog.e("显示尾部加载前："+mAdapter.getItemCount());
            adapter.showFooter();
            // KLog.e("显示尾部加载后："+mAdapter.getItemCount());
            mRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
        });

        /**
         * 刷新数据
         */
        mRefreshLayout.setRefreshListener(()->{
            mPresenter.refreshData();
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
