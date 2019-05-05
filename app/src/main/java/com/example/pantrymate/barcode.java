package com.example.pantrymate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import database.DBPantry;
import database.DatabaseHelper;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pantrymate.codeScanner.CodeScannerActivity;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class barcode extends AppCompatActivity {


    private RecyclerView nRecyclerView;
    private Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    private DatabaseHelper db1, db;
    ArrayList<Items> itemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        findViewById(R.id.code_scanner)
                .setOnClickListener(v -> startActivity(new Intent(this, CodeScannerActivity.class)));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db1 = new DatabaseHelper(this, "shopping.db");
        db = new DatabaseHelper(this, "pantry.db");
        itemList = getPantry();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String name = bundle.getString("name");
            //expiry and date added
            Format formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String dateString = formatter.format(date);


            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.WEEK_OF_MONTH, 1);

            Date expiryDate = c.getTime();
            String expiryString = formatter.format(expiryDate);

            Intent i = new Intent(barcode.this, editCheck.class);
            Bundle bundleCheck = new Bundle();
            bundleCheck.putString("name", name);
            bundleCheck.putString("expiry", expiryString);
            bundleCheck.putString("quantity", "1");
            bundleCheck.putString("date", dateString);
            bundleCheck.putInt("Add", 1); //adding
            bundleCheck.putInt("pantry", 2);
            bundleCheck.putString("activity", "barcode");
            i.putExtras(bundleCheck);
            startActivity(i);

        }


        nRecyclerView = findViewById(R.id.ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager = new LinearLayoutManager(this);
        nAdapter = new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);
        Button addbut, addAll, helpBut;

        nAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent i = new Intent(barcode.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", itemList.get(position).getText1());
                bundle.putString("expiry", itemList.get(position).getText2());
                bundle.putString("quantity", itemList.get(position).getText3());
                bundle.putString("date", itemList.get(position).getDateAdded());
                bundle.putInt("Add", 2); //updating;
                bundle.putInt("pantry", 2);
                bundle.putString("activity", "barcode");
                i.putExtras(bundle);
                startActivity(i);
            }

        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int i) {
                int position = target.getAdapterPosition();
                removeItem(itemList.get(position).getText1(), itemList.get(position).getDateAdded());
                itemList.remove(position);
                nAdapter.notifyDataSetChanged();

            }
        });
        helper.attachToRecyclerView(nRecyclerView);

        addAll = (Button) findViewById(R.id.addPantry);
        addAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Items tempItem : itemList) {
                        increaseQuantity(tempItem.getText1(),tempItem.getText2(),Integer.parseInt(tempItem.getText3()));
                }
                itemList.clear();
                nAdapter.notifyDataSetChanged();
                Toast.makeText(barcode.this, "Added to pantry", Toast.LENGTH_SHORT).show();
                db1.deleteAll();
            }
        });

        helpBut = (Button) findViewById(R.id.instructions);
        helpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });

        addbut = (Button) findViewById(R.id.addItem);
        addbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(barcode.this, editCheck.class);
                Bundle bundle = new Bundle();
                Format formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                String dateString = formatter.format(date);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.WEEK_OF_MONTH, 1);
                Date expiryDate = c.getTime();
                String expiryString = formatter.format(expiryDate);

                bundle.putString("name", "");
                bundle.putString("expiry", expiryString);
                bundle.putString("quantity", "");
                bundle.putString("date", "");
                bundle.putInt("Add", 1); //adding
                bundle.putInt("pantry", 2);
                bundle.putString("activity", "barcode");
                i.putExtras(bundle);
                startActivity(i);

            }
        });

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

    private ArrayList<Items> getPantry() {
        ArrayList<Items> itemList = new ArrayList<>();
        // returns a list of the pantry
        // use pantryList[x].getVariableName() to get values from the list
        List<DBPantry> pantryList = db1.fetchPantryAll();

        for (DBPantry tempPantry : pantryList) {
            // adds the new food item to the item list
            itemList.add(new Items(tempPantry.getName(), tempPantry.getDateExpiry(), Integer.toString(tempPantry.getAmount()), tempPantry.getDateAdded()));
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
        db1.deleteFood(foodName, dateAdded);

    }

    public void openDialog() {
        barcodeDialog addDialogue = new barcodeDialog();
        addDialogue.show(getSupportFragmentManager(), "help");

    }

    public boolean increaseQuantity(String foodName, String expiry, int amount) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateString = formatter.format(date);
        List<DBPantry> pantryList;
        boolean found = false;

        pantryList = db.fetchPantryAll();

        for (DBPantry tempPantry : pantryList) {
            if (tempPantry.getName().equals(foodName) && tempPantry.getDateExpiry().substring(0, 10).equals(expiry)) {
                DBPantry tempP = new DBPantry();
                tempP.setName(foodName);
                tempP.setDateExpiry(expiry);
                tempP.setAmount(tempPantry.getAmount() + amount);
                db.updateFood(tempP, foodName, tempPantry.getDateAdded());
                found = true;
            }
        }
        if (!found) {
            db.insertFood(foodName, expiry, amount);
        }
        return found;
    }
}