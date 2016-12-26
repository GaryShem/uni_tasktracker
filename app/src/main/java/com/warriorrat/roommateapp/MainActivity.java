package com.warriorrat.roommateapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button choresBtn;
    private Button billsBtn;
    private Button registerBtn;
    private Button loginBtn;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choresBtn = (Button) findViewById(R.id.chores_btn);
        billsBtn = (Button) findViewById(R.id.bills_btn);
        registerBtn = (Button) findViewById(R.id.register_btn);
        loginBtn = (Button) findViewById(R.id.login_btn);
        logoutBtn = (Button) findViewById(R.id.logout_btn);

        Util.database();
        Util.auth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.isLoggedIn()) {
            choresBtn.setVisibility(View.VISIBLE);
            billsBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.GONE);
            loginBtn.setVisibility(View.GONE);
        } else {
            choresBtn.setVisibility(View.GONE);
            billsBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    public void loadChoreList(View view) {
        Intent intent = new Intent(MainActivity.this, ChoresActivity.class);
        startActivity(intent);
    }

    public void loadBillList(View view) {
        Intent intent = new Intent(MainActivity.this, BillsActivity.class);
        startActivity(intent);
    }

    public void registerClick(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginClick(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void logOut(View view) {
        Util.logOff();
        MainActivity.this.recreate();
    }
}
