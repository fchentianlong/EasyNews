package codeword.com.easynews.module.news.ui;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import butterknife.Bind;
import codeword.com.easynews.R;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.base.BaseActivity;
import codeword.com.easynews.bean.NeteastNewsDetail;
import codeword.com.easynews.bean.SinaPhotoDetail;
import codeword.com.easynews.module.news.presenter.INewsDetailPresenter;
import codeword.com.easynews.module.news.presenter.INewsDetailPresenterImpl;
import codeword.com.easynews.module.news.view.INewsDetailView;
import codeword.com.easynews.module.photo.ui.PhotoDetailActivity;
import codeword.com.easynews.utils.MeasureUtil;
import codeword.com.easynews.widget.ThreePointLoadingView;
import zhou.widget.RichText;

/**
 * Created by Administrator on 2016/3/25.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_news_detail,
enableSlidr = true,
menuId = R.menu.menu_settings)
public class NewsDetailActivity extends BaseActivity<INewsDetailPresenter> implements INewsDetailView{

   /* @Bind(R.id.tpl_view)
    ThreePointLoadingView mLoadingView;*/
    @Bind(R.id.iv_news_detail_photo)
    ImageView mNewsImageView;
    @Bind(R.id.tv_news_detail_title)
    TextView mTvTitle;
    @Bind(R.id.tv_news_detail_from)
    TextView mTvFrom;
    @Bind(R.id.tv_news_detail_body)
    RichText mTvBody;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @Bind(R.id.fab)
    FloatingActionButton mFab;


    private String mNewsListSrc;
    private SinaPhotoDetail mSinaPhotoDetail;

    @Override
    protected void initView() {
        mToolbarLayout.setTitle(getString(R.string.news_detail));
        mToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.accent));
        mToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.material_white));

        materialCollapsingForKitkat(mToolbarLayout);

        mNewsListSrc = getIntent().getStringExtra("imgsrc");
        mFab.setOnClickListener(this);

        mPresenter=new INewsDetailPresenterImpl(this,getIntent().getStringExtra("postid"));
    }



    @Override
    public void initDetailNews(NeteastNewsDetail data) {
        if (data.img!=null&&data.img.size()>0){
            // 设置tag用于点击跳转浏览图片列表的时候判断是否有图片可供浏览
            mNewsImageView.setTag(R.id.img_tag,true);
            // 显示第一张图片，通过pixel字符串分割得到图片的分辨率
            String[] pixel=null;
            if (!TextUtils.isEmpty(data.img.get(0).pixel)){
                pixel=data.img.get(0).pixel.split("\\*");
            }
            // 图片高清显示，按屏幕宽度为准缩放
            if (pixel!=null&&pixel.length==2){
                int w=MeasureUtil.getScreenSize(this).x;
                int h=Integer.parseInt(pixel[1])*w/Integer.parseInt(pixel[0]);

                if (data.img.get(0).src.contains(".gif")){
                    Glide.with(this).load(data.img.get(0).src).asGif()
                            .placeholder(R.drawable.ic_loading_small_bg).error(R.drawable.ic_fail)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(w,h)
                            .into(mNewsImageView);
                }else {
                    Glide.with(this).load(data.img.get(0).src).asBitmap()
                            .placeholder(R.drawable.ic_loading_small_bg).format(DecodeFormat.PREFER_ARGB_8888)
                            .error(R.drawable.ic_fail)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(w,h)
                            .into(mNewsImageView);
                }
            }else {
                Glide.with(this).load(data.img.get(0).src).asBitmap()
                        .placeholder(R.drawable.ic_loading_small_bg).error(R.drawable.ic_fail)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mNewsImageView);
            }

            // 以下将数据封装成新浪需要的格式，用于点击跳转到图片浏览
            mSinaPhotoDetail=new SinaPhotoDetail();
            mSinaPhotoDetail.data=new SinaPhotoDetail.SinaPhotoDetailDataEntity();
            mSinaPhotoDetail.data.title=data.title;
            mSinaPhotoDetail.data.content=data.digest;
            mSinaPhotoDetail.data.pics=new ArrayList<>();
            for (NeteastNewsDetail.ImgEntity imgEntity : data.img) {
                SinaPhotoDetail.SinaPhotoDetailPicsEntity picEntity=new SinaPhotoDetail.SinaPhotoDetailPicsEntity();
                picEntity.pic=imgEntity.src;
                picEntity.alt=imgEntity.alt;
                picEntity.kpic=imgEntity.src;
                if (pixel!=null&&pixel.length==2){
                    picEntity.size=pixel[0]+"X"+pixel[1];
                }

                mSinaPhotoDetail.data.pics.add(picEntity);
            }
        }else {
            // 图片详情列表没有数据的时候，取图片列表页面传送过来的图片显示
            mNewsImageView.setTag(R.id.img_tag,false);
            Glide.with(this).load(mNewsListSrc).asBitmap()
                    .placeholder(R.drawable.ic_loading_small_bg).format(DecodeFormat.PREFER_ARGB_8888)
                    .error(R.drawable.ic_fail)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mNewsImageView);
        }

        mTvTitle.setText(data.title);
        mTvFrom.setText(getString(R.string.from, data.source, data.ptime));
        if (!TextUtils.isEmpty(data.body)){
            mTvBody.setRichText(data.body);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.fab){
            if (!(boolean)mNewsImageView.getTag(R.id.img_tag)||mSinaPhotoDetail==null){
                toast("没有图片供浏览哎o(╥﹏╥)o");
            }else {
                Intent intent=new Intent(this, PhotoDetailActivity.class);
                intent.putExtra("neteast",mSinaPhotoDetail);
                ActivityOptionsCompat options=ActivityOptionsCompat.makeScaleUpAnimation(v,v.getWidth()/2,v.getHeight()/2,0,0);
                ActivityCompat.startActivity(this,intent,options.toBundle());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
    }

    @Override
    public void hideProgress() {
//        mLoadingView.stop();
    }

    @Override
    public void showProgress() {
//       mLoadingView.play();
    }

    /**
     * 4.4设置全屏并动态修改Toolbar的位置实现类5.0效果，确保布局延伸到状态栏的效果
     *
     * @param toolbarLayout
     */
    private void materialCollapsingForKitkat(CollapsingToolbarLayout toolbarLayout){
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // 设置全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // 设置Toolbar对顶部的距离
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) toolbar
                    .getLayoutParams();
            final int statusBarHeight = layoutParams.topMargin = MeasureUtil
                    .getStatusBarHeight(this);
            // 算出伸缩移动的总距离
            final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            final int[] verticalMoveDistance = new int[1];
            mToolbarLayout.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            verticalMoveDistance[0] = mToolbarLayout
                                    .getMeasuredHeight() - MeasureUtil
                                    .getToolbarHeight(NewsDetailActivity.this);
                            mToolbarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                int lastVerticalOffset = 0;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    // KLog.e(lastVerticalOffset + ";" + verticalOffset);
                    if (lastVerticalOffset != verticalOffset && verticalMoveDistance[0] != 0) {
                        layoutParams.topMargin = (int) (statusBarHeight - Math
                                .abs(verticalOffset) * 1.0f / verticalMoveDistance[0] * statusBarHeight);
                        // KLog.e(layoutParams.topMargin);
                        toolbar.setLayoutParams(layoutParams);
                    }
                    lastVerticalOffset = verticalOffset;
                }
            });
        }
    }
}
