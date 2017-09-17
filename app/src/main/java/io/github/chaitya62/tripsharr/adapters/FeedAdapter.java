package io.github.chaitya62.tripsharr.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import io.github.chaitya62.tripsharr.ProfileActivity;
import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.FontManager;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by ankit on 30/8/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.TripViewHolder>  {

    // static for now
    private  List<Trip> tripList;
    private Context context;
    private static Context ctx;
    private static AssetManager mgr;

    public FeedAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.ctx = context;
        this.tripList = tripList;
        notifyDataSetChanged();
        mgr = context.getAssets();
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }



    public static class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView cTitle;
        protected TextView cName;
        protected TextView cDesc;
        protected TextView c_no_of_stars;
        protected TextView c_no_of_forks;
        protected TextView star,fork;
        protected  View view;

        private Trip trip;



        //protected ImageView cImage;

        public TripViewHolder(View v) {
            super(v);
            view = v;
            cTitle =  (TextView) v.findViewById(R.id.card_title);
            cName = (TextView)  v.findViewById(R.id.card_name);
            cDesc = (TextView)  v.findViewById(R.id.card_description);
            c_no_of_forks = (TextView) v.findViewById(R.id.no_of_forks);
            c_no_of_stars = (TextView) v.findViewById(R.id.no_of_stars);
            star = (TextView) v.findViewById(R.id.star);
            fork = (TextView) v.findViewById(R.id.fork);


            Typeface iconFont = FontManager.getTypeface(v.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(v, iconFont);
        }

//        public void setIndex(int i){
//            index = i;
//        }

        @Override
        public void onClick(View view) {
            // handle multiple clicks

           if(view.getId() == star.getId()){
               Log.i("CLICKED", "DID");
                this.toggleStar(trip);

               //TODO update trip on server
               //star.setImageDrawable();

           }else if(view.getId() == fork.getId()) {
               this.toggleFork(trip);
               //Toast.makeText(ctx, "Fork Clicked: "+trip.getId(), Toast.LENGTH_SHORT).show();
           }else if(view.getId() == cName.getId()){
               Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);
               profileIntent.putExtra("user_id", trip.getUserId());
               view.getContext().startActivity(profileIntent);
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
                    if(Forking_trip.isStarred()){
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

    @Override
    public void onBindViewHolder(TripViewHolder tripViewHolder, int i) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.com_facebook_button_like_background);
        Trip trip = tripList.get(i);
        Log.i("STARRED : ", trip.isStarred()+"");
        tripViewHolder.cName.setText(trip.getUserName());
        tripViewHolder.cTitle.setText(trip.getName());
        tripViewHolder.cDesc.setText(trip.getDescription());
        tripViewHolder.c_no_of_forks.setText(""+trip.getNoOfForks());
        tripViewHolder.c_no_of_stars.setText(""+trip.getNoOfStars());

        if(trip.isStarred())  tripViewHolder.star.setText("\uf006");
        else tripViewHolder.star.setText("\uf006");

        tripViewHolder.setTrip(trip);
        tripViewHolder.setStarState(trip);
        tripViewHolder.setForkState(trip);

        tripViewHolder.cName.setOnClickListener(tripViewHolder);
        tripViewHolder.star.setOnClickListener(tripViewHolder);
        tripViewHolder.fork.setOnClickListener(tripViewHolder);




    }

    public JSONObject getJsonFile(){
        InputStream is;
        JSONObject jsonObj;
        try{
            is = mgr.open("star.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            jsonObj = new JSONObject(json);
            return jsonObj;
        }catch(Exception e){
            e.printStackTrace();
            Log.i("DEBUG: ","Resource not found!");
        }
        return null;
    }





    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.i("Debug", "onCreateViewHolder");
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.trip_feed_card, viewGroup, false);
        return new TripViewHolder(itemView);
    }

    public void add(Trip trip) {
        tripList.add(tripList.size(), trip);
        notifyItemInserted(tripList.size()-1);
    }

    public void clear() {
        tripList.clear();
        Log.i("URL", "CLEAR CALLED "+tripList.size());
        this.notifyDataSetChanged();
    }

    private RoundedBitmapDrawable createRoundedBitmapDrawableWithBorder(Bitmap bitmap){
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int borderWidthHalf = 10;
        int bitmapRadius = Math.min(bitmapWidth,bitmapHeight)/2;
        int bitmapSquareWidth = Math.min(bitmapWidth,bitmapHeight);
        int newBitmapSquareWidth = bitmapSquareWidth+borderWidthHalf;
        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth,newBitmapSquareWidth,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        canvas.drawColor(Color.RED);
        int x = borderWidthHalf + bitmapSquareWidth - bitmapWidth;
        int y = borderWidthHalf + bitmapSquareWidth - bitmapHeight;
        canvas.drawBitmap(bitmap, x, y, null);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidthHalf*2);
        borderPaint.setColor(Color.WHITE);
        canvas.drawCircle(canvas.getWidth()/2, canvas.getWidth()/2, newBitmapSquareWidth/2, borderPaint);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(),roundedBitmap);
        roundedBitmapDrawable.setAntiAlias(true);
        return roundedBitmapDrawable;
    }
}
