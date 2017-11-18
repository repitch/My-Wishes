package com.repitch.mywishes;

import android.app.Application;

import com.repitch.mywishes.db.helper.DbHelper;

import timber.log.Timber;

/**
 * Created by repitch on 24.08.17.
 */
public class App extends Application {

    private static App appInstance;

    public static App getInstance() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        DbHelper.init(getApplicationContext());
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DbHelper.releaseHelper();
    }
}
