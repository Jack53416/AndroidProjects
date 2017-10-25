package com.example.jacek.handycalc;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mButtonSimpleCalc;
    private Button mButtonEnhancedCalc;
    private Button mAboutButton;
    private Button mExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSimpleCalc = (Button) findViewById(R.id.button_simpleC);
        mButtonEnhancedCalc = (Button) findViewById(R.id.button_enhancedC);
        mAboutButton = (Button) findViewById(R.id.button_about);
        mExitButton = (Button) findViewById(R.id.button_exit);

        mButtonEnhancedCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iEnhanced = Calculator_Activity.newIntent(MainActivity.this, true);
                startActivity(iEnhanced);
            }
        });

        mButtonSimpleCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSimple = Calculator_Activity.newIntent(MainActivity.this, false);
                startActivity(iSimple);
            }
        });

        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAbout = new Intent(MainActivity.this, About.class);
                startActivity(iAbout);
            }
        });

        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
