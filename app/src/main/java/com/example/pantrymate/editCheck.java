package com.example.pantrymate;

import android.content.Intent;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import database.DBPantry;
import database.DatabaseHelper;


public class editCheck extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText cname,cexpiry,cquantity;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_check);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHelper(this);

        Bundle bundle = getIntent().getExtras();
        final String name = bundle.getString("name");
        String expiry = bundle.getString("expiry");
        String quantity = bundle.getString("quantity");
        final String dateAdded = bundle.getString("date");

        EditText cname = findViewById(R.id.changeName);
        EditText cexpiry = findViewById(R.id.changeExpiry);
        EditText cquantity = findViewById(R.id.changeQuantity);


        cname.setText(name);
        cexpiry.setText(expiry);
        cquantity.setText(quantity);

        submitBtn = findViewById(R.id.changeItem);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText newName = findViewById(R.id.changeName);
                EditText newExpiry = findViewById(R.id.changeExpiry);
                EditText newQuantity = findViewById(R.id.changeQuantity);

                String changeName=newName.getText().toString();
                String changeExpiry=newExpiry.getText().toString();
                String changeQuantity=newQuantity.getText().toString();

                Pair<Integer, Boolean> pair = validateChanges(changeName, changeExpiry, changeQuantity);
                if (pair.second) {

                    updateFood(changeName, changeExpiry, pair.first ,name, dateAdded);

                    Intent i = new Intent(editCheck.this, Edit.class);
                    startActivity(i);
                }


            }
        });
    }

    private Pair<Integer,Boolean> validateChanges(String updatedFoodName, String updatedExpiry, String updatedAmount){
        String errorMessage="";
        Pair<Integer, Boolean> pair;

        if (updatedFoodName.length()==0 || updatedExpiry.length()==0 || updatedAmount.length()==0){
            errorMessage="Please do not leave any fields blank";
        }

        if (! Pattern.matches(".*[a-zA-Z]+.*",updatedFoodName)) {
            errorMessage="Please only enter letters into the Name field";
        }

        if (! Pattern.matches("^(0?[1-9]|[12][0-9]|3[01])[/](0?[1-9]|1[012])[/]\\d{4}$", updatedExpiry)){
            errorMessage="Please enter the date in the format DD/MM/YYYY, including the forward slashes";
        }

        if (errorMessage==""){
            int intQuantity = Integer.parseInt(updatedAmount);
            pair = new Pair<>(intQuantity,true);

            return pair ;
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        pair = new Pair<>(0,false);
        return pair;
    }

    private void updateFood(String updatedFoodName, String updatedExpiry, int updatedAmount, String oldFoodName, String dateAdded) {
        // pass the new variable edits along with its old food name and the date it was added
        DBPantry tempPantry = new DBPantry();
        //int intQuantity =  Integer.parseInt(updatedAmount);
        //put updated values here
        tempPantry.setName(updatedFoodName);
        tempPantry.setDateExpiry(updatedExpiry);
        tempPantry.setAmount(updatedAmount);

        db.updateFood(tempPantry, oldFoodName, dateAdded);
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
