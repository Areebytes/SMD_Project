package com.example.smd_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "property_app.db";
    // Bumped to 5 to add "phone" column to the users table
    private static final int    DB_VERSION = 5;

    // Users table
    private static final String TABLE_USERS      = "users";
    private static final String COL_UID          = "uid";
    private static final String COL_NAME         = "name";
    private static final String COL_EMAIL        = "email";
    private static final String COL_PHONE        = "phone";
    private static final String COL_IS_SELLER    = "is_seller"; // 0 = buyer, 1 = seller/admin

    // Favourites table
    private static final String TABLE_FAVOURITES  = "favourites";
    private static final String COL_PROPERTY_ID   = "property_id";
    private static final String COL_PROPERTY_NAME = "property_name";
    private static final String COL_PRICE         = "price";
    private static final String COL_LOCATION      = "location";
    private static final String COL_TYPE          = "type";
    private static final String COL_IMAGE_URL     = "image_url";
    private static final String COL_IS_FEATURED   = "is_featured";
    private static final String COL_USER_ID       = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_UID       + " TEXT PRIMARY KEY, "
                + COL_NAME      + " TEXT, "
                + COL_EMAIL     + " TEXT, "
                + COL_PHONE     + " TEXT, "
                + COL_IS_SELLER + " INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE " + TABLE_FAVOURITES + " ("
                + COL_PROPERTY_ID   + " TEXT, "
                + COL_PROPERTY_NAME + " TEXT, "
                + COL_PRICE         + " INTEGER, "
                + COL_LOCATION      + " TEXT, "
                + COL_TYPE          + " TEXT, "
                + COL_IMAGE_URL     + " TEXT, "
                + COL_IS_FEATURED   + " INTEGER, "
                + COL_USER_ID       + " TEXT, "
                + "PRIMARY KEY (" + COL_PROPERTY_ID + ", " + COL_USER_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        onCreate(db);
    }

    public void saveUser(String uid, String name, String email, String phone, boolean isSeller) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_UID,       uid);
        cv.put(COL_NAME,      name);
        cv.put(COL_EMAIL,     email);
        cv.put(COL_PHONE,     phone);
        cv.put(COL_IS_SELLER, isSeller ? 1 : 0);
        db.insertWithOnConflict(TABLE_USERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Overload for when phone is not available
    public void saveUser(String uid, String name, String email, boolean isSeller) {
        saveUser(uid, name, email, "", isSeller);
    }

    public boolean isUserSeller(String uid) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_IS_SELLER + " FROM " + TABLE_USERS + " WHERE " + COL_UID + " = ?", new String[]{uid});
        boolean isSeller = false;
        if (cursor != null && cursor.moveToFirst()) {
            isSeller = cursor.getInt(0) == 1;
            cursor.close();
        }
        db.close();
        return isSeller;
    }

    public Cursor getUser(String uid) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_UID + " = ?",
                new String[]{uid});
    }

    public void addFavourite(String userId, String propertyId,
                             String name, int price, String location,
                             String type, String imageUrl, boolean isFeatured) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PROPERTY_ID,   propertyId);
        cv.put(COL_PROPERTY_NAME, name);
        cv.put(COL_PRICE,         price);
        cv.put(COL_LOCATION,      location);
        cv.put(COL_TYPE,          type   != null ? type     : "");
        cv.put(COL_IMAGE_URL,     imageUrl != null ? imageUrl : "");
        cv.put(COL_IS_FEATURED,   isFeatured ? 1 : 0);
        cv.put(COL_USER_ID,       userId);
        db.insertWithOnConflict(TABLE_FAVOURITES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void removeFavourite(String propertyId, String userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_FAVOURITES,
                COL_PROPERTY_ID + " = ? AND " + COL_USER_ID + " = ?",
                new String[]{propertyId, userId});
        db.close();
    }

    public boolean isFavourite(String propertyId, String userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + TABLE_FAVOURITES +
                        " WHERE " + COL_PROPERTY_ID + " = ? AND " + COL_USER_ID + " = ?",
                new String[]{propertyId, userId});
        boolean exists = c.moveToFirst();
        c.close();
        db.close();
        return exists;
    }

    public Cursor getFavourites(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_FAVOURITES +
                        " WHERE " + COL_USER_ID + " = ?",
                new String[]{userId});
    }
}