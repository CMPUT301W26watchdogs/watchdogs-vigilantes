// ViewPager2 adapter for displaying featured event cards in a horizontally swipeable carousel on the home screen (Wildcard)

package com.example.vigilante;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// extending RecyclerView.Adapter because ViewPager2 uses a RecyclerView internally
public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private List<Event> events;
    private Context context;

    public CarouselAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NotNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        // inflating the carousel card layout for each page in the ViewPager2
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel_event, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull CarouselViewHolder holder, int position) {
        Event event = events.get(position);

        // setting the event title and description on the carousel card
        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());

        // showing the registration start date if available
        if (event.getRegistrationStart() != null && !event.getRegistrationStart().isEmpty()) {
            holder.date.setText(event.getRegistrationStart());
            holder.date.setVisibility(View.VISIBLE);
        } else {
            holder.date.setVisibility(View.GONE);
        }

        // showing the category tag if the event has one
        if (event.getCategory() != null && !event.getCategory().isEmpty()) {
            holder.categoryTag.setText(event.getCategory());
            holder.categoryTag.setVisibility(View.VISIBLE);
        } else {
            holder.categoryTag.setVisibility(View.GONE);
        }

        // loading the event poster image with Glide, falling back to a placeholder if missing
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(event.getPosterUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(holder.poster);
        } else {
            holder.poster.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // tapping the carousel card opens the event detail screen
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("event_id", event.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    // returning the list of events so tests can verify what the adapter is holding
    public List<Event> getEvents() {
        return events;
    }

    // updating the adapter data and refreshing the carousel
    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    // view holder caching references to the carousel card views
    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, description, date, categoryTag;

        CarouselViewHolder(@NotNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.carouselPoster);
            title = itemView.findViewById(R.id.carouselTitle);
            description = itemView.findViewById(R.id.carouselDescription);
            date = itemView.findViewById(R.id.carouselDate);
            categoryTag = itemView.findViewById(R.id.carouselCategoryTag);
        }
    }
}
