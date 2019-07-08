package com.meterstoinches.mygrocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int db_version = 1;
    public static final String db_name = "groceryListDB";
    public static final String table_name = "groceryTBL";

    //Table columns
    public static final String key_id = "id";
    public static final String key_groceryItem = "groceryItem";
    public static final String key_qty_num = "quantity_number";
    public static final String key_date_name = "date_added";

    String create_GroceryTable = "create table "+table_name+"("+key_id+
            " Integer Primary key,"+key_groceryItem+ " text,"+key_qty_num
            + " text,"+key_date_name+" long"+")";

    String drop_GroceryTable = "drop table if exists "+table_name;

    public DatabaseHandler(Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_GroceryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(drop_GroceryTable);
        onCreate(db);
    }
    //CRUD OPERATIONS

    //Add Grocery

    public void addGrocery(Grocery grocery){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key_groceryItem,grocery.getName());
        values.put(key_qty_num, grocery.getQuantity());
        //get system time
        values.put(key_date_name,java.lang.System.currentTimeMillis());
        //insert the row
        db.insert(table_name,null,values);

        Log.d("Saved!!!","Saved to DB");
    }

    //Get a Grocery
    public Grocery getGrocery(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Grocery grocery = new Grocery();
        Cursor cursor = db.query(table_name, new String[]{
                        key_id, key_groceryItem, key_qty_num,
                        key_date_name},
                key_id+"=?",
                new String[]{String.valueOf(id)},null,null,null, null);
        if (cursor != null){
            cursor.moveToFirst();

            grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(key_id))));
            grocery.setName(cursor.getString(cursor.getColumnIndex(key_groceryItem)));
            grocery.setQuantity(cursor.getString(cursor.getColumnIndex(key_qty_num)));

            //CONVERT timestamp to something readable
            java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
            String formatedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex
                    (key_date_name))).getTime());

            grocery.setDateItemAdded(formatedDate);
        }

        return grocery;
    }

    //Get all Grocery
    public List<Grocery> getAllGroceries(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Grocery> groceryList = new ArrayList<>();
        Cursor cursor = db.query(table_name, new String[]{
                        key_id, key_groceryItem, key_qty_num,
                        key_date_name}, null,null,null, null,
                key_date_name + " DESC");

        if (cursor.moveToFirst()){
            do{
                Grocery grocery = new Grocery();
                grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(key_id))));
                grocery.setName(cursor.getString(cursor.getColumnIndex(key_groceryItem)));
                grocery.setQuantity(cursor.getString(cursor.getColumnIndex(key_qty_num)));

                //CONVERT timestamp to something readable
                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                String formatedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex
                        (key_date_name))).getTime());

                grocery.setDateItemAdded(formatedDate);
                //add to the groceryList
                groceryList.add(grocery);
            }while (cursor.moveToNext());
        }

        return groceryList;
    }
    //Updated Grocery
    public int updateGrocery(Grocery grocery){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key_groceryItem,grocery.getName());
        values.put(key_qty_num, grocery.getQuantity());
        //get system time
        values.put(key_date_name,java.lang.System.currentTimeMillis());

        //update row
        return db.update(table_name,values, key_id + "=?",
                new String[]{ String.valueOf(grocery.getId()) });
    }
    //Delete Grocery
    public void deleteGrocery(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_name, key_id+ " =?",new
                String[]{String.valueOf(id)});
        db.close();
    }
    //Get Count
    public int getGroceriesCount(){
        String countquery = "select * from "+ table_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countquery,null);

        return cursor.getCount();
    }
}
