package com.example.vigilante;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Gemini, 2026-04-02, Organizers should be able to delete comments on their own events. Admins should be able to delete comments on any event.
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private boolean canDelete;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Comment comment);
    }

    /**
     * creating the comment adapter with a list of comments, a flag controlling
     * whether the delete button shows and a callback for handling deletions
     *
     * @param commentList the list of comments to display
     * @param canDelete whether the delete button should be visible
     * @param deleteClickListener callback triggered when a delete button is tapped
     */
    public CommentAdapter(List<Comment> commentList, boolean canDelete, OnDeleteClickListener deleteClickListener) {
        this.commentList = commentList;
        this.canDelete = canDelete;
        this.deleteClickListener = deleteClickListener;
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

        // loading the commenter's profile picture from Firestore
        holder.avatarImage.setImageResource(R.drawable.ic_launcher_background);
        String userId = comment.getUserId();
        if (userId != null && !userId.isEmpty()) {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        String picUrl = doc.getString("profilePicUrl");
                        if (picUrl != null && !picUrl.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(picUrl)
                                    .transform(new CircleCrop())
                                    .into(holder.avatarImage);
                        }
                    });
        }

        if (canDelete) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(comment);
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    /**
     * toggling whether the delete button is visible on comment cards
     * and refreshing the entire list to apply the change
     *
     * @param canDelete true to show delete buttons, false to hide them
     */
    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
        notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, commentText, timeStampText;
        ImageButton deleteButton;
        ImageView avatarImage;

        public CommentViewHolder(@NotNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.comment_user_name);
            timeStampText = itemView.findViewById(R.id.comment_timestamp);
            commentText = itemView.findViewById(R.id.comment_text);
            deleteButton = itemView.findViewById(R.id.delete_comment_button);
            avatarImage = itemView.findViewById(R.id.comment_user_avatar);
        }
    }
}
