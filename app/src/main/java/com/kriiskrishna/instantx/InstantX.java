package com.kriiskrishna.instantx;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class InstantX extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
