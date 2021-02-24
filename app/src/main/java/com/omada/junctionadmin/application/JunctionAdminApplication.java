package com.omada.junctionadmin.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import me.shouheng.utils.UtilsApp;

public class JunctionAdminApplication extends Application {

    private static JunctionAdminApplication instance;

    public static JunctionAdminApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {

        instance = this;
        Log.e("JunctionAdminApp", "Current branch : master");

        super.onCreate();
        UtilsApp.init(this);
    }
}
