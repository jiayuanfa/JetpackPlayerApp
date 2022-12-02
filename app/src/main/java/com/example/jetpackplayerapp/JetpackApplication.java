package com.example.jetpackplayerapp;

import android.app.Application;

import com.example.libnetwork.ApiService;

public class JetpackApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ApiService.init("http://123.56.232.18:8080/serverdemo", null);
    }
}
