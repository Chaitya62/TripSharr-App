package io.github.chaitya62.tripsharr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.chaitya62.tripsharr.primeobjects.Trip;

/**
 * Created by ankit on 30/8/17.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private List<Trip> tripList;
    private Context context;

    public TripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        protected TextView cTitle;
        protected TextView cName;
        protected TextView cDesc;
        protected ImageView cImage;

        public TripViewHolder(View v) {
            super(v);
            cTitle =  (TextView) v.findViewById(R.id.card_title);
            cName = (TextView)  v.findViewById(R.id.card_name);
            cDesc = (TextView)  v.findViewById(R.id.card_description);
            cImage = (ImageView) v.findViewById(R.id.card_image);
        }
    }

    @Override
    public void onBindViewHolder(TripViewHolder tripViewHolder, int i) {
        Log.i("Debug", "onBindViewHolder");
        Trip trip = tripList.get(i);
        tripViewHolder.cName.setText(trip.getName());
        tripViewHolder.cTitle.setText(trip.getName());
        tripViewHolder.cDesc.setText(trip.getDescription());
        RoundedBitmapDrawable mDrawable = createRoundedBitmapDrawableWithBorder(BitmapFactory.decodeResource(context.getResources(), R.drawable.googleg_standard_color_18));
        tripViewHolder.cImage.setImageDrawable(mDrawable);
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
        notifyDataSetChanged();
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
