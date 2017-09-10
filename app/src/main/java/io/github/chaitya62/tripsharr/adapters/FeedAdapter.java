package io.github.chaitya62.tripsharr.adapters;

import android.content.Context;
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

import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.FontManager;

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
        protected LottieAnimationView cStar;
        private Trip trip;



        //protected ImageView cImage;

        public TripViewHolder(View v) {
            super(v);

            cTitle =  (TextView) v.findViewById(R.id.card_title);
            cName = (TextView)  v.findViewById(R.id.card_name);
            cDesc = (TextView)  v.findViewById(R.id.card_description);
            cStar = (LottieAnimationView) v.findViewById(R.id.card_image);
            c_no_of_forks = (TextView) v.findViewById(R.id.no_of_forks);
            c_no_of_stars = (TextView) v.findViewById(R.id.no_of_stars);
            cStar.setOnClickListener(this);
            star = (TextView) v.findViewById(R.id.star);
            fork = (TextView) v.findViewById(R.id.fork);
            Typeface iconFont = FontManager.getTypeface(v.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(v, iconFont);
            //star.setTypeface(FontManager.getTypeface(v.getContext(),FontManager.FONTAWESOME));
            //cImage = (ImageView) v.findViewById(R.id.card_image);
        }

//        public void setIndex(int i){
//            index = i;
//        }

        @Override
        public void onClick(View view) {
            // handle multiple clicks

           if(view.getId() == star.getId()){
               Log.i("CLICKED", "DID");

               //star.setImageDrawable();

           }else if(view.getId() == fork.getId()) {
               Toast.makeText(ctx, "Fork Clicked: "+trip.getId(), Toast.LENGTH_SHORT).show();
           }
        }

        public void toggleStar(Trip trip){
            if(trip.isStarred()){
                this.star.setText("\uf005");
                this.star.setTextColor(Color.rgb(255,234,0));
                //Toast.makeText(ctx, "Star Clicked: "+trip.getId(), Toast.LENGTH_SHORT).show();
                trip.setStarred(false);
            }else{
                this.star.setText("\uf006");
                this.star.setTextColor(Color.BLACK);
                //Toast.makeText(ctx, "Star Clicked: "+trip.getId(), Toast.LENGTH_SHORT).show();
                trip.setStarred(true);
            }

        }


        public void setTrip(Trip trip) {
            this.trip = trip;
        }

//        public JSONObject getJsonFile(){
//            InputStream is;
//            JSONObject jsonObj;
//            try{
//                is = mgr.open("star.json");
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//                String json = new String(buffer, "UTF-8");
//                jsonObj = new JSONObject(json);
//                return jsonObj;
//            }catch(Exception e){
//                e.printStackTrace();
//                Log.i("DEBUG: ","Resource not found!");
//            }
//            return null;
//        }
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


        tripViewHolder.star.setOnClickListener(tripViewHolder);
        //tripViewHolder.toggleStar(trip);
        if(trip.isStarred())  tripViewHolder.star.setText("\uf006");
        else tripViewHolder.star.setText("\uf006");
        tripViewHolder.fork.setOnClickListener(tripViewHolder);
        tripViewHolder.setTrip(trip);
        //if(trip.isStarred()){
          //  tripViewHolder.cStar.reverseAnimation();
            //Log.i("NOT STARRED", "WRITE CODE TO SET DEFAULT STAR HERE");
            //TODO add default star here
        //}else {
            //tripViewHolder.cStar.refreshDrawableState();
//        //}
//        tripViewHolder.cStar.setAnimation(getJsonFile());
//
//        tripViewHolder.cStar.setOnClickListener(tripViewHolder);
        //RoundedBitmapDrawable mDrawable = createRoundedBitmapDrawableWithBorder(BitmapFactory.decodeResource(context.getResources(), R.drawable.googleg_standard_color_18));
        //tripViewHolder.cImage.setImageDrawable(mDrawable);
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
