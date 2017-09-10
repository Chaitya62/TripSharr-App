package io.github.chaitya62.tripsharr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Trip;

/**
 * Created by chaitya62 on 2/9/17.
 */

public class ProfileFeedAdapter extends RecyclerView.Adapter<ProfileFeedAdapter.ViewHolder> {

    private List<Trip> trips;
    private Context mcontext;

    public ProfileFeedAdapter(Context context, List<Trip> trips){
        this.mcontext = context;
        this.trips = trips;
    }


    public void add(Trip trip){
        trips.add(trips.size(), trip);
        notifyItemInserted(trips.size()-1);
    }

    public void clear() {
        trips.clear();
        notifyDataSetChanged();
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.trip_feed_card, parent, false);
        return new ProfileFeedAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.cTitle.setText(trip.getName());
        holder.cName.setText(trip.getUserName());
        holder.cDesc.setText(trip.getDescription());
        holder.c_no_of_forks.setText(trip.getNoOfForks() + "");
        holder.c_no_of_stars.setText(trip.getNoOfStars() + "");

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static  class  ViewHolder extends RecyclerView.ViewHolder{
        protected TextView cTitle, cName, cDesc, c_no_of_stars, c_no_of_forks;

        public ViewHolder(View view){
            super(view);
            cTitle = (TextView) view.findViewById(R.id.card_title);
            cName = (TextView) view.findViewById(R.id.card_name);
            cDesc = (TextView) view.findViewById(R.id.card_description);
            c_no_of_forks = (TextView) view.findViewById(R.id.no_of_forks);
            c_no_of_stars = (TextView) view.findViewById(R.id.no_of_stars);
        }


    }
}
