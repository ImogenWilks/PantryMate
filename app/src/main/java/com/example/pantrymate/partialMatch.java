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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class partialMatch extends AppCompatActivity {


    private RecyclerView nRecyclerView;
    private Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    private DatabaseHelper db,db1;

    ArrayList<Items> itemList = new ArrayList<>();
    String[] item;
    int numMatches=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partial_match);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nRecyclerView = findViewById(R.id.ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager = new LinearLayoutManager(this);
        nAdapter = new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);

        db = new DatabaseHelper(this, "pantry.db");
        db1 = new DatabaseHelper(this, "list.db");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            item=bundle.getStringArray("itemList");
            numMatches=bundle.getInt("numMatches");
            populateItemList(item);

        }




        nAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String foodName=itemList.get(position).getText1();
                String dateAdded=itemList.get(position).getDateAdded();
                String quantity=itemList.get(position).getText3();
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

                    Items tempItem=itemList.get(position);
                    String storeQuantity=tempItem.getText3();

                    int tempQuantity=Integer.parseInt(storeQuantity);
                    tempQuantity-=1;
                    storeQuantity=Integer.toString(tempQuantity);

                    itemList.get(position).changeQuantity(storeQuantity);
                    nAdapter.notifyDataSetChanged();

                }

                else
                {
                    itemList.remove(position);
                    db1.deleteFood(foodName, dateAdded);
                    nAdapter.notifyDataSetChanged();
                    db.insertFood(foodName, expiryString, intQuantity);
                }

                Intent i = new Intent(partialMatch.this,ShoppingList.class);
                startActivity(i);

            }
        });
    }

    public void populateItemList(String[] item)
    {
        int pos=0;
        for (int i=0; i < numMatches; i++)
        {
            itemList.add(new Items(item[pos],item[pos+1],item[pos+2],item[pos+3]));
            pos+=4;
        }
        nAdapter.notifyDataSetChanged();
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