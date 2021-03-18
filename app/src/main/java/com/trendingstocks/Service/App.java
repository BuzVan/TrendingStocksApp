package com.trendingstocks.Service;

import android.app.Application;

import androidx.room.Room;

import com.trendingstocks.Service.Room.AppDatabase;

public class App extends Application {
    private static final String TAG = "App";

    private static App instance;
    private AppDatabase database;

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(
                this, AppDatabase.class, "database")
            .build();
        android.util.Log.i(TAG, "Preference Created.");


    }


}