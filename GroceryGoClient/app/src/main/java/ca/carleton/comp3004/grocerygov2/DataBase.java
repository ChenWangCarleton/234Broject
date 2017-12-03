package ca.carleton.comp3004.grocerygov2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

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

    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
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

    public void addProduct(Product p){
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTID + " = \"" + p.productID + "\";";
        Cursor cursor = db.rawQuery(query, null);

        Log.e("Test: ", ""+p.productID+p.name);

        int count = cursor.getCount();

        if(count < 1) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCTID, p.productID);
            values.put(COLUMN_PRODUCTNAME, p.name);
            values.put(COLUMN_QUANTITY, 1);
            db.insert(TABLE_PRODUCTS, null, values);
            db.close();
        } else{
            cursor.moveToFirst();
            ContentValues values = new ContentValues();
            int quant = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
            Log.e("Test:" , ""+quant);
            values.put(COLUMN_QUANTITY, (quant+1));
            values.put(COLUMN_PRODUCTNAME, p.name);
            db.update(TABLE_PRODUCTS, values, COLUMN_ID + "=" + cursor.getString(cursor.getColumnIndex(COLUMN_ID)), null);
        }
        cursor.close();
        db.close();
    }

    public void removeProduct(int pID) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTID + " = \"" + pID + "\";";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            //No product to be removed.
            Log.e("Error: ", "Product selected does not exists");
        } else {
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)) <= 1) {
                db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTID + "=\"" + pID + "\";");
            } else {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_QUANTITY, cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)) - 1);
                db.update(TABLE_PRODUCTS, cv, COLUMN_ID + "=" + cursor.getString(cursor.getColumnIndex(COLUMN_ID)), null);
            }
        }
        cursor.close();
        db.close();
    }



    //This will return list of products in the users list with the quantity
    public ArrayList<String> userProductsList(){
        ArrayList<String> products = new ArrayList<String>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        //move to first row
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_PRODUCTNAME)) != null){
                products.add(c.getString(c.getColumnIndex(COLUMN_QUANTITY)) + "x - " + c.getString(c.getColumnIndex(COLUMN_PRODUCTNAME)));
            }
            c.moveToNext();
        }
        //c.close();
        //db.close();

        return products;
    }

    public ArrayList<Integer> userProductsID(){
        ArrayList<Integer> products = new ArrayList<Integer>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_PRODUCTNAME)) != null){
                products.add(c.getInt(c.getColumnIndex(COLUMN_PRODUCTID)));
            }
            c.moveToNext();
        }
        c.close();
        db.close();


        return products;
    }
    public int getQuant(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTID + " = " + id +";";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() <= 0){
            Log.e("Error: ","DOESN'T EXISTS");
        } else {
            cursor.moveToNext();
            int quant = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
            cursor.close();
            db.close();
            return quant;
        }
        cursor.close();
        db.close();
        return 0;
    }
    public void removeAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_PRODUCTS);
    }

    public void deleteProduct(String productName){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + " = \"" + productName +"\";";
        Cursor cursor = db.rawQuery(query, null);

        Log.e("TEST", productName);

        if(cursor.getCount() <= 0) {
            Log.e("Console:"," NO UPDATE");
        } else {
            cursor.moveToFirst();
            if(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)) <= 1){
                db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE "+ COLUMN_PRODUCTNAME + "=\"" + productName + "\";");
            } else {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_QUANTITY, cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)) - 1);
                cv.put(COLUMN_PRODUCTNAME, productName);
                db.update(TABLE_PRODUCTS, cv, COLUMN_ID + "=" + cursor.getString(cursor.getColumnIndex(COLUMN_ID)), null);
            }
        }
        cursor.close();
        db.close();
    }
}
