package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DBPantry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DBPantry.TABLENAME);
        onCreate(db);
    }



    public long insertFood(String foodName, String expiry, int amount)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues inValues = new ContentValues();


        inValues.put(DBPantry.COLUMN_FOODNAME, foodName);
        inValues.put(DBPantry.COLUMN_EXPIRY, expiry);
        inValues.put(DBPantry.COLUMN_AMOUNT, amount);

        long id = db.insert(DBPantry.TABLENAME, null, inValues);
        db.close();
        return id;
    }

    public List<DBPantry> fetchPantryAll()
    {
        List<DBPantry> pantryList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DBPantry.TABLENAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            do
            {
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
}
