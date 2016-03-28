package codeword.com.easynews.module.photo.ui;

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
import codeword.com.easynews.bean.SinaPhotoList;
import codeword.com.easynews.callback.OnItemClickAdapter;
import codeword.com.easynews.common.DataLoadType;
import codeword.com.easynews.module.photo.presenter.IPhotoListPresenter;
import codeword.com.easynews.module.photo.presenter.IPhotoListPresenterImpl;
import codeword.com.easynews.module.photo.view.IPhotoListView;
import codeword.com.easynews.utils.MeasureUtil;
import codeword.com.easynews.widget.AutoLoadMoreRecyclerView;
import codeword.com.easynews.widget.ThreePointLoadingView;
import codeword.com.easynews.widget.refresh.RefreshLayout;

/**
 * Created by Administrator on 2016/3/12.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragmnet_photo_list)
public class PhotoListFragment extends BaseFragment<IPhotoListPresenter> implements IPhotoListView{
    private String mPhotoId;
    private static String PHOTOID="photo_id";

    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)
    AutoLoadMoreRecyclerView mRecyclerView;
    @Bind(R.id.tpl_view)
    ThreePointLoadingView mLoadingView;

    BaseRecyclerAdapter<SinaPhotoList.DataEntity.PhotoListEntity> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            mPhotoId=getArguments().getString(PHOTOID);
        }
    }


    public static PhotoListFragment newInstance(String photoId){
        PhotoListFragment fragment=new PhotoListFragment();
        Bundle bundle=new Bundle();
        bundle.putString(PHOTOID, photoId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View fragmentRootView) {
        mPresenter=new IPhotoListPresenterImpl(this,mPhotoId,0);

    }

    @Override
    public void updatePhotoList(List<SinaPhotoList.DataEntity.PhotoListEntity> data, DataLoadType type) {
        switch (type){
            case TYPE_REFRESH_SUCCESS:
                mRefreshLayout.refreshFinish();
                if (mAdapter==null){
                    initPhotoList(data);
                }else {
                    mAdapter.setData(data);
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
                mAdapter.hideFooter();
                if (data==null||data.size()==0){
                    mRecyclerView.notifyAllLoaded();
                    toast("全部加载完毕噜(☆＿☆)");
                }else {
                    mAdapter.addMoreData(data);
                    mRecyclerView.notifyMoreLoaded();
                }
                break;
            case TYPE_LOAD_MORE_FAIL:
                mAdapter.hideFooter();
                mRecyclerView.notifyMoreLoaded();
                break;
        }

    }

    private void initPhotoList(List<SinaPhotoList.DataEntity.PhotoListEntity> data){
        final StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mAdapter=new BaseRecyclerAdapter<SinaPhotoList.DataEntity.PhotoListEntity>(getActivity(),data,true,layoutManager) {
            Random mRandom = new Random();
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_photo_summary;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, SinaPhotoList.DataEntity.PhotoListEntity item) {
                final ImageView imageView=holder.getImageView(R.id.iv_photo_summary);
                final ViewGroup.LayoutParams layoutParams=imageView.getLayoutParams();
                if (item.picHeight==-1&&item.picWidth==-1){
                    item.picWidth = MeasureUtil.getScreenSize(getActivity()).x / 2 - MeasureUtil
                            .dip2px(getActivity(), 4) * 2 - MeasureUtil.dip2px(getActivity(), 2);
                    item.picHeight = (int) (item.picWidth * (mRandom.nextFloat() / 2 + 1));
                }

                layoutParams.width=item.picWidth;
                layoutParams.height=item.picHeight;
                imageView.setLayoutParams(layoutParams);

                Glide.with(getActivity()).load(item.kpic).asBitmap()
                        .placeholder(R.drawable.ic_loading_small_bg).error(R.drawable.ic_fail)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

                holder.getTextView(R.id.tv_photo_summary).setText(item.title);
            }
        };

        mAdapter.setOnItemClickListener(new OnItemClickAdapter() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                intent.putExtra("photoId", mAdapter.getData().get(position).id);
                //让新的Activity从一个小的范围扩大到全屏
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0,
                                0);
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        });

        mRecyclerView.setAutoLayoutManager(layoutManager).addAutoItemDecoration(
                new BaseSpacesItemDecoration(MeasureUtil.dip2px(getActivity(), 4)))
                .setAutoItemAnimator(new DefaultItemAnimator()).setAutoItemAnimatorDuration(250)
                .setAutoAdapter(mAdapter);

        mRecyclerView.setOnLoadMoreListener(()->{
            // 状态停止，并且滑动到最后一位
            mPresenter.loadMoreData();
            // 显示尾部加载
            // KLog.e("显示尾部加载前："+mAdapter.getItemCount());
            mAdapter.showFooter();
            // KLog.e("显示尾部加载后："+mAdapter.getItemCount());
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        });

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
