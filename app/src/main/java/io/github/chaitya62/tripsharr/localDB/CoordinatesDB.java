package io.github.chaitya62.tripsharr.localDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.github.chaitya62.tripsharr.primeobjects.Coordinates;

/**
 * Created by ankit on 29/8/17.
 */

public class CoordinatesDB extends SQLiteOpenHelper {

    public CoordinatesDB(Context context) {
        super(context, "Coordinates", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table coordinates(" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "name text NOT NULL," +
                "description text," +
                "trip_id integer NOT NULL," +
                "x_co real NOT NULL," +
                "y_co real NOT NULL," +
                "priority timestamp DEFAULT CURRENT_TIMESTAMP," +
                "image_count integer DEFAULT 0," +
                "video_count integer DEFAULT 0," +
                "old_coordinate_id integer DEFAULT 0," +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "coordinates");
        onCreate(db);
    }


}
