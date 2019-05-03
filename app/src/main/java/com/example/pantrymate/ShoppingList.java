package com.example.pantrymate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.pantrymate.codeScanner.CodeScannerActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import static com.example.pantrymate.R.id.ListRecyclerView;

public class ShoppingList extends AppCompatActivity {

    private RecyclerView nRecyclerView;
    private Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    private DatabaseHelper db,db1;

    Button addBut, helpBut, scanBut;
    String itemName;

    public ArrayList<Items> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHelper(this, "pantry.db");
        db1 = new DatabaseHelper(this, "list.db");

        itemList = getPantry();

        nRecyclerView = findViewById(ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager = new LinearLayoutManager(this);
        nAdapter = new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            itemName = bundle.getString("name");
            tickOff(itemName);
        }





        nAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent i = new Intent(ShoppingList.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", itemList.get(position).getText1());
                bundle.putString("expiry", itemList.get(position).getText2());
                bundle.putString("quantity", itemList.get(position).getText3());
                bundle.putString("date", itemList.get(position).getDateAdded());
                bundle.putInt("Add", 2);
                bundle.putInt("pantry", 3);
                bundle.putString("activity", "ShoppingList");
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
                db1.deleteFood(itemList.get(position).getText1(),itemList.get(position).getDateAdded());
                itemList.remove(position);
                nAdapter.notifyDataSetChanged();
                Toast.makeText(ShoppingList.this, "Item removed from shopping list", Toast.LENGTH_SHORT).show();


            }

        });

        ItemTouchHelper helper2 = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int i) {
                int position = target.getAdapterPosition();
                db1.transferItemToPantry(ShoppingList.this, itemList.get(position).getText1(),itemList.get(position).getDateAdded());
                itemList.remove(position);
                nAdapter.notifyDataSetChanged();
                Toast.makeText(ShoppingList.this, "Item added to pantry", Toast.LENGTH_SHORT).show();


            }

        });


        helper.attachToRecyclerView(nRecyclerView);
        helper2.attachToRecyclerView(nRecyclerView);

        addBut = (Button) findViewById(R.id.addItem);
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(ShoppingList.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "");
                bundle.putString("expiry", "");
                bundle.putString("quantity", "");
                bundle.putString("date", "");
                bundle.putInt("Add", 1);
                bundle.putInt("pantry", 3);
                bundle.putString("activity", "ShoppingList");
                i.putExtras(bundle);
                startActivity(i);

            }
        });

        scanBut = (Button) findViewById(R.id.shopScan);
        scanBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ShoppingList.this, CodeScannerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("comingFromShopping", true);
                i.putExtras(bundle);
                startActivity(i);
            }
        });


        helpBut = (Button) findViewById(R.id.instructions);
        helpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    private ArrayList<Items> getPantry() {
        ArrayList<Items> itemList = new ArrayList<>();
        // returns a list of the pantry
        // use pantryList[x].getVariableName() to get values from the list
        List<DBPantry> pantryList = db1.fetchPantryAll();

        for (DBPantry tempPantry : pantryList) {
            // adds the new food item to the item list
            itemList.add(new Items(tempPantry.getName(),tempPantry.getDateExpiry(),Integer.toString(tempPantry.getAmount()), tempPantry.getDateAdded()));
        }

        return itemList;
    }

    public void tickOff(String name)
    {

        String[] splitName = name.split("\\s+");
        String[] splitExisting;
        int matches;
        boolean exactMatch = false;
        int pos = 0;
        Vector<Integer> matchCount = new Vector<>();
        for (Items item : itemList)
        {
            matches = 0;

            if (name.toLowerCase().replaceAll("\\p{Punct}", "").equals(item.getText1().toLowerCase().replaceAll("\\p{Punct}", ""))) {
                exactMatch = true;
                exactMatchLocated(pos);
                break;
            }

            else
            {
                splitExisting = item.getText1().split("\\s+");
                for (int word = 0; word < splitExisting.length; word++)
                {
                    for (int i = 0; i < splitName.length; i++)
                    {
                        if (splitExisting[word].toLowerCase().replaceAll("\\p{Punct}", "").equals(splitName[i].toLowerCase().replaceAll("\\p{Punct}", ""))) {
                            matches += 1;
                        }
                    }
                }

                matchCount.add(matches);
            }
            pos +=1;
        }

        if (!exactMatch)
        {
            findHighestMatchCount(matchCount);
        }

    }


    private void exactMatchLocated(int index)
    {

        String foodName=itemList.get(index).getText1();
        String dateAdded=itemList.get(index).getDateAdded();
        String quantity=itemList.get(index).getText3();
        int intQuantity= Integer.parseInt(quantity);
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String dateString=formatter.format(date);


        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.WEEK_OF_MONTH, 1);

        Date expiryDate=c.getTime();
        String expiryString=formatter.format(expiryDate);

        if (intQuantity > 1)
        {
            DBPantry tempPantry = new DBPantry();

            tempPantry.setAmount(intQuantity-1);
            tempPantry.setName(foodName);
            tempPantry.setDateExpiry("N/A");
            tempPantry.setDateAdded(dateAdded);
            db1.updateFood(tempPantry,foodName,dateAdded);
            db.insertFood(foodName,expiryString,1);

            Items tempItem=itemList.get(index);
            String storeQuantity=tempItem.getText3();

            int tempQuantity=Integer.parseInt(storeQuantity);
            tempQuantity-=1;
            storeQuantity=Integer.toString(tempQuantity);

            itemList.get(index).changeQuantity(storeQuantity);
            nAdapter.notifyDataSetChanged();

        }

        else
        {
            itemList.remove(index);
            db1.deleteFood(foodName, dateAdded);
            nAdapter.notifyDataSetChanged();
            db.insertFood(foodName, expiryString, intQuantity);
        }

    }

    public void findHighestMatchCount(Vector<Integer> matchCount)
    {
        Vector<Integer> bestMatches = new Vector<>();
        int highest = 0;
        for (int i = 0; i < matchCount.size(); i++)
        {
            System.out.println(matchCount.get(i));
            if (matchCount.get(i) != 0 && matchCount.get(i) > highest)
            {
                bestMatches.clear();
                bestMatches.add(i);
                highest = matchCount.get(i);
            }

            else if (matchCount.get(i) != 0 && matchCount.get(i) == highest && bestMatches.size() <= 5)
            {
                bestMatches.add(i);
            }
        }

        bundleBestMatches(bestMatches,matchCount);


    }

    public void bundleBestMatches(Vector<Integer> bestMatches, Vector<Integer> matchCount)
    {
        String[] items = new String[20];
        int pos = 0;
        int item=0;
        Iterator value = bestMatches.iterator();
        if (!bestMatches.isEmpty())
        {
            while (value.hasNext())
            {
                System.out.println("BEST MATCH: " + itemList.get(bestMatches.get(item)).getText1());
                System.out.println("WITH MATCHES:  " + matchCount.get(bestMatches.get(item)));
                items[pos] = itemList.get(bestMatches.get(item)).getText1();
                items[pos + 1] = itemList.get(bestMatches.get(item)).getText2();
                items[pos + 2] = itemList.get(bestMatches.get(item)).getText3();
                items[pos + 3] = itemList.get(bestMatches.get(item)).getDateAdded();

                pos += 4;
                item+=1;
                value.next();
            }

            Intent i = new Intent(ShoppingList.this, partialMatch.class);
            Bundle bundle = new Bundle();
            bundle.putStringArray("itemList", items);
            bundle.putInt("numMatches",bestMatches.size());
            i.putExtras(bundle);
            startActivity(i);
        }

        else
        {
            System.out.println("NO MATCHES FOUND");
            openDialogNoMatch();
        }

    }



    public void openDialog() {
        shopDialogue shopDialog = new shopDialogue();
        shopDialog.show(getSupportFragmentManager(),"help");

    }

    public void openDialogNoMatch() {
        matchesDialog matchesDialogue = new matchesDialog();
        Bundle args = new Bundle();
        args.putString("name",itemName);
        matchesDialogue.setArguments(args);
        matchesDialogue.show(getSupportFragmentManager(),"No Matches");

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

}
