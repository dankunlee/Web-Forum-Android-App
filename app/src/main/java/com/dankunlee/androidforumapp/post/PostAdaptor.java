package com.dankunlee.androidforumapp.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dankunlee.androidforumapp.R;

import java.util.ArrayList;

public class PostAdaptor extends ArrayAdapter<Post> {
    public PostAdaptor(@NonNull Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_post, parent, false);
        }

        Post currentPost = getItem(position);

        TextView title = listItemView.findViewById(R.id.post_title);
        title.setText("Title: " + currentPost.getTitle());

        TextView writer = listItemView.findViewById(R.id.post_createdby);
        writer.setText("Writer: " + currentPost.getCreatedBy());

        TextView createdDate = listItemView.findViewById(R.id.post_createddate);
        createdDate.setText("Date: " + currentPost.getCreatedDate());

        return listItemView;
    }
}
