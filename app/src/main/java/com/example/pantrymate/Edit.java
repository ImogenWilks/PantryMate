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

import java.util.ArrayList;

public class Edit extends AppCompatActivity {

    private RecyclerView nRecyclerView;
    private RecyclerView.Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<Items> itemList = new ArrayList<>();
        itemList.add(new Items("Apple", "Line 2"));
        itemList.add(new Items("Grapes","test2"));
        nRecyclerView=findViewById(R.id.ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager= new LinearLayoutManager(this);
        nAdapter= new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);

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
}

