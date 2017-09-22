package io.github.chaitya62.tripsharr.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.chaitya62.tripsharr.ProfileActivity;
import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.FontManager;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by chaitya62 on 2/9/17.
 */

public class ProfileFeedAdapter extends RecyclerView.Adapter<ProfileFeedAdapter.ViewHolder> {

    private List<Trip> trips;
    private Context mcontext;
    static protected long AppUserId;

    public ProfileFeedAdapter(Context context, List<Trip> trips){
        this.mcontext = context;
        this.trips = trips;
        ProfileFeedAdapter.AppUserId = SharedPrefs.getPrefs().getLong("user_id", 1);
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
        holder.setTrip(trip);
        holder.setForkState(trip);
        holder.setStarState(trip);
        if(trip.getUserId() == AppUserId){
            holder.fork.setVisibility(View.GONE);
        }
        holder.star.setOnClickListener(holder);
        holder.fork.setOnClickListener(holder);
        holder.ctx = mcontext;


    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static  class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView cTitle, cName, cDesc, c_no_of_stars, c_no_of_forks, star, fork;
        protected  Context ctx;
        private Trip trip;

        public ViewHolder(View view){
            super(view);
            cTitle = (TextView) view.findViewById(R.id.card_title);
            cName = (TextView) view.findViewById(R.id.card_name);
            cDesc = (TextView) view.findViewById(R.id.card_description);
            c_no_of_forks = (TextView) view.findViewById(R.id.no_of_forks);
            c_no_of_stars = (TextView) view.findViewById(R.id.no_of_stars);
            star = (TextView) view.findViewById(R.id.star);
            fork = (TextView) view.findViewById(R.id.fork);

            Typeface iconFont = FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(view, iconFont);
        }


        @Override
        public void onClick(View view) {
            // handle multiple clicks

            if(view.getId() == star.getId()){
                Log.i("CLICKED", "DID");
                this.toggleStar(trip);
                return;

            }else if(view.getId() == fork.getId()) {
                this.toggleFork(trip);
                Toast.makeText(ctx, "Forked", Toast.LENGTH_SHORT).show();
                return;
            }else if(view.getId() == cName.getId()){
                Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);
                profileIntent.putExtra("user_id", trip.getUserId());
                view.getContext().startActivity(profileIntent);
                return;
            }else if(view.getId() == cTitle.getId()){
                Log.i("HERE it is", "IT WAS HERE");
                Toast.makeText(ctx, "selected the trip", Toast.LENGTH_SHORT).show();
                return;
            }
        }



        public void toggleStar(Trip trip){
            if(trip.isStarred()){
                this.star.setText("\uf006");
                this.star.setTextColor(Color.BLACK);
                //Toast.makeText(ctx, "Star Clicked: "+trip.getId(), Toast.LENGTH_SHORT).show();
                trip.setStarred(false);

            }else{

                this.star.setText("\uf005");
                this.star.setTextColor(Color.rgb(255,234,0));
                //Toast.makeText(ctx, "Star Clicked: "+trip.getId(), Toast.LENGTH_SHORT).show();
                trip.setStarred(true);

            }

            setStarOnServer(trip);


        }

        private void setForkState(Trip trip){
            if(trip.isForked()){
                this.fork.setTextColor(Color.rgb(0,0,225));
            }else{
                this.fork.setTextColor(Color.BLACK);
            }
        }

        private void toggleFork(Trip trip){
            if(!trip.isForked()){
                this.fork.setTextColor(Color.rgb(0, 0, 225));
                trip.setForked(true);
                setForkOnServer(trip);
            }
        }

        private void setForkOnServer(final Trip Forking_trip){
            String fork_url = ctx.getString(R.string.host);
            fork_url +=  "index.php/trip/fork/";

            final HashMap<String, String> starred_Trip = new HashMap<>();
            starred_Trip.put("user_id", SharedPrefs.getPrefs().getLong("user_id", 1)+"");
            starred_Trip.put("trip_id", Forking_trip.getId()+"");
            JsonObjectRequest forkRequest = new JsonObjectRequest(Request.Method.POST, fork_url, new JSONObject(starred_Trip), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("URL",response.toString());
                    if(Forking_trip.isForked()){
                        trip.setNoOfForks(trip.getNoOfForks()+1);
                        c_no_of_forks.setText(trip.getNoOfForks()+"");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Forking ERROR ", "ERROr IN VOLLEY SERER HUGA "+error.toString());
                    Toast.makeText(ctx, "Something went wrong, try again!", Toast.LENGTH_SHORT).show();
                }
            });

            VolleySingleton.getInstance(ctx).addToRequestQueue(forkRequest);

        }

        private void setStarOnServer(final Trip Starring_trip){
            String star_url = ctx.getString(R.string.host);

            if(Starring_trip.isStarred()){
                star_url +=  "index.php/trip/star/";
            }else{
                star_url +=  "index.php/trip/unstar/";
            }
            final HashMap<String, String> starred_Trip = new HashMap<>();
            starred_Trip.put("user_id", SharedPrefs.getPrefs().getLong("user_id", 1)+"");
            starred_Trip.put("trip_id", Starring_trip.getId()+"");
            Log.i("post : ",(starred_Trip.toString()));
            JsonObjectRequest starRequest = new JsonObjectRequest(Request.Method.POST, star_url, new JSONObject(starred_Trip), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("URL",response.toString());
                    if(Starring_trip.isStarred()){
                        trip.setNoOfStars(trip.getNoOfStars()+1);
                        c_no_of_stars.setText(trip.getNoOfStars()+"");
                    }else{
                        trip.setNoOfStars(trip.getNoOfStars()-1);
                        Log.i("DEBUG", "In Stars"+trip.getNoOfStars());
                        c_no_of_stars.setText(trip.getNoOfStars()+"");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("STARRING ERROR ", "ERROr IN VOLLEY SERER HUGA "+error.toString());
                    Toast.makeText(ctx, "Something went wrong, try again!", Toast.LENGTH_SHORT).show();
                }
            });

            VolleySingleton.getInstance(ctx).addToRequestQueue(starRequest);
        }

        private void setStarState(Trip trip){
            Log.i("trip: ",(trip.isStarred())+"" );
            if(trip.isStarred()){
                this.star.setText("\uf005");
                this.star.setTextColor(Color.rgb(255,234,0));
            }else{
                this.star.setText("\uf006");
                this.star.setTextColor(Color.BLACK);
            }


        }
        public void setTrip(Trip trip) {
            this.trip = trip;
        }

    }
}
