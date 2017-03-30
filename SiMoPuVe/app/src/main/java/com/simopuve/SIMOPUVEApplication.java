package com.simopuve;

import android.app.Application;
import android.content.Context;

/**
 * Created by aldorangel on 3/29/17.
 */

public class SIMOPUVEApplication extends Application {
    private static SIMOPUVEApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        this.setAppContext(getApplicationContext());
    }

    public static SIMOPUVEApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }
}
