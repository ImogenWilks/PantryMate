package com.example.pantrymate;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar.OnMenuItemClickListener;
import android.widget.Button;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import database.DBPantry;
import database.DatabaseHelper;

public class Edit extends AppCompatActivity implements AddDialogue.addDialogListener {

    private RecyclerView nRecyclerView;
    private Adapter nAdapter;
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

        nAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent i = new Intent(Edit.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name",itemList.get(position).getText1());
                bundle.putString("expiry",itemList.get(position).getText2());
                bundle.putString("quantity",itemList.get(position).getText3());
                bundle.putString("date",itemList.get(position).getDateAdded());
                i.putExtras(bundle);
                startActivity(i);

            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int i) {
                int position = target.getAdapterPosition();
                removeItem(itemList.get(position).getText1(),itemList.get(position).getDateAdded());
                itemList.remove(position);
                nAdapter.notifyDataSetChanged();

            }
        });
        helper.attachToRecyclerView(nRecyclerView);

        addBut = (Button) findViewById(R.id.addItem);
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

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
            itemList.add(new Items(tempPantry.getName(),tempPantry.getDateExpiry(),Integer.toString(tempPantry.getAmount()), tempPantry.getDateAdded()));
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


