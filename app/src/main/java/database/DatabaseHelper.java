package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DB_NAME;
    private static final int DB_VERSION = 1;


    //supply databasehelper with a database file name
    // pantry.db for main pantry, shopping.db for shopping list, "anyname".db to create another pantry
    public DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DB_VERSION);
        this.DB_NAME = databaseName;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBPantry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBPantry.TABLENAME);
        onCreate(db);
    }


    public long insertFood(String foodName, String expiry, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues inValues = new ContentValues();


        inValues.put(DBPantry.COLUMN_FOODNAME, foodName);
        inValues.put(DBPantry.COLUMN_EXPIRY, expiry);
        inValues.put(DBPantry.COLUMN_AMOUNT, amount);

        long id = db.insert(DBPantry.TABLENAME, null, inValues);
        db.close();
        return id;
    }

    public List<DBPantry> fetchPantryAll() {
        List<DBPantry> pantryList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DBPantry.TABLENAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DBPantry pantry = new DBPantry();
                pantry.setName(cursor.getString(cursor.getColumnIndex(DBPantry.COLUMN_FOODNAME)));
                pantry.setDateAdded(cursor.getString(cursor.getColumnIndex(DBPantry.COLUMN_DATEADDED)));
                pantry.setDateExpiry(cursor.getString(cursor.getColumnIndex(DBPantry.COLUMN_EXPIRY)));
                pantry.setAmount(cursor.getInt(cursor.getColumnIndex(DBPantry.COLUMN_AMOUNT)));

                pantryList.add(pantry);

            } while (cursor.moveToNext());
        }
        db.close();
        return pantryList;
    }

    public void updateFood(DBPantry updatedPantry, String oldFoodName, String dateAdded) {
        System.out.println(updatedPantry.getName());
        System.out.println(updatedPantry.getAmount());
        System.out.println(updatedPantry.getDateExpiry());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(DBPantry.COLUMN_FOODNAME, updatedPantry.getName());
        updatedValues.put(DBPantry.COLUMN_AMOUNT, updatedPantry.getAmount());
        updatedValues.put(DBPantry.COLUMN_EXPIRY, updatedPantry.getDateExpiry());

        db.update(DBPantry.TABLENAME, updatedValues, DBPantry.COLUMN_FOODNAME + " = ? AND " + DBPantry.COLUMN_DATEADDED + " = ?", new String[]{oldFoodName, dateAdded});
        db.close();


    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBPantry.TABLENAME, null, null);
    }

    public void deleteFood(String foodName, String dateAdded) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(DBPantry.TABLENAME, DBPantry.COLUMN_FOODNAME + " = ? AND " + DBPantry.COLUMN_DATEADDED + " = ?", new String[]{foodName, dateAdded});
    }

    //run this function to transfer anything from the shopping list table to the main pantry table
    public void transferAllToPantry(Context context) {
        DatabaseHelper shoppingDB = new DatabaseHelper(context, "shopping.db");
        DatabaseHelper pantryDB = new DatabaseHelper(context, "pantry.db");

        List<DBPantry> shoppingList = shoppingDB.fetchPantryAll();

        for (DBPantry x : shoppingList) {
            pantryDB.insertFood(x.getName(), x.getDateExpiry(), x.getAmount());
        }
        shoppingDB.close();
        pantryDB.close();


    }
    public void transferItemToPantry(Context context, String itemName, String dateAdded) {
        DatabaseHelper shoppingDB = this;
        DatabaseHelper pantryDB = new DatabaseHelper(context, "pantry.db");

        List<DBPantry> shoppingList = shoppingDB.fetchPantryAll();
        for (DBPantry x : shoppingList) {
            if (itemName.equals(x.getName()) && dateAdded.equals(x.getDateAdded())) {
                pantryDB.insertFood(x.getName(), x.getDateExpiry(), x.getAmount());
                shoppingDB.deleteFood(itemName, dateAdded);
                break;
            }
        }

        shoppingDB.close();
        pantryDB.close();


    }
}
