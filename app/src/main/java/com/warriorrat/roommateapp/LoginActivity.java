package com.warriorrat.roommateapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText login;
    private EditText password;
    private static final String TAG = LoginActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        loginButton = (Button) findViewById(R.id.login_button);
        login = (EditText) findViewById(R.id.login_text);
        password = (EditText) findViewById(R.id.pwd_text);
        loginButton.setText(R.string.log_in);
        setLoginListener();
    }

    private void setLoginListener()
    {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = login.getText().toString().trim();
                String pass = password.getText().toString().trim();
                if (username.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, R.string.username_and_pass_should_not_be_empty,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Util.auth().signInWithEmailAndPassword(username, pass)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed) + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    finish();
                                }
                                // ...
                            }
                        });
            }
        });
    }

}
