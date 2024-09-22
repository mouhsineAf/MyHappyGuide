package com.devm22.happyguide.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.devm22.happyguide.model.Tips;
import com.devm22.happyguide.utils.Utils;

import java.util.ArrayList;

public class DatabaseHelperTips extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelperTips";

    SQLiteDatabase sqLiteDatabase;

    private String CREATE_TABLE;
    private static final String DATABASE_NAME = "AllTips.sqlite";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "TipsTable";

    private static final String COLUMN_TIPS_ID = "tipId";
    private static final String COLUMN_TIPS_NAME = "tipName";
    private static final String COLUMN_TIPS_IMAGE = "tipImage";
    private static final String COLUMN_TIPS_CONTENT = "tipContent";
    private static final String COLUMN_TIPS_DATE = "tipDate";
    private static final String COLUMN_TIPS_RATE = "tipRate";
    private static final String COLUMN_TIPS_FAVORITE = "tipFavorite";




    public DatabaseHelperTips(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_TIPS_ID + " INTEGER,"
                + COLUMN_TIPS_NAME + " TEXT,"
                + COLUMN_TIPS_IMAGE + " TEXT,"
                + COLUMN_TIPS_CONTENT + " TEXT,"
                + COLUMN_TIPS_DATE + " DATETIME,"
                + COLUMN_TIPS_RATE + " FLOAT,"
                + COLUMN_TIPS_FAVORITE + " INTEGER"
                + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public void addTips(int tipId, String tipName, String tipImage, String tipContent, String tipDate, float tipRate, int tipFavorite) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TIPS_ID, tipId);
        contentValues.put(COLUMN_TIPS_NAME, tipName);
        contentValues.put(COLUMN_TIPS_IMAGE, tipImage);
        contentValues.put(COLUMN_TIPS_CONTENT, tipContent);
        contentValues.put(COLUMN_TIPS_DATE, tipDate);
        contentValues.put(COLUMN_TIPS_RATE, tipRate);
        contentValues.put(COLUMN_TIPS_FAVORITE, tipFavorite);

        database.insert(TABLE_NAME, null, contentValues);
        database.close();
    }

    public void updateTips(int tipId, String tipName, String tipImage, String tipContent) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TIPS_NAME, tipName);
        contentValues.put(COLUMN_TIPS_IMAGE, tipImage);
        contentValues.put(COLUMN_TIPS_CONTENT, tipContent);

        database.update(TABLE_NAME, contentValues, COLUMN_TIPS_ID + "= ?", new String[]{"" + tipId});
        database.close();
    }

    public void updateTipsFavorite(int tipId, int tipFavorite) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TIPS_FAVORITE, tipFavorite);

        database.update(TABLE_NAME, contentValues, COLUMN_TIPS_ID + "= ?", new String[]{"" + tipId});
        database.close();
    }

    public void updateTipsRate(int tipId, float tipRate) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TIPS_RATE, tipRate);

        database.update(TABLE_NAME, contentValues, COLUMN_TIPS_ID + "= ?", new String[]{"" + tipId});
        database.close();
    }

    public boolean CheckIsDataAlreadyInDBorNot(int valueId) {
        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_TIPS_ID + " = " + valueId;
        Cursor cursor = getReadableDatabase().rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }


    public ArrayList<Tips> getTipsByDate(Boolean newDate) {
        ArrayList<Tips> appModels = new ArrayList();
        ArrayList<Tips> appModelsNulls = new ArrayList();
        boolean isNull = true;
        String query;
        if (newDate){
            query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TIPS_DATE + " ASC ";

        }else {
            query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TIPS_DATE + " DESC ";

        }

        Cursor cursor = getReadableDatabase().rawQuery(query, null);


        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_TIPS_ID));
                @SuppressLint("Range")
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_NAME));
                @SuppressLint("Range")
                String image = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_IMAGE));
                @SuppressLint("Range")
                String content = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_CONTENT));
                @SuppressLint("Range")
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_DATE));
                @SuppressLint("Range")
                float rate = cursor.getFloat(cursor.getColumnIndex(COLUMN_TIPS_RATE));
                @SuppressLint("Range")
                boolean favorite = cursor.getInt(cursor.getColumnIndex(COLUMN_TIPS_FAVORITE)) == 1;


                Tips list = new Tips(id, name, image, content, Utils.stringToDate(date), rate, favorite);
                appModels.add(list);
                isNull = false;
            } while (cursor.moveToNext());
        }
        return isNull ? appModelsNulls : appModels;
    }

    public ArrayList<Tips> getTipsByRate(Boolean bestRate) {
        ArrayList<Tips> appModels = new ArrayList();
        ArrayList<Tips> appModelsNulls = new ArrayList();
        boolean isNull = true;
        String query;
        if (bestRate){
            query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TIPS_RATE + " DESC ";

        }else {
            query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TIPS_RATE + " ASC ";

        }

        Cursor cursor = getReadableDatabase().rawQuery(query, null);


        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_TIPS_ID));
                @SuppressLint("Range")
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_NAME));
                @SuppressLint("Range")
                String image = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_IMAGE));
                @SuppressLint("Range")
                String content = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_CONTENT));
                @SuppressLint("Range")
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_DATE));
                @SuppressLint("Range")
                float rate = cursor.getFloat(cursor.getColumnIndex(COLUMN_TIPS_RATE));
                @SuppressLint("Range")
                boolean favorite = cursor.getInt(cursor.getColumnIndex(COLUMN_TIPS_FAVORITE)) == 1;


                Tips list = new Tips(id, name, image, content, Utils.stringToDate(date), rate, favorite);
                appModels.add(list);
                isNull = false;
            } while (cursor.moveToNext());
        }
        return isNull ? appModelsNulls : appModels;
    }

    public ArrayList<Tips> getFavoriteTips() {
        ArrayList<Tips> appModels = new ArrayList();
        ArrayList<Tips> appModelsNulls = new ArrayList();
        boolean isNull = true;
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TIPS_FAVORITE + " = '1'";

        Cursor cursor = getReadableDatabase().rawQuery(query, null);


        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_TIPS_ID));
                @SuppressLint("Range")
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_NAME));
                @SuppressLint("Range")
                String image = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_IMAGE));
                @SuppressLint("Range")
                String content = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_CONTENT));
                @SuppressLint("Range")
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_TIPS_DATE));
                @SuppressLint("Range")
                float rate = cursor.getFloat(cursor.getColumnIndex(COLUMN_TIPS_RATE));
                @SuppressLint("Range")
                boolean favorite = cursor.getInt(cursor.getColumnIndex(COLUMN_TIPS_FAVORITE)) == 1;


                Tips list = new Tips(id, name, image, content, Utils.stringToDate(date), rate, favorite);
                appModels.add(list);
                isNull = false;
            } while (cursor.moveToNext());
        }
        return isNull ? appModelsNulls : appModels;
    }





}
