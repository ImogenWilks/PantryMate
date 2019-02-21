package com.example.pantrymate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar.OnMenuItemClickListener;
import android.widget.Button;


import java.util.ArrayList;

import java.util.List;
import database.DBPantry;
import database.DatabaseHelper;

public class Edit extends AppCompatActivity {

    private RecyclerView nRecyclerView;
    private RecyclerView.Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    private Button addBut, editBut, removeBut;
    private DatabaseHelper db;
    ArrayList<Items> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHelper(this);

        itemList = getPantry();

        nRecyclerView=findViewById(R.id.ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager= new LinearLayoutManager(this);
        nAdapter= new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);


        addBut  = (Button) findViewById(R.id.addItem);
        addBut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addItem("Banana", "19/2/15", 4);
                itemList = updateList(getPantry());

               // getPantry(itemList);
            }
        });

        editBut  = (Button) findViewById(R.id.editItem);
        editBut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateFood("Burger","12/4/45", 34, "Banana", "2019-02-21 11:48:17" );
                itemList = updateList(getPantry());
               // getPantry();
            }
        });
       removeBut  = (Button) findViewById(R.id.removeItem);
        removeBut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                removeItem("Burger", "2019-02-21 11:48:17");
                itemList = updateList(getPantry());
                // getPantry();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
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
            case R.id.Help:
                Intent intentHelp = new Intent(this, Help.class);
                startActivity(intentHelp);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addItem(String foodName, String expiry, int amount)
    {
        long id = db.insertFood(foodName, expiry, amount);


        System.out.print(id);

    }

    private ArrayList<Items> getPantry()
    {
        ArrayList<Items> itemList = new ArrayList<>();
        // returns a list of the pantry
        // use pantryname[x].getValue() to get values from the list (replace Value with variable name)
        List <DBPantry> pantryList = db.fetchPantryAll();

        for(DBPantry tempPantry : pantryList)
        {
            System.out.println(tempPantry.getName() + " | " + tempPantry.getDateExpiry() + " | " + tempPantry.getAmount());
            itemList.add(new Items(tempPantry.getName(), Integer.toString(tempPantry.getAmount())));
        }

        return itemList;
    }
    private ArrayList<Items> updateList(ArrayList<Items> oldList)
    {
        if (oldList != null && oldList.size() > 0) {
            itemList.clear();
            itemList.addAll(oldList);
            nAdapter.notifyDataSetChanged();
        }
        return itemList;

    }
    private void updateFood(String updatedFoodName, String updatedExpiry, int updatedAmount, String oldFoodName, String dateAdded)
    {
        DBPantry tempPantry = new DBPantry();
        System.out.println("test");

        //put updated values here
        tempPantry.setName(updatedFoodName);
        tempPantry.setDateExpiry(updatedExpiry);
        tempPantry.setAmount(updatedAmount);
        db.updateFood(tempPantry, oldFoodName, dateAdded);
    }

    private void removeItem(String foodName, String dateAdded)
    {
        db.deleteFood(foodName, dateAdded);
    }
}

