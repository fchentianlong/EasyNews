package codeword.com.easynews.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import codeword.com.easynews.R;
import codeword.com.easynews.callback.OnItemClickListener;
import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

/**
 * Created by Administrator on 2016/3/9.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder>{
    protected List<T> mData;
    protected Context mContext;
    protected boolean mUseAnimation;
    private RecyclerView.LayoutManager mLayoutManager;
    protected LayoutInflater mInflater;
    protected OnItemClickListener mClickListener;

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 2;
    public static final int TYPE_FOOTER = 3;

    private int lastPosition = -1;

    protected boolean mShowFooter;

    public BaseRecyclerAdapter(Context context,List<T> data){
        this(context,data,true);
    }

    public BaseRecyclerAdapter(Context context,List<T> data,boolean useAnimation){
        this(context,data,useAnimation,null);
    }

    public BaseRecyclerAdapter(Context context,List<T> data,boolean useAnimation,RecyclerView.LayoutManager layoutManager){
        mContext=context;
        mUseAnimation=useAnimation;
        mLayoutManager=layoutManager;
        mData=data==null?new ArrayList<T>():data;
        mInflater=LayoutInflater.from(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_FOOTER){
            return new BaseRecyclerViewHolder(mContext,mInflater.inflate(R.layout.item_footer,parent,false));
        }else {
            View view=mInflater.inflate(getItemLayoutId(viewType),parent,false);
            typeface(view);
            final BaseRecyclerViewHolder holder=new BaseRecyclerViewHolder(mContext,view);
            if (mClickListener!=null){
                holder.itemView.setOnClickListener(v->{
                    mClickListener.onItemClick(v,holder.getLayoutPosition());
                });
            }

            return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
            if (getItemViewType(position)==TYPE_FOOTER){
                if (mLayoutManager!=null){
                    if (mLayoutManager instanceof StaggeredGridLayoutManager){
                        if (((StaggeredGridLayoutManager)mLayoutManager).getSpanCount()!=1){
                            StaggeredGridLayoutManager.LayoutParams layoutParams=(StaggeredGridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
                            layoutParams.setFullSpan(true);
                        }

                    }else if (mLayoutManager instanceof GridLayoutManager){
                        if (((GridLayoutManager)mLayoutManager).getSpanCount()!=1&&((GridLayoutManager)mLayoutManager).getSpanSizeLookup() instanceof GridLayoutManager.DefaultSpanSizeLookup){
                            throw new RuntimeException("网格布局列数大于1时应该继承SpanSizeLookup时处理底部加载时布局占满一行。");
                        }
                    }
                }
                holder.getPacman(R.id.pac_man).performLoading();
            }else {
                bindData(holder,position,mData.get(position));
                if (mUseAnimation){
                    setAnimation(holder.itemView, position);
                }
            }
    }

    @Override
    public void onViewDetachedFromWindow(BaseRecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mUseAnimation&&holder.itemView.getAnimation()!=null&&holder.itemView.getAnimation().hasStarted()){
            holder.itemView.clearAnimation();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mShowFooter && getItemCount() - 1 == position) {
            return TYPE_FOOTER;
        }
        return bindViewType(position);
    }

    @Override
    public int getItemCount() {
        int i = mShowFooter ? 1 : 0;
        // KLog.e("插入: "+i);
        return mData != null ? mData.size() + i : 0;
    }


    /**
     * 设置动画
     * @param view
     * @param position
     */
    protected void setAnimation(View view,int position){
        if (position>lastPosition){
            Animation animation= AnimationUtils.loadAnimation(view.getContext(),R.anim.item_bottom_in);
            view.startAnimation(animation);
            lastPosition=position;
        }


    }

    public void add(int position, T item){
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public void delete(int position){
        mData.remove(position);
        notifyItemRemoved(position);

    }

    public void addMoreData(List<T> data){
        int startPos=mData.size();
        mData.addAll(data);
        notifyItemRangeChanged(startPos,data.size());
    }



    public List<T> getData(){
        return mData;
    }

    public void setData(List<T> data){
        mData=data;
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mClickListener=listener;
    }

    public abstract int getItemLayoutId(int viewType);
    public abstract void bindData(BaseRecyclerViewHolder holder,int position,T item);

    protected int bindViewType(int position){
        return 0;
    }

    public void showFooter(){
        notifyItemInserted(getItemCount());
        mShowFooter=true;
    }

    public void hideFooter(){
        notifyItemRemoved(getItemCount()-1);
        mShowFooter=false;
    }



}
