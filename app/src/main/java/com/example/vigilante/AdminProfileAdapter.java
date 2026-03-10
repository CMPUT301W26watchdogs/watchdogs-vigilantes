package com.example.vigilante;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
public class AdminProfileAdapter extends RecyclerView.Adapter<AdminProfileAdapter.ViewHolder> {
    private ArrayList<UserProfile> userList;
    public AdminProfileAdapter(ArrayList<UserProfile> userList) {
        this.userList = userList;}
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        TextView phone;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.profileName);
            email = itemView.findViewById(R.id.profileEmail);
            phone = itemView.findViewById(R.id.profilePhone);
        }}
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_profile, parent, false);
        return new ViewHolder(view);}
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserProfile user = userList.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.phone.setText(user.getPhone());}
    @Override
    public int getItemCount() {
        return userList.size();}
}