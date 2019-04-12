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

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class ShoppingList extends AppCompatActivity {

    private RecyclerView nRecyclerView;
    private Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    ArrayList<Items> itemList = new ArrayList<>();
    Button addBut,helpBut, scanBut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        itemList.add(new Items("Apple","N/A","2","TBA"));

        nRecyclerView = findViewById(R.id.ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager = new LinearLayoutManager(this);
        nAdapter = new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);


        nAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent i = new Intent(ShoppingList.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", itemList.get(position).getText1());
                bundle.putString("expiry", itemList.get(position).getText2());
                bundle.putString("quantity", itemList.get(position).getText3());
                bundle.putString("date", itemList.get(position).getDateAdded());
                bundle.putInt("Add", 3);
                bundle.putBoolean("tempPantry",true);
                bundle.putString("activity","ShoppingList");
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
                itemList.remove(position);
                nAdapter.notifyDataSetChanged();
                Toast.makeText(ShoppingList.this, "Item removed from shopping list", Toast.LENGTH_SHORT).show();

            }

        });

        ItemTouchHelper helper2 = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int i) {
                int position = target.getAdapterPosition();
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
                bundle.putInt("Add", 3);
                bundle.putBoolean("tempPantry",true);
                bundle.putString("activity","ShoppingList");
                i.putExtras(bundle);
                startActivity(i);

            }
        });

        scanBut = (Button) findViewById(R.id.shopScan);
        scanBut.setOnClickListener(v -> startActivity(new Intent(this, CodeScannerActivity.class)));


        helpBut = (Button) findViewById(R.id.instructions);
        helpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {openDialog(); }
        });
    }

    public void openDialog() {
        shopDialogue shopDialog = new shopDialogue();
        shopDialog.show(getSupportFragmentManager(),"help");

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
