package codeword.com.easynews.app;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;

import com.norbsoft.typefacehelper.TypefaceCollection;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.socks.library.KLog;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zhy.changeskin.SkinManager;

import codeword.com.easynews.BuildConfig;
import codeword.com.easynews.common.Constant;
import codeword.com.easynews.greendao.DaoMaster;
import codeword.com.easynews.greendao.DaoSession;
import codeword.com.easynews.utils.SpUtil;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2016/3/4.
 */
public class App extends Application{

    private RefWatcher mRefWatcher;
    private DaoSession mDaoSession;
    private static Context mApplicationContext;
    private TypefaceCollection defaultTypeface;
    private TypefaceCollection actionMainTypeface;
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化换肤
        SkinManager.getInstance().init(this);
        //初始化内存监测
        mRefWatcher= LeakCanary.install(this);
        //初始化数据库
        initDatabase();
        mApplicationContext=this;
        //初始化日志
        KLog.init(BuildConfig.DEBUG);

        //初始化字体
        initFonts();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    /**
     * 初始化数据库
     */
    private void initDatabase(){
        DaoMaster.DevOpenHelper helper=new DaoMaster.DevOpenHelper(this, Constant.DB_NAME,null);
        SQLiteDatabase db=helper.getWritableDatabase();
        DaoMaster daoMaster=new DaoMaster(db);
        mDaoSession=daoMaster.newSession();
        // 在 QueryBuilder 类中内置两个 Flag 用于方便输出执行的 SQL 语句与传递参数的值
        QueryBuilder.LOG_SQL=BuildConfig.DEBUG;
        QueryBuilder.LOG_VALUES=BuildConfig.DEBUG;
    }

    /**
     * 获取内存监测对象
     * @param context
     * @return
     */
    public static RefWatcher getRefWatcher(Context context){
        App application=(App)context.getApplicationContext();
        return application.mRefWatcher;
    }

    /**
     * 获取application对象
     * @return
     */
    public static Context getContext(){
        return mApplicationContext;
    }

    /**
     *
     * @return
     */
    public DaoSession getmDaoSession() {
        return mDaoSession;
    }


    /**
     * 初始化字体
     */
    public void initFonts(){
        //assesst文件夹读取
        if (SpUtil.readString("font_type").equals("1")){
            if (actionMainTypeface==null){
                actionMainTypeface = new TypefaceCollection.Builder().set(Typeface.NORMAL,
                        Typeface.createFromAsset(getAssets(), "fonts/SF_Arch_Rival.ttf")).create();
            }
            TypefaceHelper.init(actionMainTypeface);

        }else {
            if(defaultTypeface==null){
                defaultTypeface=TypefaceCollection.createSystemDefault();
            }
            TypefaceHelper.init(defaultTypeface);
        }

    }

    public TypefaceCollection getDefaultTypeface() {
        return defaultTypeface;
    }

    public TypefaceCollection getActionMainTypeface() {
        return actionMainTypeface;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
