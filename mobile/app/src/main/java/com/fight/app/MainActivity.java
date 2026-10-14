package com.fight.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Entry point of the FIGHT fighter app.
 * Business flows (auth, discover, chat, bout) will be added under {@code ui/}.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
