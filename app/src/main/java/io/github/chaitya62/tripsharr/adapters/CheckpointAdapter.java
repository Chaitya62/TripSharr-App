package io.github.chaitya62.tripsharr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Coordinates;


/**
 * Created by mikasa on 2/9/17.
 */

public class CheckpointAdapter extends RecyclerView.Adapter<CheckpointAdapter.MyViewHolder> {

    private List<Coordinates> coordinatesList;
    private Context context;

    public CheckpointAdapter(Context context, List<Coordinates> coordinatesList){
        this.context=context;
        this.coordinatesList=coordinatesList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView chkpoint,datetime;

        public MyViewHolder(View view){
            super(view);
            chkpoint = (TextView) view.findViewById(R.id.chkpoint);
            datetime = (TextView) view.findViewById(R.id.datetime);

        }
    }



    @Override
    public CheckpointAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ongoing_chkpoint_listview,parent,false);
        return new CheckpointAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CheckpointAdapter.MyViewHolder holder, int position){
        Coordinates coordinates = coordinatesList.get(position);
        holder.chkpoint.setText(coordinates.getName());
        holder.datetime.setText(""+coordinates.getTimestamp());
    }

    @Override
    public int getItemCount(){
        return coordinatesList.size();
    }

    public void add(Coordinates coordinates) {
        Log.i("DEBUG",coordinates.getName());
        coordinatesList.add(coordinatesList.size(), coordinates);
        notifyItemInserted(coordinatesList.size()-1);
    }
}
