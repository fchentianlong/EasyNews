package codeword.com.easynews.module.video.adapter;

import android.content.Context;
import android.graphics.RectF;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import codeword.com.easynews.R;
import codeword.com.easynews.bean.SinaPhotoDetail;
import codeword.com.easynews.utils.MeasureUtil;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/3/12.
 */
public class PhotoDetailAdapter extends PagerAdapter{
    private final int mWidth;
    private List<SinaPhotoDetail.SinaPhotoDetailPicsEntity> mPics;
    private Context context;


    public PhotoDetailAdapter(Context context,List<SinaPhotoDetail.SinaPhotoDetailPicsEntity> pics){
        this.context=context;
        mPics=pics==null?new ArrayList<>():pics;
        mWidth= MeasureUtil.getScreenSize(context).x;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final PhotoView photoView=new PhotoView(context);
        Glide.with(context).load(mPics.get(position).kpic).asBitmap()
                .placeholder(R.drawable.ic_loading_small_bg).error(R.drawable.ic_fail)
                .format(DecodeFormat.PREFER_ARGB_8888).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(photoView);
        photoView.setTag(R.id.img_tag, position);
        photoView.setOnMatrixChangeListener(new PhotoViewAttacher.OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                if (mOnPhotoExpandListener != null && rect.left < -20 && rect.right > mWidth + 20) {
                    // 显示标题
                    mOnPhotoExpandListener.onExpand(false, (int) photoView.getTag(R.id.img_tag));
                    mPics.get(position).showTitle = false;
                } else if (mOnPhotoExpandListener != null && rect.left >= -20 && rect.right <= mWidth + 20) {
                    // 隐藏标题
                    mOnPhotoExpandListener.onExpand(true, (int) photoView.getTag(R.id.img_tag));
                    mPics.get(position).showTitle = true;
                }
            }
        });
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;


    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mPics.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }



    public void setOnPhotoExpandListener(OnPhotoExpandListener onPhotoExpandListener) {
        mOnPhotoExpandListener = onPhotoExpandListener;
    }

    private OnPhotoExpandListener mOnPhotoExpandListener;

    /**
     * 图片被拉伸的监听接口
     */
    public interface OnPhotoExpandListener {
        void onExpand(boolean show, int position);
    }

    public List<SinaPhotoDetail.SinaPhotoDetailPicsEntity> getPics() {
        return mPics;
    }
}
