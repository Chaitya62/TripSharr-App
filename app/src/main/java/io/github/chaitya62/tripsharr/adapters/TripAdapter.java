package io.github.chaitya62.tripsharr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Trip;

/**
 * Created by mikasa on 31/8/17.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {

    private List<Trip> tripsList;
    String mTitle,mDuration;
    private Context context;

    public TripAdapter(Context context,List<Trip> tripsList){
        this.context=context;
        this.tripsList=tripsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title,description;

        public MyViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.tripname1);
            description = (TextView) view.findViewById(R.id.description);

        }
    }

    /*public FeedAdapter(List<Trip> tripsList) {
        this.tripsList = tripsList;
    }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ongoing_trips_list_view,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Trip trip = tripsList.get(position);
        holder.title.setText(trip.getName());
        holder.description.setText(trip.getDescription());
    }

    @Override
    public int getItemCount(){
        return tripsList.size();
    }

    public void add(Trip trip) {
        Log.i("DEBUG",trip.getName());
        tripsList.add(tripsList.size(), trip);
        notifyItemInserted(tripsList.size()-1);
    }

}
