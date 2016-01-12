package com.intetics.kkilyachkov.nestjingleapp.service;

import android.content.Context;
import android.util.Log;

import com.firebase.client.Config;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.Logger;
import com.firebase.client.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.Map;


public class NestAPI implements ValueEventListener, Firebase.AuthListener {
    private static final String NEST_FIREBASE_URL = "https://developer-api.nest.com";
    private static final String TOKEN = "c.1lsWWs4XaHR6e917KTmR9A9Gnhcu1e3WDB8aET0X2YlNjUHzqIFBeHk5KyOOOO2F5KvhSvRGFjgcBeaQsVEPoGuDluQBi52pTj85uK9B2OBqM3GyiJXX7TLGA30ynIbBp8pxIur67gPjK7Sv";
    private static final String SEARCH_TAG = "start_time";

    private static String lastValue = "EMPTY";

    private static NestAPI nestAPI;

    private UpdateListener updateListener;

    private Firebase firebaseRef;

    public static NestAPI getInstance(Context context) {
        if (nestAPI == null) {
            nestAPI = new NestAPI(context);
        }
        return nestAPI;
    }

    private NestAPI(Context context) {
        Firebase.goOffline();
        Firebase.goOnline();
        Firebase.setAndroidContext(context);
        Config defaultConfig = Firebase.getDefaultConfig();
        defaultConfig.setLogLevel(Logger.Level.DEBUG);
        firebaseRef = new Firebase(NEST_FIREBASE_URL);
    }

    public void authenticate() {
        firebaseRef.auth(TOKEN, this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Map<String, Object> values = dataSnapshot.getValue(MAP_INDICATOR);
        hashMapper(values);
    }

    @SuppressWarnings("Unchecked")
    private void hashMapper(Map<String, Object> lhm1) {
        for (Map.Entry<String, Object> entry : lhm1.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String || value instanceof Boolean) {
                if (key.equals(SEARCH_TAG) && isLatestUpdate((String) value)) {
                    updateListener.onUpdate();
                }
            } else if (value instanceof Map) {
                Map<String, Object> subMap = (Map<String, Object>) value;
                hashMapper(subMap);
            } else {
                return;
            }
        }
    }

    private static boolean isLatestUpdate(String value) {
        boolean afterNow = DateTime.parse(value).plus(Minutes.THREE).isAfterNow();
        if (afterNow && !value.equals(lastValue)) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        updateListener.onUpdateError(firebaseError.getMessage());
    }

    public void setUpdateListener(UpdateListener updateListener) {
        firebaseRef.addValueEventListener(this);
        this.updateListener = updateListener;
    }

    @Override
    public void onAuthError(FirebaseError firebaseError) {
        Log.e(this.getClass().getSimpleName(), firebaseError.getMessage());
    }

    @Override
    public void onAuthSuccess(Object o) {
        Log.e(this.getClass().getSimpleName(), "success auth");
    }

    @Override
    public void onAuthRevoked(FirebaseError firebaseError) {
        Log.e(this.getClass().getSimpleName(), "revoked auth");
    }

    public interface UpdateListener {
        void onUpdate();

        void onUpdateError(String message);
    }

    private static class StringIndicator extends GenericTypeIndicator<Map<String, Object>> {
    }

    private static final StringIndicator MAP_INDICATOR = new StringIndicator();

}
