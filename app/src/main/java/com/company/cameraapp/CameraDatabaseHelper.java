package com.company.cameraapp;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class CameraDatabaseHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "cameraDB";
    private static final int DB_VERSION = 1;

    CameraDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 1) {
            db.execSQL("CREATE TABLE PICTURES ("
                    + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "IMAGE BLOB NOT NULL);");
        }
    }

    public long insertPictureToDB(SQLiteDatabase db, byte[] image) {
        ContentValues pictureValues = new ContentValues();
        pictureValues.put("IMAGE", image);
        long idValue = db.insert("PICTURES", null, pictureValues);
        return idValue;
    }
}
