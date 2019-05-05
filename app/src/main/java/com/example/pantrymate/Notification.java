package com.example.pantrymate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import database.DBPantry;
import database.DatabaseHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Notification extends AppCompatActivity {

    private RecyclerView nRecyclerView;
    private Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    private DatabaseHelper db;
    ArrayList<Items> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this, "pantry.db");


        try {
            itemList = getPantry();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        nRecyclerView = findViewById(R.id.ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager = new LinearLayoutManager(this);
        nAdapter = new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);
    }



    private ArrayList<Items> getPantry() throws ParseException {
        ArrayList<Items> itemList = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        List<DBPantry> pantryList = db.fetchPantryAll();
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH,-1);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH,3);
        Date dateExpire;

        for (DBPantry tempPantry : pantryList) {
            dateExpire = formatter.parse(tempPantry.getDateExpiry());

            if (dateExpire.before(maxDate.getTime()) && dateExpire.after(today.getTime())) {
                itemList.add(new Items(tempPantry.getName(), tempPantry.getDateExpiry(), Integer.toString(tempPantry.getAmount()), tempPantry.getDateAdded()));

            }
        }
        return itemList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Home:
                Intent intentHome = new Intent(this, MainActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.Manual:
                Intent intentManual = new Intent(this, Edit.class);
                startActivity(intentManual);
                return true;
            case R.id.Camera:
                Intent intentCamera = new Intent(this, Camera.class);
                startActivity(intentCamera);
                return true;
            case R.id.Barcode:
                Intent intentBarcode = new Intent(this, barcode.class);
                startActivity(intentBarcode);
                return true;
            case R.id.ShoppingList:
                Intent intentShopping = new Intent(this, ShoppingList.class);
                startActivity(intentShopping);
                return true;
            case R.id.Receipt:
                Intent intentReceipt = new Intent(this, receipt.class);
                startActivity(intentReceipt);
                return true;
            case R.id.Help:
                Intent intentHelp = new Intent(this, Help.class);
                startActivity(intentHelp);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
