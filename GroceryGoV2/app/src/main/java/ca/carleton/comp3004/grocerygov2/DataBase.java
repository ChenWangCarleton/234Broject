package ca.carleton.comp3004.grocerygov2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Abdullrhman Aljasser on 2017-11-09.
 */

public class DataBase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userslist.db";
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCTNAME = "productname";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCTID = "productid";

    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ COLUMN_PRODUCTNAME + " TEXT," + COLUMN_QUANTITY + " INTEGER," + COLUMN_PRODUCTID + " INTEGER" + ");";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(sqLiteDatabase);
    }

    public void addProduct(Product p){
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + " = \"" + p.na + "\";";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() <= 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCTID, p.id);
            values.put(COLUMN_PRODUCTNAME, p.id);
            values.put(COLUMN_QUANTITY, 1);

            db.insert(TABLE_PRODUCTS, null, values);
        } else {
            cursor.moveToFirst();
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY))+1);
            db.update(TABLE_PRODUCTS, values, COLUMN_ID + "=" + cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID)), null);

        }
    }
}
