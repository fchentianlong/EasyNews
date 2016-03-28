package codeword.com.easynews.callback;

import android.view.View;

/**
 * Created by Administrator on 2016/3/9.
 */
public interface OnItemClickListener {
    void onItemClick(View view,int position);
    void onItemLongClick(View view,int position);
}
