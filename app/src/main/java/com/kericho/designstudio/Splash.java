package com.kericho.designstudio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class Splash extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

       findViewById(R.id.clickk).setOnClickListener(view -> {
            startActivity(new Intent(Splash.this,MainActivity.class));
            finish();
        });


    }
}