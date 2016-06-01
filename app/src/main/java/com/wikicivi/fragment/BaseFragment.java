package com.wikicivi.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wikicivi.net.HttpOpenHelper;
import com.wikicivi.net.NetCallBack;

import org.xutils.x;

/**
 * Fragment基类
 * Created by pro on 16/1/29.
 */
public class BaseFragment extends Fragment {

    private boolean injected = false;

    /**
     * 网络帮助类
     */
    protected HttpOpenHelper httpOpenHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        injected = true;
        httpOpenHelper = new HttpOpenHelper();
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!injected) {
            x.view().inject(this, this.getView());
        }
    }
    /**
     * 请求数据
     */
    public void post(Object param, NetCallBack netCallBack) {

        httpOpenHelper.post(param, netCallBack);
    }
}
