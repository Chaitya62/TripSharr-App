package io.github.chaitya62.tripsharr.localDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import io.github.chaitya62.tripsharr.primeobjects.Trip;

/**
 * Created by ankit on 28/8/17.
 */

public class TripDB extends SQLiteOpenHelper {

    public TripDB(Context context) {
        super(context, "Trip", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table trips(" +
                "id integer primary key autoincrement,"+
                "user_id integer NOT NULL," +
                "name text NOT NULL," +
                "description text," +
                "start_coordinate_id int DEFAULT 0"+
                "end_coordinate_id int DEFAULT 0"+
                "no_of_stars integer DEFAULT 0," +
                "no_of_forks integer DEFAULT 0," +
                "old_trip_id integer DEFAULT NULL," +
                "is_complete integer DEFAULT false NOT NULL," +
                "is_public boolean DEFAULT false NOT NULL," +
                "owner_id integer DEFAULT null );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "trips");
        onCreate(db);
    }

    private ContentValues putContentValues(Trip trip) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", trip.getId());
        contentValues.put("user_id", trip.getUserId());
        contentValues.put("name", trip.getName());
        contentValues.put("description", trip.getDescription());
        contentValues.put("start_coordinate_id", trip.getStartCoordinateId());
        contentValues.put("end_coordinate_id", trip.getEndCoordinateId());
        contentValues.put("no_of_stars", trip.getNoOfStars());
        contentValues.put("no_of_forks", trip.getNoOfForks());
        contentValues.put("is_compete", trip.isComplete());
        contentValues.put("is_public", trip.isPublic());
        contentValues.put("old_trip_id", trip.getOldTripId());
        contentValues.put("owner_id", trip.getOwnerId());
        return contentValues;
    }

    private Trip putTrip(Cursor res) {
        Trip trip = new Trip();
        res.moveToFirst();
        trip.setId(res.getLong(res.getColumnIndex("id")));
        trip.setName(res.getString(res.getColumnIndex("name")));
        trip.setDescription(res.getString(res.getColumnIndex("description")));
        trip.setUserId(res.getLong(res.getColumnIndex("user_id")));
        trip.setOldTripId(res.getLong(res.getColumnIndex("old_trip_id")));
        trip.setNoOfStars(res.getLong(res.getColumnIndex("no_of_stars")));
        trip.setNoOfForks(res.getLong(res.getColumnIndex("no_of_forks")));
        return trip;
    }

    public boolean save(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = putContentValues(trip);
        db.insert("trips", null, contentValues);
        return true;
    }

    public Trip getById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from trips where id = " + id, null);
        return putTrip(cursor);
    }
    
}
