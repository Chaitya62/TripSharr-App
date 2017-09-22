package io.github.chaitya62.tripsharr.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Coordinates;

/**
 * Created by mikasa on 22/9/17.
 */

public class MultiSelectCoordinateAdapter extends RecyclerView.Adapter<MultiSelectCoordinateAdapter.MyViewHolder>{
    public ArrayList<Coordinates> coordinatesArrayList=new ArrayList<>();
    public ArrayList<Coordinates> selected_coordinateslist=new ArrayList<>();
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView desc, name;
        public RelativeLayout ll_listitem;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.chkpoint);
            desc = (TextView) view.findViewById(R.id.datetime);
            ll_listitem=(RelativeLayout) view.findViewById(R.id.ll_chklistitem);

        }
    }

    public MultiSelectCoordinateAdapter(ArrayList<Coordinates> coordinatesArrayList, ArrayList<Coordinates> selected_coordinateslist, Context mContext) {
        this.coordinatesArrayList = coordinatesArrayList;
        this.selected_coordinateslist = selected_coordinateslist;
        this.mContext = mContext;
    }


    @Override
    public MultiSelectCoordinateAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ongoing_chkpoint_listview, parent, false);

        return new MultiSelectCoordinateAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MultiSelectCoordinateAdapter.MyViewHolder holder, int position) {
        Coordinates coordinates = coordinatesArrayList.get(position);
        holder.name.setText(coordinates.getName());
        holder.desc.setText(""+coordinates.getTimestamp());

        Log.v("hello","hello");

        if(selected_coordinateslist.contains(coordinatesArrayList.get(position))) {
            Log.v("hello","sel");
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_selected_state));
        }
        else {
            Log.v("hello","desel");
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_normal_state));
        }

    }

    @Override
    public int getItemCount() {
        return coordinatesArrayList.size();
    }

    public void add(Coordinates coordinates) {
        coordinatesArrayList.add(coordinatesArrayList.size(), coordinates);
        notifyItemInserted(coordinatesArrayList.size()-1);
    }

    public void clear() {
        coordinatesArrayList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Coordinates> list) {
        coordinatesArrayList.addAll(list);
        notifyDataSetChanged();
    }

}
