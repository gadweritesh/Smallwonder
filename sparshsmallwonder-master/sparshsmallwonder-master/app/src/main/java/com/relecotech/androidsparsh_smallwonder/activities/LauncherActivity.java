package com.relecotech.androidsparsh_smallwonder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.relecotech.androidsparsh_smallwonder.MainActivity;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.utils.SessionManager;

/**
 * Created by Relecotech on 02-01-2018.
 */

public class LauncherActivity extends AppCompatActivity {
    Intent i;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        sessionManager = new SessionManager(getApplicationContext());

        //StartAnimations();

        new Handler().postDelayed(new Runnable() {
            // Using handler with postDelayed called runnable run method
            @Override
            public void run() {
                System.out.println("sessionManager.isLoggedIn()" + sessionManager.isLoggedIn());
                if (sessionManager.isLoggedIn()) {


                    i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("Name","Launch");
                    startActivity(i);
                } else {


                    i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.putExtra("Name","Launch");
                    startActivity(i);
                }
                finish();
            }
        }, 5000); // wait for n seconds
    }

    private void StartAnimations() {

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splashImageView);
        iv.clearAnimation();
        iv.startAnimation(anim);
    }
}