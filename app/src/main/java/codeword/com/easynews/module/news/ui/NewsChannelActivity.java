package codeword.com.easynews.module.news.ui;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import codeword.com.easynews.R;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.base.BaseActivity;
import codeword.com.easynews.base.BaseFragment;
import codeword.com.easynews.base.BaseSpacesItemDecoration;
import codeword.com.easynews.callback.OnItemClickAdapter;
import codeword.com.easynews.callback.SimpleItemTouchHelperCallback;
import codeword.com.easynews.greendao.NewsChannelTable;
import codeword.com.easynews.module.news.adapter.NewsChannelAdapter;
import codeword.com.easynews.module.news.presenter.INewsChannelPresenter;
import codeword.com.easynews.module.news.presenter.INewsChannelPresenterImpl;
import codeword.com.easynews.module.news.view.INewsChannelView;
import codeword.com.easynews.utils.MeasureUtil;
import codeword.com.easynews.utils.TextViewUtil;

/**
 * Created by Administrator on 2016/3/21.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_news_channel,
menuId = R.menu.menu_settings,
enableSlidr = true,
toolbarTitle = R.string.news_channel_manage)
public class NewsChannelActivity extends BaseActivity<INewsChannelPresenter> implements INewsChannelView{

    @Bind(R.id.tv_my_channel)
    TextView myTextView;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView1;
    @Bind(R.id.recycler_view2)
    RecyclerView recyclerView2;

    @Override
    protected void initView() {
        final float textSize=myTextView.getTextSize();
        int startPos=myTextView.getText().toString().lastIndexOf(" ");
        TextViewUtil.setPartialSize(myTextView,startPos,myTextView.getText().toString().length(),(int) (textSize * 3 * 1.0f / 4));

        mPresenter=new INewsChannelPresenterImpl(this);

    }

    @Override
    public void initTwoRecyclerView(List<NewsChannelTable> selectChannels, List<NewsChannelTable> unSelectChannels) {
        recyclerView1.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        recyclerView1.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this, 8)));
        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.getItemAnimator().setAddDuration(250);
        recyclerView1.getItemAnimator().setMoveDuration(250);
        recyclerView1.getItemAnimator().setChangeDuration(250);
        recyclerView1.getItemAnimator().setRemoveDuration(250);

        final NewsChannelAdapter mRecyclerAdapter1=new NewsChannelAdapter(this,selectChannels,false);
        recyclerView1.setAdapter(mRecyclerAdapter1);
        // 只有我的频道可以拖拽排序
        SimpleItemTouchHelperCallback callback1 = new SimpleItemTouchHelperCallback(
                mRecyclerAdapter1);
        ItemTouchHelper itemTouchHelper1 = new ItemTouchHelper(callback1);
        itemTouchHelper1.attachToRecyclerView(recyclerView1);
        mRecyclerAdapter1.setItemTouchHelper(callback1);

        mRecyclerAdapter1.setItemMoveListener(new NewsChannelAdapter.OnItemMoveListener() {
            @Override
            public void onItemMove(int fromPosition, int toPosition) {
                // 拖拽交换位置的时候通知代理更新数据库
                mPresenter.onItemSwap(fromPosition, toPosition);
            }
        });

        // 初始化更多频道RecyclerView
        final RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view2);
        recyclerView2.setLayoutManager(
                new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        recyclerView2.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this, 8)));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.getItemAnimator().setAddDuration(250);
        recyclerView2.getItemAnimator().setMoveDuration(250);
        recyclerView2.getItemAnimator().setChangeDuration(250);
        recyclerView2.getItemAnimator().setRemoveDuration(250);

        final NewsChannelAdapter mRecyclerAdapter2 = new NewsChannelAdapter(this, unSelectChannels,
                false);
        recyclerView2.setAdapter(mRecyclerAdapter2);

        // 设置两个RecyclerView的点击监听，进行Item相应的增删操作
        mRecyclerAdapter1.setOnItemClickListener(new OnItemClickAdapter() {
            @Override
            public void onItemClick(View view, int position) {
                if (!mRecyclerAdapter1.getData().get(position).getNewsChannelFixed()) {
                    // 点击我的频道，不是固定的就删除，更多频道添加

                    // 通知代理把频道从数据库删掉
                    mPresenter.onItemAddOrRemove(
                            mRecyclerAdapter1.getData().get(position).getNewsChannelName(), false);

                    mRecyclerAdapter2.add(mRecyclerAdapter2.getItemCount(),
                            mRecyclerAdapter1.getData().get(position));

                    mRecyclerAdapter1.delete(position);

                }
            }
        });

        mRecyclerAdapter2.setOnItemClickListener(new OnItemClickAdapter() {
            @Override
            public void onItemClick(View view, int position) {
                // 点击更多频道，更多频道删除，我的频道添加

                // 通知代理把频道添加到数据库
                mPresenter.onItemAddOrRemove(
                        mRecyclerAdapter2.getData().get(position).getNewsChannelName(), true);


                mRecyclerAdapter1.add(mRecyclerAdapter1.getItemCount(),
                        mRecyclerAdapter2.getData().get(position));

                mRecyclerAdapter2.delete(position);
            }
        });


    }


}
