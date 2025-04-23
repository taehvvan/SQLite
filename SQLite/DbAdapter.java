package com.example.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DbAdapter {
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_TABLE = "notes";

    static final String KEY_ROWID = "_id";
    static final String KEY_TITLE = "title";
    static final String KEY_BODY = "body";
    static final String KEY_ETC = "etc";

    private static final String CREATE_TABLE =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, body text not null, etc text not null);";

    private static final String TAG = "DbAdapter";
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;
    private static Context mCtx;
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    private static class DatabaseHelper extends SQLiteOpenHelper {
        InputStream in;
        BufferedReader reader;
        ContentValues initialData = new ContentValues();

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL( CREATE_TABLE );

            // 초기 데이터 작성
            in = mCtx.getResources().openRawResource(R.raw.init_data);

            try {
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String s = null;
                String[] init;

                while ((s = reader.readLine()) != null) {
                    init = s.split("/");
                    // 데이터가 2개뿐이므로 그대로 처리 (데이터 양이 많으면 루프를 통해 처리)
                    initialData.put(KEY_TITLE, init[0]);
                    initialData.put(KEY_BODY, init[1]);
                    initialData.put(KEY_ETC, init[2]);
                    db.insert(DATABASE_TABLE, null, initialData);
                }
            } catch (IOException e) {
                Log.e("File Error", e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long insertNote(String title, String body, String etc) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_ETC, etc);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public Cursor fetchAllNotes() {
        return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_ETC }, null, null, null, null, "_id DESC");
    }

    public boolean updateNote(String rowId, String title, String body, String etc) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_ETC, etc);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteNote(String rowId) {
        Log.i("Delete called", "value__" + rowId);
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
}