package io.github.chaitya62.tripsharr.localDB;

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
        db.execSQL("create table user (" +
                        "id integer PRIMARY KEY AUTOINCREMENT," +
                "name text  NOT NULL," +
                "email text NOT NULL," +
                "user_id text UNIQUE NOT NULL,"+
                "forks integer DEFAULT 0,"+
                "stars integer DEFAULT 0 );"
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
        contentValues.put("stars", user.getStars());
        contentValues.put("forks", user.getForks());
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
        res.moveToFirst();
        user.setUserId(res.getString(res.getColumnIndex("user_id")));
        user.setEmail(res.getString(res.getColumnIndex("email")));
        user.setId(id);
        user.setForks(res.getLong(res.getColumnIndex("forks")));
        user.setStars(res.getLong(res.getColumnIndex("stars")));
        user.setName(res.getString(res.getColumnIndex("name")));
        res.close();
        return user;
    }

    public User getByUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = new User();
        Cursor res = db.rawQuery("select * from user where user_id = "+userId, null);
        res.moveToFirst();
        user.setUserId(res.getString(res.getColumnIndex("user_id")));
        user.setEmail(res.getString(res.getColumnIndex("email")));
        user.setId(res.getLong(res.getColumnIndex("id")));
        user.setForks(res.getLong(res.getColumnIndex("forks")));
        user.setStars(res.getLong(res.getColumnIndex("stars")));
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
