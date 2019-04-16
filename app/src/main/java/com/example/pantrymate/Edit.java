package com.example.pantrymate;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import java.util.ArrayList;

import java.util.List;
import database.DBPantry;
import database.DatabaseHelper;

public class Edit extends AppCompatActivity  {

    private RecyclerView nRecyclerView;
    private Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    private Button addBut, helpBut;
    private DatabaseHelper db;

    ArrayList<Items> itemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //db is the database helper object
        db = new DatabaseHelper(this, "pantry.db");

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
                bundle.putInt("Add",2);
                bundle.putInt("pantry",1);
                bundle.putString("activity","Edit");
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

        helpBut= (Button) findViewById(R.id.instructions);
        helpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });

        addBut = (Button) findViewById(R.id.addItem);
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(Edit.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name","");
                bundle.putString("expiry","");
                bundle.putString("quantity","");
                bundle.putString("date","");
                bundle.putInt("Add",1);
                bundle.putInt("pantry",1);
                bundle.putString("activity","Edit");
                i.putExtras(bundle);
                startActivity(i);


            }
        });
        
    }

    public void openDialog() {
        AddDialogue addDialogue = new AddDialogue();
        addDialogue.show(getSupportFragmentManager(),"help");

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


}


