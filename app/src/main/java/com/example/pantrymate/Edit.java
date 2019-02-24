package com.example.pantrymate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;

import java.util.List;
import database.DBPantry;
import database.DatabaseHelper;

public class Edit extends AppCompatActivity implements AddDialogue.addDialogListener {

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
        //db is the database helper object
        db = new DatabaseHelper(this);

        itemList = getPantry();

        nRecyclerView = findViewById(R.id.ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager = new LinearLayoutManager(this);
        nAdapter = new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);


        addBut = (Button) findViewById(R.id.addItem);
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
                //pass food name, expiry and amount. Date added auto generated using sql
                // addItem("Carrot", "19/2/15", 4);
                //itemList = updateList(getPantry());

            }
        });

        editBut = (Button) findViewById(R.id.editItem);
        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pass updated food details, pass pantry.getVariableName() for any not to be updated (e.g. updating food pass food name, but leave amount)
                updateFood("Burger", "12/4/45", 34, "Banana", "2019-02-21 11:48:17");
                itemList = updateList(getPantry());
                // getPantry();
            }
        });
        removeBut = (Button) findViewById(R.id.removeItem);
        removeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pass food name and date added
                removeItem("Carrot", "2019-02-21 11:48:17");
                itemList = updateList(getPantry());
                ;
            }
        });

    }

    public void openDialog() {
        AddDialogue addDialogue = new AddDialogue();
        addDialogue.show(getSupportFragmentManager(), "dialog");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case R.id.Help:
                Intent intentHelp = new Intent(this, Help.class);
                startActivity(intentHelp);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addItem(String foodName, String expiry, int amount) {
        // inserts an item into the table
        db.insertFood(foodName, expiry, amount);


    }

    private ArrayList<Items> getPantry() {
        ArrayList<Items> itemList = new ArrayList<>();
        // returns a list of the pantry
        // use pantryList[x].getVariableName() to get values from the list
        List<DBPantry> pantryList = db.fetchPantryAll();

        for (DBPantry tempPantry : pantryList) {
            // adds the new food item to the item list
            itemList.add(new Items(tempPantry.getName(), "Expires: " + tempPantry.getDateExpiry(), "Amount: " + Integer.toString(tempPantry.getAmount()), tempPantry.getDateAdded()));
        }

        return itemList;
    }

    private ArrayList<Items> updateList(ArrayList<Items> oldList) {
        // updates the list recyclerview with  any changes (additions, removals, edits etc).
        if (oldList != null && oldList.size() > 0) {
            itemList.clear();
            itemList.addAll(oldList);
            nAdapter.notifyDataSetChanged();
        }
        return itemList;

    }

    private void updateFood(String updatedFoodName, String updatedExpiry, int updatedAmount, String oldFoodName, String dateAdded) {
        // pass the new variable edits along with its old food name and the date it was added
        DBPantry tempPantry = new DBPantry();

        //put updated values here
        tempPantry.setName(updatedFoodName);
        tempPantry.setDateExpiry(updatedExpiry);
        tempPantry.setAmount(updatedAmount);
        db.updateFood(tempPantry, oldFoodName, dateAdded);
    }

    private void removeItem(String foodName, String dateAdded) {
        //removes the item from the table, pass food name and dateadded
        db.deleteFood(foodName, dateAdded);

    }

    @Override
    public void addItems(String name, String quantity, String expiry) {
        int intQuantity = Integer.parseInt(quantity); //read in as string parsed to int
        addItem(name, expiry , intQuantity);
        itemList = updateList(getPantry());
    }
}


