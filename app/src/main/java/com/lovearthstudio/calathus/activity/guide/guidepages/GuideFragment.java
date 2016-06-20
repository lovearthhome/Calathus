package com.lovearthstudio.calathus.activity.guide.guidepages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.x;

/**
 * 下面这个链接描述了如何取消fragment的预加载
 * http://www.2cto.com/kf/201501/368954.html
 * Fragment基类,这个类的设计
 * Created by pro on 16/1/29.
 */
public abstract  class GuideFragment extends Fragment {
    private boolean injected = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        injected = true;
        return x.view().inject(this, inflater, container);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!injected) {
            x.view().inject(this, this.getView());
        }
    }

    /** Fragment当前状态是否可见 */
    protected boolean isVisible;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * 不可见
     */
    protected void onInvisible() {
        lazyExit();
    }
    /**
     * 延迟加载
     * 子类必须重写此方法
     * fixme:lazyexit有可能在Oncreate之前执行
     */
    protected abstract void lazyLoad();

    protected abstract void lazyExit();


}
