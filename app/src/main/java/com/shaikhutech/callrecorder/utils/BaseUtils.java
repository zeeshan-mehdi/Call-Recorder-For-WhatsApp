package com.shaikhutech.callrecorder.utils;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseUtils extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    protected void init() {
        initView();
        initData();
        initEvent();
    }

    protected abstract void initView();


    protected abstract void initData();


    protected abstract void initEvent();

}
