package com.haiyunshan.pudding;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.haiyunshan.pudding.font.dataset.FontManager;

public class App extends Application {

    private static App sInstance;

    int mActivityCount;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        this.mActivityCount = 0;
        this.registerActivityLifecycleCallbacks(mCallback);
    }

    public static final App getInstance() {
        return sInstance;
    }

    ActivityLifecycleCallbacks mCallback = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            ++mActivityCount;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            --mActivityCount;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (mActivityCount == 0) {
                FontManager.recycle();
            }
        }
    };
}
