package codeword.com.easynews.module.setting.ui;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.norbsoft.typefacehelper.ActionBarHelper;
import com.norbsoft.typefacehelper.TypefaceCollection;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.zhy.changeskin.SkinManager;

import butterknife.Bind;
import codeword.com.easynews.R;
import codeword.com.easynews.annotation.ActivityFragmentInject;
import codeword.com.easynews.app.App;
import codeword.com.easynews.base.BaseActivity;
import codeword.com.easynews.module.setting.presenter.ISettingPresenter;
import codeword.com.easynews.module.setting.presenter.ISettingPresenterImpl;
import codeword.com.easynews.module.setting.view.ISettingView;
import codeword.com.easynews.utils.ClickUtils;
import codeword.com.easynews.utils.RxBus;
import codeword.com.easynews.utils.SpUtil;
import codeword.com.easynews.utils.ViewUtil;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

/**
 * Created by Administrator on 2016/3/7.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_setting,
        menuId = R.menu.menu_settings,
        hasNavigationView = true,
        toolbarTitle = R.string.settings,
        toolbarIndicator = R.drawable.ic_list_white,
        menuDefaultCheckedItem = R.id.action_settings)
public class SettingActivity extends BaseActivity<ISettingPresenter> implements ISettingView{

    @Bind(R.id.ctv_night_mode)
    CheckedTextView mNightModeCheckedTextView;
    @Bind(R.id.ctv_slide_mode)
    CheckedTextView mSlideModeCheckedTextView;
    @Bind(R.id.tv_about)
    TextView mTvAbout;
    @Bind(R.id.tv_fonts)
    TextView mTvFont;

    @Override
    protected void initView() {
        mNightModeCheckedTextView.setOnClickListener(this);
        mSlideModeCheckedTextView.setOnClickListener(this);
        mTvAbout.setOnClickListener(this);
        mTvFont.setOnClickListener(this);

        mPresenter=new ISettingPresenterImpl(this);
    }

    @Override
    public void initItemState() {
        applyTint(mNightModeCheckedTextView);
        applyTint(mSlideModeCheckedTextView);

        mNightModeCheckedTextView.setChecked(SpUtil.readBoolean("enableNightMode"));
        mNightModeCheckedTextView
                .setText(SpUtil.readBoolean("enableNightMode") ? "关闭夜间模式" : "开启夜间模式");
        mSlideModeCheckedTextView.setChecked(!SpUtil.readBoolean("disableSlide"));
        mSlideModeCheckedTextView
                .setText(!SpUtil.readBoolean("disableSlide") ? "关闭侧滑返回" : "开启侧滑返回");

        mTvFont.setText(SpUtil.readString("font_type").equals("1")?"华文简体":"默认字体");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ctv_night_mode:
                if (ClickUtils.isFastDoubleClick())return;

                final boolean nightModel=!((CheckedTextView) v).isChecked();
                ((CheckedTextView)v).setChecked(nightModel);
                SkinManager.getInstance().changeSkin(nightModel ? "night" : "");
                SpUtil.writeBoolean("enableNightMode", nightModel);
                // 这里设置主题不起作用，但是我们弹窗时候的主题和着色时的颜色状态列表属性是需要主题支持的
                setTheme(nightModel ? R.style.BaseAppThemeNight_AppTheme : R.style.BaseAppTheme_AppTheme);

                mNightModeCheckedTextView.setText(nightModel ? "关闭夜间模式" : "开启夜间模式");

                applyTint(mNightModeCheckedTextView);
                applyTint(mSlideModeCheckedTextView);

                // 主题更改了，发送消息通知其他导航Activity销毁掉
                RxBus.get().post("finish", true);
                break;
            case R.id.ctv_slide_mode:
                final boolean slideModeCheck=!((CheckedTextView)v).isChecked();
                mSlideModeCheckedTextView.setText(slideModeCheck?"关闭侧滑返回" : "开启侧滑返回");
                ((CheckedTextView) v).setChecked(slideModeCheck);

                SpUtil.writeBoolean("disableSlide", !slideModeCheck);

                if (slideModeCheck){
                    String currentSlideMode = SpUtil
                            .readBoolean("enableSlideEdge") ? "边缘侧滑" : "整页侧滑";
                    new MaterialDialog.Builder(this).title("选择侧滑模式(当前为" + currentSlideMode + ")")
                            .items(R.array.slide_mode_items).itemsCallbackSingleChoice(SpUtil.readBoolean("enableSlideEdge") ? 0 : 1,
                            (dialog, view, which, text) -> {
                                SpUtil.writeBoolean("enableSlideEdge", which == 0);
                                return true;
                            }).positiveText("选择").show();
                }

                break;
            case R.id.tv_about:
                new MaterialDialog.Builder(this).content(R.string.about_info).show();
                break;
            case R.id.tv_fonts:
                String currentFontMode = SpUtil
                        .readString("font_type").equals("") ? "默认字体" : "华文简体";
                new MaterialDialog.Builder(this).title("当前字体("+ currentFontMode + ")")
                        .items(R.array.font_mode_items).itemsCallbackSingleChoice(SpUtil.readString("font_type").equals("")? 0 : 1,
                        (dialog, view, which, text) -> {
                            SpUtil.writeString("font_type", which == 0?"":"1");

                            //
                            RxBus.get().post("finish",true);
                            //重新初始化
                            ((App) getApplication()).initFonts();

                            if (which==0){
                                TypefaceHelper.init(((App) getApplication()).getDefaultTypeface());//默认字体
                            }else {
                                TypefaceHelper.init(((App) getApplication()).getActionMainTypeface());
                            }

                            final TextView email= (TextView) findViewById(R.id.email);
                            typeface(email);
                            typeface(mNightModeCheckedTextView);
                            typeface(mSlideModeCheckedTextView);
                            typeface(mTvAbout);
                            typeface(mTvFont);
                            //设置ActionBar
                            ActionBarHelper.setTitle(getSupportActionBar(), typeface(getSupportActionBar().getTitle()));

                            return true;
                        }).positiveText("确定").show();
                break;
        }
    }

    // 因为这里是通过鸿洋大神的换肤做的，而CheckedTextView着色不兼容5.0以下，
    // 所以切换皮肤的时候动态加载当前主题自定义的ColorStateList，对CheckMarkDrawable进行着色
    private void applyTint(CheckedTextView checkedTextView) {
        int[] attrs = {R.attr.checkTextViewColorStateList};
        TypedArray ta = getTheme().obtainStyledAttributes(attrs);
        ColorStateList indicator = ta.getColorStateList(0);
        ta.recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkedTextView.setCheckMarkTintList(indicator);
        } else {
            ViewUtil.tintDrawable(checkedTextView.getCheckMarkDrawable(), indicator);
        }
    }
}
