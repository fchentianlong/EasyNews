package codeword.com.easynews.module.video.ui;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.socks.library.KLog;

import butterknife.Bind;
import codeword.com.easynews.R;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.base.BaseActivity;
import codeword.com.easynews.module.video.presenter.IVideoPlayPresenter;
import codeword.com.easynews.module.video.presenter.IVideoPlayPresenterImpl;
import codeword.com.easynews.module.video.view.IVideoPlayView;
import codeword.com.easynews.utils.VideoPlayController;
import codeword.com.easynews.utils.ViewUtil;
import codeword.com.easynews.widget.ThreePointLoadingView;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Administrator on 2016/3/11.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_video_play,
enableSlidr = true)
public class VideoPlayActivity extends BaseActivity<IVideoPlayPresenter> implements IVideoPlayView{

    @Bind(R.id.tpl_view)
    ThreePointLoadingView mLoadingView;
    @Bind(R.id.rl_bg)
    View mBgView;
    @Bind(R.id.video_view)
    VideoView mVideoView;


    private VideoPlayController mPlayController;

    @Override
    protected void initView() {
        //   主题设置了<item name="android:windowIsTranslucent">true</item>不能自动旋转屏幕了，这里强制开启就可以了 -_-
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        String videoUrl = getIntent().getStringExtra("videoUrl");

        mBgView.setOnClickListener(this);
        mLoadingView.setOnClickListener(this);
        mVideoView.setZOrderOnTop(true);
        mVideoView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        mPresenter=new IVideoPlayPresenterImpl(this,videoUrl);
    }

    @Override
    public void playVideo(String path) {
        if (Vitamio.isInitialized(getApplicationContext())) {
            mVideoView.setVideoPath(path);
            mPlayController = new VideoPlayController(this, mVideoView, mBgView);
            mVideoView.requestFocus();
            mVideoView.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 解决与背景点击事件的冲突
                    mBgView.performClick();
                }

                return true;
            });

            mVideoView.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
                @Override
                public void onTimedText(String text) {
                    KLog.e(text);
                }

                @Override
                public void onTimedTextUpdate(byte[] pixels, int width, int height) {

                }
            });

            mVideoView.setOnPreparedListener((mediaPlayer) -> {
                mediaPlayer.setPlaybackSpeed(1.0f);
                hideProgress();
            });

            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    hideProgress();
                    toast("视频播放出错了╮(╯Д╰)╭");
                    return false;
                }
            });
        }else {
            toast("播放器还没初始化完哎，等等咯╮(╯Д╰)╭ ");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mVideoView.setVisibility(View.INVISIBLE);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_bg && mPlayController != null) {
            if (mPlayController.isShowing()) {
                mPlayController.hide();
            } else {
                mPlayController.show();
            }
        }
    }


    @Override
    public void showProgress() {
        mLoadingView.play();
    }

    @Override
    public void hideProgress() {
        mLoadingView.stop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewUtil.setFullScreen(this, newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
        // 保持屏幕比例正确
        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        mPlayController.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayController!=null){
            mPlayController.onDestroy();
        }
    }
}
