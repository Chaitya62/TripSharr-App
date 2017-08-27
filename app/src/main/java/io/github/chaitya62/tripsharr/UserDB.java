package io.github.chaitya62.tripsharr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.github.chaitya62.tripsharr.primeobjects.User;

/**
 * Created by ankit on 26/8/17.
 */

public class UserDB extends SQLiteOpenHelper{

    public  UserDB(Context context) {
        super(context, "User", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user " +
                        "id integer PRIMARY KEY AUTO_INCREMENT," +
                "name text  NOT NULL," +
                "email text NOT NULL," +
                "user_id text UNIQUE NOT NULL"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "user");
        onCreate(db);
    }

    private ContentValues putContents(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", user.getId());
        contentValues.put("email", user.getEmail());
        contentValues.put("user_id", user.getUserId());
        contentValues.put("name", user.getName());
        return contentValues;
    }

    public boolean save(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = putContents(user);
        db.insert("user", null, contentValues);
        return true;
    }

    public User getById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = new User();
        Cursor res = db.rawQuery("select * from user where id = "+id, null);
        user.setUserId(res.getString(res.getColumnIndex("user_id")));
        user.setEmail(res.getString(res.getColumnIndex("email")));
        user.setId(id);
        user.setName(res.getString(res.getColumnIndex("name")));
        res.close();
        return user;
    }

    public boolean update(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = putContents(user);
        db.update("user", contentValues, "id = "+user.getId(), null);
        return true;
    }
}
