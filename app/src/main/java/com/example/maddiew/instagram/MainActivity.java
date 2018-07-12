package com.example.maddiew.instagram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.maddiew.instagram.NavigationActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private Button signupBtn;
    public static final String KEY_HANDLE = "handle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        ParseUser currentUser = ParseUser.getCurrentUser();

        usernameInput = findViewById(R.id.etCaption);
        passwordInput = findViewById(R.id.etPassword);
        loginBtn = findViewById(R.id.btLogin);
        signupBtn = findViewById(R.id.btnSignup);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                login(username, password);

            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                signup(username, password);

            }
        });

        if (currentUser != null) {
            // user is logged in!
            final Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();
        } // otherwise show the signup or login screen

    }
    private void signup(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.put(KEY_HANDLE, username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Log.i("SignupActivity", "Signup success");
                    final Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("SignupActivity", "Login fail");
                    e.printStackTrace();
                }
            }
        });

    }
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login success");
                    final Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginActivity", "Login fail");
                    e.printStackTrace();
                }
            }
        });
    }

}
