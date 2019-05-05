package database;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class DBPantry  {
    public static final String TABLENAME = "pantryTable";
    public static final String COLUMN_FOODNAME = "foodName";
    public static final String COLUMN_DATEADDED = "dateAdded";
    public static final String COLUMN_EXPIRY = "dateExpire";
    public static final String COLUMN_AMOUNT = "amount";

    private String foodName;
    private String dateAdded;
    private String dateExpiry;
    private int amount;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLENAME + "("
            + COLUMN_FOODNAME + " TEXT, "
            + COLUMN_DATEADDED + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + COLUMN_EXPIRY + " DATETIME, "
            + COLUMN_AMOUNT + " INT "
            + ")";

    public DBPantry(){};
    public DBPantry (String name, String added, String expiry, int amount)
    {
        this.foodName = name;
        this.dateAdded = added;
        this.dateExpiry = expiry;
        this.amount = amount;
    }

    public String getName() {return foodName;}
    public void setName(String name){ this.foodName = name;}

    public String getDateAdded () {return dateAdded;}
    public void setDateAdded (String added) { this.dateAdded = added;}

    public String getDateExpiry () {return dateExpiry;}
    public void setDateExpiry(String expiry) {this.dateExpiry = expiry;}

    public int getAmount () {return amount;}
    public void setAmount (int amount) { this.amount = amount;}
}
