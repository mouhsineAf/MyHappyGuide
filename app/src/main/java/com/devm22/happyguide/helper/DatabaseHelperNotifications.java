package com.devm22.happyguide.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.devm22.happyguide.model.Notifications;

import java.util.ArrayList;

public class DatabaseHelperNotifications  extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelperNotifications";

    SQLiteDatabase sqLiteDatabase;

    private String CREATE_TABLE;
    private static final String DATABASE_NAME = "AllNotifications.sqlite";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "NotificationsTable";

    private static final String COLUMN_NOTIFICATIONS_ID = "notificationId";
    private static final String COLUMN_NOTIFICATIONS_TITLE = "notificationTitle";
    private static final String COLUMN_NOTIFICATIONS_MESSAGE = "notificationMessage";
    private static final String COLUMN_NOTIFICATIONS_ICON = "notificationIcon";
    private static final String COLUMN_NOTIFICATIONS_LARGE_ICON = "notificationLargeIcon";
    private static final String COLUMN_NOTIFICATIONS_PICTURE = "notificationPicture";
    private static final String COLUMN_NOTIFICATIONS_LAUNCH_URL = "notificationLaunchURL";
    private static final String COLUMN_NOTIFICATIONS_SENDING_DATE = "notificationSendingDate";
    private static final String COLUMN_NOTIFICATIONS_SHOWED = "notificationShowed";


    public DatabaseHelperNotifications(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_NOTIFICATIONS_ID + " INTEGER,"
                + COLUMN_NOTIFICATIONS_TITLE + " TEXT,"
                + COLUMN_NOTIFICATIONS_MESSAGE + " TEXT,"
                + COLUMN_NOTIFICATIONS_ICON + " TEXT,"
                + COLUMN_NOTIFICATIONS_LARGE_ICON + " TEXT,"
                + COLUMN_NOTIFICATIONS_PICTURE + " TEXT,"
                + COLUMN_NOTIFICATIONS_LAUNCH_URL + " TEXT,"
                + COLUMN_NOTIFICATIONS_SENDING_DATE + " TEXT,"
                + COLUMN_NOTIFICATIONS_SHOWED + " INTEGER"
                + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public void addNotifications(int notificationId, String notificationTitle, String notificationMessage, String notificationIcon, String notificationLargeIcon,
                                 String notificationPicture, String notificationLaunchURL, String notificationSendingDate, int notificationShowed) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTIFICATIONS_ID, notificationId);
        contentValues.put(COLUMN_NOTIFICATIONS_TITLE, notificationTitle);
        contentValues.put(COLUMN_NOTIFICATIONS_MESSAGE, notificationMessage);
        contentValues.put(COLUMN_NOTIFICATIONS_ICON, notificationIcon);
        contentValues.put(COLUMN_NOTIFICATIONS_LARGE_ICON, notificationLargeIcon);
        contentValues.put(COLUMN_NOTIFICATIONS_PICTURE, notificationPicture);
        contentValues.put(COLUMN_NOTIFICATIONS_LAUNCH_URL, notificationLaunchURL);
        contentValues.put(COLUMN_NOTIFICATIONS_SENDING_DATE, notificationSendingDate);
        contentValues.put(COLUMN_NOTIFICATIONS_SHOWED, notificationShowed);

        database.insert(TABLE_NAME, null, contentValues);
        database.close();
    }

    public void updateShowedNotifications() {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NOTIFICATIONS_SHOWED, 1);

        database.update(TABLE_NAME, contentValues, COLUMN_NOTIFICATIONS_SHOWED + "= ?", new String[]{"" + 0});
     //   database.update(TABLE_NAME, contentValues, COLUMN_NOTIFICATIONS_ID + "= ?", new String[]{"" + notificationId});
        database.close();
    }


    public boolean CheckIsDataAlreadyInDBorNot(int valueId) {
        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_NOTIFICATIONS_ID + " = " + valueId;
        Cursor cursor = getReadableDatabase().rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean CheckForNewNotifications() {
        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_NOTIFICATIONS_SHOWED + " = 0";
        Cursor cursor = getReadableDatabase().rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }


    public ArrayList<Notifications> getNotifications() {
        ArrayList<Notifications> appModels = new ArrayList();
        ArrayList<Notifications> appModelsNulls = new ArrayList();
        boolean isNull = true;
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_NOTIFICATIONS_SENDING_DATE + " DESC ";

        Cursor cursor = getReadableDatabase().rawQuery(query, null);


        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int notificationId = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_ID));
                @SuppressLint("Range")
                String notificationTitle = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_TITLE));
                @SuppressLint("Range")
                String notificationMessage = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_MESSAGE));
                @SuppressLint("Range")
                String notificationIcon = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_ICON));
                @SuppressLint("Range")
                String notificationLargeIcon = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_LARGE_ICON));
                @SuppressLint("Range")
                String notificationPicture = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_PICTURE));
                @SuppressLint("Range")
                String notificationLaunchURL = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_LAUNCH_URL));
                @SuppressLint("Range")
                String notificationSendingDate = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_SENDING_DATE));
                @SuppressLint("Range")
                Boolean notificationShowed = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_SENDING_DATE)) == 1;

                Notifications list = new Notifications(notificationId, notificationTitle, notificationMessage, notificationIcon, notificationLargeIcon, notificationPicture, notificationLaunchURL, notificationSendingDate, notificationShowed);
                appModels.add(list);
                isNull = false;
            } while (cursor.moveToNext());
        }
        return isNull ? appModelsNulls : appModels;
    }


}
