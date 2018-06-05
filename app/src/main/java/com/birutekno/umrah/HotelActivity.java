package com.birutekno.umrah;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.birutekno.umrah.ui.BaseActivity;

import butterknife.Bind;

public class HotelActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, HotelActivity.class);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_hotel;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        setupToolbar(mToolbar, true);
        setTitle("");
    }
}