package com.intetics.kkilyachkov.nestjingleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.intetics.kkilyachkov.nestjingleapp.service.ObserverService;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class JingleActivity extends AppCompatActivity {

    private static final int MILLISECONDS = 7000;
    private Button btnExit;
    private TextView vContent;
    private boolean isJingleMode;
    private Vibrator vb;
    private GifDrawable gifDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_jingle_page);
        vContent = (TextView) findViewById(R.id.fullscreen_content);
        btnExit = (Button) findViewById(R.id.exit_button);
        isJingleMode = getIntent().getBooleanExtra(ObserverService.SOUND_INTENT, false);
        final GifImageView gifImageView = (GifImageView) findViewById(R.id.gif_image_view);
        gifDrawable = (GifDrawable) gifImageView.getDrawable();
        toggleAll();
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isJingleMode) {
                    isJingleMode = false;
                    toggleAll();
                } else {
                    finish();
                }
            }
        });
        startService(new Intent(this, ObserverService.class));
    }

    private void toggleAll() {
        switchText();
        jingle();
        toggleAnimation();
    }

    private void switchText() {
        if (isJingleMode) {
            btnExit.setText(R.string.btn_action);
            vContent.setText(R.string.text_action);
        } else {
            btnExit.setText(R.string.btn_wait);
            vContent.setText(R.string.text_wait);
        }
    }

    private void jingle() {
        if (isJingleMode) {
            vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(MILLISECONDS);
            new FlashlightThread().start();
        } else if (vb != null) {
            vb.cancel();
        }
    }

    private void toggleAnimation() {
        if (isJingleMode) {
            gifDrawable.start();
        } else {
            gifDrawable.stop();
            gifDrawable.setLoopCount(4);
            gifDrawable.seekToFrameAndGet(5);
        }
    }
}
