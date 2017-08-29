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
                "user_id int NOT NULL," +
                "name text NOT NULL," +
                "description text," +
                "start_x double NOT NULL,"+
                "start_y double NOT NULL," +
                "end_x double NOT NULL," +
                "end_y double NOT NULL," +
                "no_of_stars int DEFAULT 0," +
                "no_of_forks int DEFAULT 0," +
                "old_trip_id int DEFAULT NULL," +
                "is_complete int DEFAULT 0 NOT NULL," +
                "is_public boolean DEFAULT 0 NOT NULL," +
                "owner_id int DEFAULT null );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "user");
        onCreate(db);
    }

    private ContentValues putContentValues(Trip trip) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", trip.getId());
        contentValues.put("user_id", trip.getUserId());
        contentValues.put("name", trip.getName());
        contentValues.put("description", trip.getDescription());
        contentValues.put("start_x", trip.getStartCoordinate().first);
        contentValues.put("start_y", trip.getStartCoordinate().second);
        contentValues.put("end_x", trip.getEndCoordinate().first);
        contentValues.put("end_y", trip.getEndCoordinate().second);
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
        trip.setEndCoordinate(new Pair<>(res.getDouble(res.getColumnIndex("end_x")), res.getDouble(res.getColumnIndex("end_y"))));
        trip.setStartCoordinate(new Pair<>(res.getDouble(res.getColumnIndex("start_x")), res.getDouble(res.getColumnIndex("start_y"))));
        trip.setNoOfStars(res.getLong(res.getColumnIndex("no_of_stars")));
        trip.setNoOfForks(res.getLong(res.getColumnIndex("no_of_forks")));
        return trip;
    }

    public boolean save(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = putContentValues(trip);
        db.insert("trip", null, contentValues);
        return true;
    }

    public Trip getById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from trips where id = " + id, null);
        return putTrip(cursor);
    }
    
}
