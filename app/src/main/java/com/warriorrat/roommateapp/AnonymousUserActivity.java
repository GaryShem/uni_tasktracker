package com.warriorrat.roommateapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AnonymousUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_anonymous_user);
        if (Util.isLoggedIn()) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.isLoggedIn()) {
            finish();
        }
    }

    public void registerClick(View v){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginClick(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
