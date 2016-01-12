package com.intetics.kkilyachkov.nestjingleapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.intetics.kkilyachkov.nestjingleapp.JingleActivity;

public class ObserverService extends IntentService implements NestAPI.UpdateListener {

    public static final String SOUND_INTENT = "SOUNDVIBRO";

    public ObserverService() {
        super(ObserverService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NestAPI nestAPI = NestAPI.getInstance(getApplicationContext());
        nestAPI.setUpdateListener(this);
        nestAPI.authenticate();
    }

    @Override
    public void onUpdate() {
        startJingle();
    }

    @Override
    public void onUpdateError(String message) {
        Log.e(ObserverService.class.getSimpleName(), message);
    }

    private void startJingle() {
        Intent i = new Intent();
        i.setClass(this, JingleActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(SOUND_INTENT,true);
        startActivity(i);
    }
}
