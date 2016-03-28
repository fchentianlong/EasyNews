package codeword.com.easynews.bean;

/**
 * ClassName: NewsChannel<p>
 * Author: oubowu<p>
 * Fuction: 频道管理的封装<p>
 * CreateDate: 2016/2/20 0:43<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
public class NewsChannel {

    /**
     * 频道名称
     */
    private String mName;
    /**
     * 频道是否固定频道
     */
    private boolean mFixed;

    private String mId;

    private String mType;

    private int mIndex;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public NewsChannel(String name, boolean fixed) {
        mName = name;
        mFixed = fixed;
    }

    public NewsChannel(String name, boolean fixed, String id, String type, int index) {
        mName = name;
        mFixed = fixed;
        mId = id;
        mType = type;
        mIndex = index;
    }

    public NewsChannel(String name) {
        mName = name;
    }

    public boolean isFixed() {
        return mFixed;
    }

    public void setFixed(boolean fixed) {
        mFixed = fixed;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
