package com.dankunlee.androidforumapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dankunlee.androidforumapp.post.Post;
import com.dankunlee.androidforumapp.post.PostAdaptor;
import com.dankunlee.androidforumapp.request.HttpRequest;
import com.dankunlee.androidforumapp.request.PostRequest;
import com.dankunlee.androidforumapp.request.UserRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ForumActivity extends AppCompatActivity {

    Button signOut_Button, writePost_Button;
    SwipeRefreshLayout refreshLayout;
    ListView pageView;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        // initializes layout variables
        signOut_Button = findViewById(R.id.signout);
        writePost_Button = findViewById(R.id.write_post);
        refreshLayout = findViewById(R.id.refresh);
        pageView = findViewById(R.id.pageview);

        // updates the forum when refreshed
        refreshLayout.setOnRefreshListener(new RefreshListener());

        // signs out from the application
        signOut_Button.setOnClickListener(new SignOutListener());

        // writes a new post
        writePost_Button.setOnClickListener(new WritePostListener());

        // new AsyncGetPage(0,3).execute(); // gets a page
        new AsyncGetAllPosts().execute(); // gets all posts
    }

    // updates the list view for displaying all posts
    private void updatePageView(String page, boolean isInputPageFormat) {
        List<Post> posts = stringToPage(page, isInputPageFormat); // converts a page to a list of posts
        PostAdaptor postAdaptor = new PostAdaptor(this, (ArrayList) posts);
        pageView.setAdapter(postAdaptor); // displays all the posts to list view
        pageView.setOnItemClickListener(new PostClickListener()); // starts new Post activity upon clicking
        pageView.setOnItemLongClickListener(new PostLongClickListener()); // pops up a menu for edit/deletion of the post
    }

    // converts json response in string format to a list of Posts
    private List<Post> stringToPage(String page, boolean isInputPageFormat)  {
        List<Post> posts = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();

            List<Map<String, String>> postList;
            if (isInputPageFormat) { // for getting a page of posts
                JSONObject jsonObject = (JSONObject) parser.parse(page);
                postList = (List<Map<String, String>>) jsonObject.get("content");
            }
            else { // for getting all posts
                JSONArray jsonArray = (JSONArray) parser.parse(page);
                postList = (List<Map<String, String>>) jsonArray;
            }

            for (Map<String, String> post : postList) {
                String id = String.valueOf(post.get("id"));
                posts.add(new Post(Integer.parseInt(id), post.get("title"), post.get("createdBy"), post.get("createdDate")));
            }
        }
        catch (Exception e) {
            Log.e("stringToPage Method", e.toString());
        }
        return posts;
    }

    // gets all the posts
    private class AsyncGetAllPosts extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            HttpRequest getAllPostsRequest = new PostRequest.GetAllPosts(MainActivity.host);
            ServerConnection serverConnection = new ServerConnection(getAllPostsRequest, ForumActivity.this);
            return serverConnection.makeRequest();
        }

        @Override
        protected void onPostExecute(String allPosts) {
            super.onPostExecute(allPosts);
            updatePageView(allPosts, false);
        }
    }

    // reads posts in a page and update the list view to display the posts
    private class AsyncGetPage extends AsyncTask<Void, Void, String> {
        private int page, pageSize;

        public AsyncGetPage(int page, int pageSize) {
            this.page = page;
            this.pageSize = pageSize;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpRequest getPageRequest = new PostRequest.GetPage(MainActivity.host, page, pageSize);
            ServerConnection serverConnection = new ServerConnection(getPageRequest, ForumActivity.this);
            return serverConnection.makeRequest();
        }

        @Override
        protected void onPostExecute(String page) {
            super.onPostExecute(page);
            updatePageView(page, true);
        }
    }

    private class SignOutListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new AsyncSignOut().execute();
        }

        // Asynchronous task for signing out
        class AsyncSignOut extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                HttpRequest logOutRequest = new UserRequest.LogOut(MainActivity.host);
                ServerConnection serverConnection = new ServerConnection(logOutRequest, ForumActivity.this);
                return serverConnection.makeRequest();
            }

            @Override
            protected void onPostExecute(String returnedBody) {
                super.onPostExecute(returnedBody);
                // removes the sessionID to null when signing out
                SharedPreferences preferences = getSharedPreferences("sessionCookie", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("sessionID", null);
                editor.putString("sessionOwner", null);
                editor.commit();

                Toast.makeText(ForumActivity.this, returnedBody, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ForumActivity.this, MainActivity.class));
            }
        }
    }

    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            new AsyncGetAllPosts().execute(); // gets all posts
            Toast.makeText(ForumActivity.this, "refreshed", Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
        }
    }

    private class PostClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Post currentPost = (Post) parent.getItemAtPosition(position);
            int postID = currentPost.getID();
            Intent intent = new Intent(ForumActivity.this, ViewPostActivity.class);
            intent.putExtra("postID", Integer.toString(postID));
            startActivity(intent);
        }
    }

    private class WritePostListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(ForumActivity.this, WritePostActivity.class));
        }
    }

    // lets the writer of the post delete the post
    private class PostLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Post currentPost = (Post) parent.getItemAtPosition(position);
            int postID = currentPost.getID();
            showMenu(view, postID);
            return false;
        }
    }

    private void showMenu(View view, final int postID) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        // TODO: implement editing
                        return true;
                    case R.id.delete:
                        new AsyncDeletePost(Integer.toString(postID)).execute();
                        return true;
                }
                return false;
            }
        });

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popupmenu, popupMenu.getMenu());
        popupMenu.show();
    }

    // calls API for deleting a selected post
    private class AsyncDeletePost extends AsyncTask<Void, Void, String> {
        String postID;

        public AsyncDeletePost(String postID) {
            this.postID = postID;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpRequest deleteCommentRequest = new PostRequest.DeletePost(MainActivity.host, postID);
            ServerConnection serverConnection = new ServerConnection(deleteCommentRequest, ForumActivity.this);
            return serverConnection.makeRequest();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Toast.makeText(ForumActivity.this, response, Toast.LENGTH_SHORT).show();
            new AsyncGetAllPosts().execute(); // refreshes the comments section
        }
    }
}