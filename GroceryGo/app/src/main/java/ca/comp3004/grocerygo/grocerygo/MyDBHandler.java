package ca.comp3004.grocerygo.grocerygo;

/**
 * Created by AyeJay on 10/8/2017.
 */

import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MyDBHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "productList.db";
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCTNAME = "productname";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCTID = "productid";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +  COLUMN_PRODUCTNAME + " TEXT," + COLUMN_QUANTITY + " INTEGER,"+ COLUMN_PRODUCTID + " INTEGER" + ");";
            db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    //Add new a row
    public void addProduct(Product product){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + " = \"" + product.get_productName()+"\";";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCTNAME, product.get_productName());
            values.put(COLUMN_QUANTITY, 1);
            values.put(COLUMN_PRODUCTID,product.get_productID());
            db.insert(TABLE_PRODUCTS, null, values);
            db.close();
        } else {
            cursor.moveToFirst();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_QUANTITY, (cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)) + 1));
            cv.put(COLUMN_PRODUCTNAME, product.get_productName());
            cv.put(COLUMN_PRODUCTID,product.get_productID());
            //Log.e(TAG,cursor.getString(cursor.getColumnIndex(COLUMN_QUANTITY)));
            db.update(TABLE_PRODUCTS, cv, COLUMN_ID + "=" + cursor.getString(cursor.getColumnIndex(COLUMN_ID)), null);
        }
    }

    //Delete product
    public void deleteProduct(String productName){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + " = \"" + productName +"\";";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() <= 0) {
            Log.e("Console:"," NO UPDATE");
        } else {
            cursor.moveToFirst();
            //Log.e("data", ""+ cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
            if(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)) <= 1){
                db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE "+ COLUMN_PRODUCTNAME + "=\"" + productName + "\";");
            } else {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_QUANTITY, cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)) - 1);
                cv.put(COLUMN_PRODUCTNAME, productName);
                db.update(TABLE_PRODUCTS, cv, COLUMN_ID + "=" + cursor.getString(cursor.getColumnIndex(COLUMN_ID)), null);
            }

        }
    }

    public ArrayList<String> dbToAS(){
        ArrayList<String> dbAS = new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";

        //Cursor pointer
        Cursor c = db.rawQuery(query, null);
        //move to first row
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_PRODUCTNAME)) != null){
                dbAS.add(c.getString(c.getColumnIndex(COLUMN_PRODUCTNAME)));
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbAS;
    }
    public int getProductID(String productName){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + " = \"" + productName +"\";";
        Cursor cursor = db.rawQuery(query, null);

        //Check if product exists
        if(cursor.getCount() <= 0){
            Log.e("ERROR", "DONESN'T EXISTS");
        } else {
            cursor.moveToNext();
            return cursor.getInt(cursor.getColumnIndex(COLUMN_PRODUCTID));
        }
        return 0;
    }

}
