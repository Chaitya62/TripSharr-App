package io.github.chaitya62.tripsharr.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Trip;

/**
 * Created by mikasa on 10/9/17.
 */

public class MultiSelectAdapter extends RecyclerView.Adapter<MultiSelectAdapter.MyViewHolder> {

    public ArrayList<Trip> tripsList=new ArrayList<>();
    public ArrayList<Trip> selected_tripsList=new ArrayList<>();
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView desc, name;
        public CardView ll_listitem;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tripname1);
            desc = (TextView) view.findViewById(R.id.description);
            ll_listitem=(CardView) view.findViewById(R.id.ll_listitem);

        }
    }


    public MultiSelectAdapter(Context context, ArrayList<Trip> userList, ArrayList<Trip> selectedList) {
        this.mContext=context;
        this.tripsList = userList;
        this.selected_tripsList = selectedList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ongoing_trips_list_view, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Trip trip = tripsList.get(position);
        holder.name.setText(trip.getName());
        holder.desc.setText(trip.getDescription());

        Log.v("hello","hello");

        if(selected_tripsList.contains(tripsList.get(position))) {
            Log.v("hello","sel");
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLightText));
        }
        else {
            Log.v("hello","desel");
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.pureWhite));
        }

    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    public void add(Trip trip) {
        Log.i("DEBUG",trip.getName());
        tripsList.add(tripsList.size(), trip);
        notifyItemInserted(tripsList.size()-1);
    }

    public void clear() {
        tripsList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Trip> list) {
        tripsList.addAll(list);
        notifyDataSetChanged();
    }

}

