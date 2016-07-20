package cn.psvmc.zjblog.application;

import android.app.Application;
import android.content.Context;

import com.litesuits.orm.BuildConfig;
import com.litesuits.orm.LiteOrm;

/**
 * Created by PSVMC on 16/7/19.
 */
public class ZJApp extends Application {
    private static final String DB_NAME = "zjblog.db";
    public static Context sContext;
    public static LiteOrm liteOrm;


    @Override public void onCreate() {
        super.onCreate();
        sContext = this;
        liteOrm = LiteOrm.newSingleInstance(this, DB_NAME);
        if (BuildConfig.DEBUG) {
            liteOrm.setDebugged(true);
        }
    }


    @Override public void onTerminate() {
        super.onTerminate();
    }
}
