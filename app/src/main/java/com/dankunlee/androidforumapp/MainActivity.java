package com.dankunlee.androidforumapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dankunlee.androidforumapp.request.HttpRequest;
import com.dankunlee.androidforumapp.request.UserRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    static String host = "http://10.0.2.2:8080"; // local host: "http://192.168.0.52:8080"

    private EditText userName_EditText, password_EditText, serverAddress_EditText;
    private Button signIn_Button, signUp_Button, serverAddressConnect_Button;

    @Override
    protected void onStop() {
        // saves the host server address in Edit Text form before stopping the activity
        super.onStop();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("savedHost", serverAddress_EditText.getText().toString());
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initializes layout variables
        userName_EditText = findViewById(R.id.username);
        password_EditText = findViewById(R.id.password);
        serverAddress_EditText = findViewById(R.id.server_address);
        signIn_Button = findViewById(R.id.signin);
        signUp_Button = findViewById(R.id.signup);
        serverAddressConnect_Button = findViewById(R.id.server_address_connect);

        setHostAddress(); // reads saved host address from previous activity cycle

        skipLogInPage(); // remains logged in if the session is still alive

        // enables the Sign In button only when username and password are given
        userName_EditText.addTextChangedListener(new FormWatcher());
        password_EditText.addTextChangedListener(new FormWatcher());

        // checks for a connection to the server
        serverAddressConnect_Button.setOnClickListener(new CheckConnectionListener());

        // tries to log in to the application hosted by the server
        signIn_Button.setOnClickListener(new SignInListener());

        signUp_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });
    }

    private void saveAccountInfo(String accountOwner) {
        SharedPreferences preferences = getSharedPreferences("sessionCookie", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sessionOwner", accountOwner);
        editor.commit();
    }

    // defines the host address
    private void setHostAddress() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String savedHost = preferences.getString("savedHost", null);
        if (savedHost != null) {
            host = savedHost;
            serverAddress_EditText.setText(savedHost);
        }
        else serverAddress_EditText.setText(host);
    }

    // when already logged in (session id exists), skip the log in page
    private void skipLogInPage() {
        SharedPreferences preferences = getSharedPreferences("sessionCookie", MODE_PRIVATE);
        String sessionID = preferences.getString("sessionID", null);
        if (sessionID != null) {
            Log.i("Skipping log in with", sessionID);
            startActivity(new Intent(MainActivity.this, ForumActivity.class));
        }
    }

    private class SignInListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Utility.hideKeyboard(MainActivity.this, v);
            String userName = userName_EditText.getText().toString();
            String password = password_EditText.getText().toString();
            JSONObject logInParams = new JSONObject();
            try {
                logInParams.put("username", userName);
                logInParams.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpRequest logInRequest = new UserRequest.LogIn(host, logInParams.toString());
            new AsyncSignIn().execute(logInRequest);
        }

        // Asynchronous task for attempting to sign in to the back end hosted from Spring Boot
        class AsyncSignIn extends AsyncTask<HttpRequest, Void, String> {
            @Override
            protected String doInBackground(HttpRequest... requests) {
                ServerConnection serverConnection = new ServerConnection(requests[0], MainActivity.this);
                serverConnection.setLogInConnection();
                return serverConnection.makeRequest();
            }

            @Override
            protected void onPostExecute(String returnedBody) {
                super.onPostExecute(returnedBody);
                Toast.makeText(MainActivity.this, returnedBody, Toast.LENGTH_SHORT).show();
                if (returnedBody.equals("Logged In")) {
                    saveAccountInfo(userName_EditText.getText().toString());
                    startActivity(new Intent(MainActivity.this, ForumActivity.class));
                }
            }
        }
    }

    private class CheckConnectionListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Utility.hideKeyboard(MainActivity.this, v);
            host = serverAddress_EditText.getText().toString();
            new AsyncCheckConnection().execute();
        }

        // Async task for quickly checking connection to the server
        class AsyncCheckConnection extends AsyncTask<Void, Void, Boolean> {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return ServerConnection.checkConnection();
            }

            @Override
            protected void onPostExecute(Boolean alive) {
                super.onPostExecute(alive);
                if (alive)
                    Toast.makeText(MainActivity.this, "New Server: " + host, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Server is dead", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // watcher that will enable the sign in button only when all the forms are filled
    private class FormWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!userName_EditText.getText().toString().equals("") &&
                    !password_EditText.getText().toString().equals(""))
                signIn_Button.setEnabled(true);
            else signIn_Button.setEnabled(false);
        }
    }
}