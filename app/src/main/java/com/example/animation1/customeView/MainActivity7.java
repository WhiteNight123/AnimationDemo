package com.example.animation1.customeView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.animation1.R;

public class MainActivity7 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        DividingLineTextView te = findViewById(R.id.tv_divide);
        te.update("Hello RedRocker!");
    }
}