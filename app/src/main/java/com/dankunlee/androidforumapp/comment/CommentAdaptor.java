package com.dankunlee.androidforumapp.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dankunlee.androidforumapp.R;
import com.dankunlee.androidforumapp.post.Post;

import java.util.ArrayList;

public class CommentAdaptor extends ArrayAdapter<Comment> {
    public CommentAdaptor(@NonNull Context context, ArrayList<Comment> comments) {
        super(context, 0, comments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_comment, parent, false);
        }

        Comment currentComment = getItem(position);

        TextView content = listItemView.findViewById(R.id.comment_content);
        content.setText(currentComment.getContent());

        TextView writer = listItemView.findViewById(R.id.comment_createdby);
        writer.setText("By: " + currentComment.getCreatedBy());

        TextView createdDate = listItemView.findViewById(R.id.comment_createddate);
        createdDate.setText("On: " + currentComment.getCreatedDate());

        return listItemView;
    }
}
