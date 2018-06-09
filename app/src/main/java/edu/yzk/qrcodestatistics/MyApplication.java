package edu.yzk.qrcodestatistics;

import android.app.Application;
import android.content.Context;

/**
 * Created by yangzikang on 2018/3/16.
 */

public class MyApplication extends Application {
    private static Context context;


    @Override
    public void onCreate(){
        super.onCreate();
        context = this.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
