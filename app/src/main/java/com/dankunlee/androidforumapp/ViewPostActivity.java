package com.dankunlee.androidforumapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dankunlee.androidforumapp.comment.Comment;
import com.dankunlee.androidforumapp.comment.CommentAdaptor;
import com.dankunlee.androidforumapp.post.Post;
import com.dankunlee.androidforumapp.request.CommentRequest;
import com.dankunlee.androidforumapp.request.HttpRequest;
import com.dankunlee.androidforumapp.request.PostRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewPostActivity extends AppCompatActivity {

    TextView title, writer, date, content;
    EditText writeComment_EditText;
    Button comment_Button;
    ListView allComments_LisView;
    String postID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpost);

        // initializes the layout variables
        title = findViewById(R.id.singlepost_title);
        writer = findViewById(R.id.singlepost_writer);
        date = findViewById(R.id.singlepost_date);
        content = findViewById(R.id.singlepost_content);
        writeComment_EditText = findViewById(R.id.singlepost_write_comment);
        comment_Button = findViewById(R.id.singlepost_write_comment_button);
        allComments_LisView = findViewById(R.id.singlepost_comments);

        // retrieves a post of specified ID and its comments
        postID = getIntent().getStringExtra("postID");
        new AsyncGetPost(postID).execute();
        new AsyncGetAllComments(postID).execute();

        // writes a comment
        comment_Button.setOnClickListener(new WriteCommentListener());
    }

    // converts the response of post "Get" to a Post object
    private Post parsePost(String postResponse) {
        Post parsedPost = null;
        try {
            JSONParser parser = new JSONParser();
            Map<String, String> postMap = (Map<String, String>) parser.parse(postResponse);
            String id = String.valueOf(postMap.get("id"));
            parsedPost = new Post(Integer.parseInt(id), postMap.get("title"), postMap.get("createdBy"),
                    postMap.get("createdDate"), postMap.get("lastModifiedDate"), postMap.get("content"));
        }
        catch (Exception e) {
            Log.e("parsePost Method", e.toString());
        }
        return parsedPost;
    }

    // fills the post layout (title, writer, date and content)
    private void fillPostLayout(String postResponse) {
        Post post = parsePost(postResponse);
        title.setText(post.getTitle());
        writer.setText(post.getCreatedBy());
        date.setText(post.getCreatedDate());
        content.setText(post.getContent());
    }

    // converts the response of comments "Get" to a list of Comment objects
    private List<Comment> parseComments(String commentResponse) {
        List<Comment> comments = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(commentResponse);
            List<Map<String, String>> commentList = (List<Map<String, String>>) jsonArray;

            for (Map<String, String> comment : commentList) {
                String id = String.valueOf(comment.get("id")); // comment id
                comments.add(new Comment(Integer.parseInt(id), comment.get("content"), comment.get("createdBy"), comment.get("createdDate")));
            }
        }
        catch (Exception e) {
            Log.e("parseComment Method", e.toString());
        }
        return comments;
    }

    // fills the comments layout (all the comments with content, commenter and date)
    private void fillCommentsLayout(String commentResponse) {
        List<Comment> comments = parseComments(commentResponse);
        CommentAdaptor commentAdaptor = new CommentAdaptor(ViewPostActivity.this, (ArrayList<Comment>) comments);
        allComments_LisView.setAdapter(commentAdaptor); // displays all comments
        allComments_LisView.setOnItemLongClickListener(new CommentLongClickListener());
    }

    // calls API that retrieves a post with specified ID from the server
    private class AsyncGetPost extends AsyncTask<Void, Void, String> {
        String postID;

        public AsyncGetPost(String postID) {
            this.postID = postID;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpRequest getPostsRequest = new PostRequest.GetPost(MainActivity.host, postID);
            ServerConnection serverConnection = new ServerConnection(getPostsRequest, ViewPostActivity.this);
            return serverConnection.makeRequest();
        }

        @Override
        protected void onPostExecute(String postResponse) {
            super.onPostExecute(postResponse);
            fillPostLayout(postResponse);
        }
    }

    // calls API that retrieves all comments associated with the post
    private class AsyncGetAllComments extends AsyncTask<Void, Void, String> {
        String postID;

        public AsyncGetAllComments(String postID) {
            this.postID = postID;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpRequest getAllCommentsRequest = new CommentRequest.GetAllComments(MainActivity.host, postID);
            ServerConnection serverConnection = new ServerConnection(getAllCommentsRequest, ViewPostActivity.this);
            return serverConnection.makeRequest();
        }

        @Override
        protected void onPostExecute(String allCommentsResponse) {
            super.onPostExecute(allCommentsResponse);
            fillCommentsLayout(allCommentsResponse);
        }
    }

    // writes a comment and updates the comments list view upon "Comment" button click
    private class WriteCommentListener implements View.OnClickListener {
        String writer;

        public WriteCommentListener() {
            SharedPreferences preferences = getSharedPreferences("sessionCookie", MODE_PRIVATE);
            writer = preferences.getString("sessionOwner", null);
        }

        @Override
        public void onClick(View v) {
            String comment = writeComment_EditText.getText().toString();
            Utility.hideKeyboard(ViewPostActivity.this, v);
            new AsyncWriteComment(postID, writer, comment).execute();
            writeComment_EditText.setText("");
        }

        // sends a request to the server to write a comment
        private class AsyncWriteComment extends AsyncTask<Void, Void, Void> {
            String postID, commentInput;

            public AsyncWriteComment(String postID, String writer, String comment) {
                this.postID = postID;
                JSONObject commentParams = new JSONObject();
                commentParams.put("writer", writer);
                commentParams.put("content", comment);
                commentInput = commentParams.toString();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                HttpRequest writeCommentRequest = new CommentRequest.WriteComment(MainActivity.host, postID, commentInput);
                ServerConnection serverConnection = new ServerConnection(writeCommentRequest, ViewPostActivity.this);
                serverConnection.makeRequest();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                new AsyncGetAllComments(postID).execute(); // refreshes the comments section
            }
        }
    }

    // lets the writer of the comment delete the comment
    private class CommentLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Comment currentComment = (Comment) parent.getItemAtPosition(position);
            int commentID = currentComment.getId();
            showMenu(view, commentID);
            return false;
        }
    }

    private void showMenu(View view, final int commentID) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        // TODO: implement editing
                        return true;
                    case R.id.delete:
                        new AsyncDeleteComment(Integer.toString(commentID)).execute();
                        return true;
                }
                return false;
            }
        });

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popupmenu, popupMenu.getMenu());
        popupMenu.show();
    }

    // calls API for calling "delete a specific comment"
    private class AsyncDeleteComment extends AsyncTask<Void, Void, String> {
        String commentID;

        public AsyncDeleteComment(String commentID) {
            this.commentID = commentID;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpRequest deleteCommentRequest = new CommentRequest.DeleteComment(MainActivity.host, postID, commentID);
            ServerConnection serverConnection = new ServerConnection(deleteCommentRequest, ViewPostActivity.this);
            return serverConnection.makeRequest();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Toast.makeText(ViewPostActivity.this, response, Toast.LENGTH_SHORT).show();
            new AsyncGetAllComments(postID).execute(); // refreshes the comments section
        }
    }
}
