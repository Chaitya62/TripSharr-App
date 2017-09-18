package io.github.chaitya62.tripsharr.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

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
        public LinearLayout ll_listitem;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tripname1);
            desc = (TextView) view.findViewById(R.id.description);
            ll_listitem=(LinearLayout)view.findViewById(R.id.ll_listitem);

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


        if(selected_tripsList.contains(tripsList.get(position)))
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_selected_state));
        else
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_normal_state));

    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }
}

