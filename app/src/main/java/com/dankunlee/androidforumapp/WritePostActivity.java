package com.dankunlee.androidforumapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dankunlee.androidforumapp.request.HttpRequest;
import com.dankunlee.androidforumapp.request.PostRequest;

import org.json.simple.JSONObject;

public class WritePostActivity extends AppCompatActivity {

    EditText title_EditText, content_EditText;
    Button writePost_Button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writepost);

        // initializes layout variables
        title_EditText = findViewById(R.id.writepost_title);
        content_EditText = findViewById(R.id.writepost_content);
        writePost_Button = findViewById(R.id.writepost_post);

        // enables the Post button only when title and content are given
        title_EditText.addTextChangedListener(new FormWatcher());
        content_EditText.addTextChangedListener(new FormWatcher());

        // writes a new post
        writePost_Button.setOnClickListener(new WritePostListener());
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
            if (!title_EditText.getText().toString().equals("") &&
                    !content_EditText.getText().toString().equals(""))
                writePost_Button.setEnabled(true);
            else writePost_Button.setEnabled(false);
        }
    }

    private class WritePostListener implements View.OnClickListener {
        String postInput;
        @Override
        public void onClick(View v) {
            JSONObject postParams = new JSONObject();
            postParams.put("title", title_EditText.getText().toString());
            postParams.put("content", content_EditText.getText().toString());
            postInput = postParams.toString();
            new AsyncWritePost().execute();
        }

        // Async Task for calling API call for writing a new post
        private class AsyncWritePost extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                HttpRequest writePostRequest = new PostRequest.WritePost(MainActivity.host, postInput);
                ServerConnection serverConnection = new ServerConnection(writePostRequest, WritePostActivity.this);
                serverConnection.makeRequest();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(WritePostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
