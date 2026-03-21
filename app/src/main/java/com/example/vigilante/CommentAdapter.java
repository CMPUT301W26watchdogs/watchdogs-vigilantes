package com.example.vigilante;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;

    }
    @NotNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull CommentAdapter.CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.nameText.setText(comment.getName());
        holder.timeStampText.setText(comment.getTimeStampText());
        holder.commentText.setText(comment.getCommentText());

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, commentText, timeStampText;

        public CommentViewHolder(@NotNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.comment_user_name);
            timeStampText = itemView.findViewById(R.id.comment_timestamp);
            commentText = itemView.findViewById(R.id.comment_text);
        }
    }

}
