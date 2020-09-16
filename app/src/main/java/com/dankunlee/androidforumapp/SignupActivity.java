package com.dankunlee.androidforumapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dankunlee.androidforumapp.request.HttpRequest;
import com.dankunlee.androidforumapp.request.UserRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private EditText userName_EditText, email_EditText, password_EditText;
    private Button signUp_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // initializes layout variables
        userName_EditText = findViewById(R.id.register_username);
        email_EditText = findViewById(R.id.register_email);
        password_EditText = findViewById(R.id.register_password);
        signUp_Button = findViewById(R.id.register);

        // enables the Sign In button only when username, email and password are given
        userName_EditText.addTextChangedListener(new FormWatcher());
        email_EditText.addTextChangedListener(new FormWatcher());
        password_EditText.addTextChangedListener(new FormWatcher());

        // sign up feature
        signUp_Button.setOnClickListener(new SignUpListener());
    }

    private class SignUpListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String userName = userName_EditText.getText().toString();
            String email = email_EditText.getText().toString();
            String password = password_EditText.getText().toString();

            JSONObject logInParams = new JSONObject();
            try {
                logInParams.put("username", userName);
                logInParams.put("email", email);
                logInParams.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpRequest logInRequest = new UserRequest.Register(MainActivity.host, logInParams.toString());
            new AsyncSignUp().execute(logInRequest);
        }

        // Asynchronous task for registering a user to the back end server
        public class AsyncSignUp extends AsyncTask<HttpRequest, Void, String> {
            @Override
            protected String doInBackground(HttpRequest... requests) {
                ServerConnection serverConnection = new ServerConnection(requests[0], SignupActivity.this);
                return serverConnection.makeRequest();
            }

            @Override
            protected void onPostExecute(String returnedBody) {
                super.onPostExecute(returnedBody);
                Toast.makeText(SignupActivity.this, returnedBody, Toast.LENGTH_SHORT).show();
                if (returnedBody.equals("Success"))
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
            }
        }
    }

    // watcher that will enable the sign up button only when all the forms are filled
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
                    !email_EditText.getText().toString().equals("") &&
                    !password_EditText.getText().toString().equals("") )
                signUp_Button.setEnabled(true);
            else signUp_Button.setEnabled(false);
        }
    }
}
