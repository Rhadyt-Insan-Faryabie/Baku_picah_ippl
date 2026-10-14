package com.fight.app.ui.bout;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.R;

/**
 * Bout status tracker: Pending Opponent → ... → Completed.
 */
public class BoutTrackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bout_tracker);
    }
}
