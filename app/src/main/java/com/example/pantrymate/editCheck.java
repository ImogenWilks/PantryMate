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
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import database.DBPantry;
import database.DatabaseHelper;


public class editCheck extends AppCompatActivity {

    private DatabaseHelper db,db1,db2,db3,db4;
    private EditText cname,cexpiry,cquantity;
    private Button submitBtn;
    private ImageButton cameraBtn,shoppingBtn,barcodeBtn,receiptBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_check);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHelper(this, "pantry.db");
        db1 = new DatabaseHelper(this, "shopping.db"); //barcode
        db2 = new DatabaseHelper(this,"list.db");
        db3 = new DatabaseHelper(this,"receipt.db");
        db4 = new DatabaseHelper(this,"Camera.db");

        Bundle bundle = getIntent().getExtras();


        final String name = bundle.getString("name");
        String expiry = bundle.getString("expiry");
        String quantity = bundle.getString("quantity");
        final String dateAdded = bundle.getString("date");
        Integer add =bundle.getInt("Add");
        Integer pantry = bundle.getInt("pantry");
        String activity = bundle.getString("activity");

        EditText cname = findViewById(R.id.changeName);
        EditText cexpiry = findViewById(R.id.changeExpiry);
        if (pantry==3){ cexpiry.setEnabled(false);}
        EditText cquantity = findViewById(R.id.changeQuantity);


        cname.setText(name);
        cexpiry.setText(expiry);
        cquantity.setText(quantity);

        cameraBtn=findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(editCheck.this, Camera.class);
                startActivity(i);
            }
        });

        shoppingBtn=findViewById(R.id.shoppingBtn);
        shoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(editCheck.this, ShoppingList.class);
                startActivity(i);
            }
        });

        receiptBtn=findViewById(R.id.receiptBtn);
        receiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(editCheck.this, receipt.class);
                startActivity(i);
            }
        });

        barcodeBtn=findViewById(R.id.barcodeBtn);
        barcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(editCheck.this, barcode.class);
                startActivity(i);
            }
        });

        submitBtn = findViewById(R.id.changeItem);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String changeExpiry;
                EditText newName = findViewById(R.id.changeName);
                EditText newExpiry = findViewById(R.id.changeExpiry);
                EditText newQuantity = findViewById(R.id.changeQuantity);

                String changeName=newName.getText().toString();

                if (pantry==3){changeExpiry="N/A";}
                else{ changeExpiry=newExpiry.getText().toString();}
                String changeQuantity=newQuantity.getText().toString();

                Pair<Integer, Boolean> pair = validateChanges(changeName, changeExpiry, changeQuantity,pantry);
                if (pair.second)
                {
                    if (add==1){addItem(changeName,changeExpiry,pair.first,pantry);}

                    else if (add==2) {updateFood(changeName, changeExpiry, pair.first, name, dateAdded,pantry);}

                    if (activity.equals("barcode"))
                    {
                        Intent i = new Intent(editCheck.this, barcode.class);
                        startActivity(i);
                    }
                    else if(activity.equals("Edit"))
                    {
                        Intent i = new Intent(editCheck.this, Edit.class);
                        startActivity(i);
                    }
                    else if (activity.equals("camera")){
                        Intent i = new Intent(editCheck.this, Camera.class);
                        startActivity(i);
                    }
                    else if (activity.equals("receipt")){
                        Intent i = new Intent(editCheck.this,receipt.class);
                        startActivity(i);
                    }
                    else
                     {
                        Intent i = new Intent(editCheck.this, ShoppingList.class);
                        startActivity(i);
                     }


                }


            }
        });
    }

    private Pair<Integer,Boolean> validateChanges(String updatedFoodName, String updatedExpiry, String updatedAmount, Integer pantry){
        String errorMessage="";
        Pair<Integer, Boolean> pair;
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.DAY_OF_MONTH,-1);
        Date todaysDate = calender.getTime();
        Date date = new Date();

        int intQuantity=0;

        if (updatedAmount.length()>3){errorMessage="The item quantity can not exceed 999";}
        else if  (updatedAmount.length()!=0) { intQuantity = Integer.parseInt(updatedAmount); }

        if (updatedFoodName.length()==0 || updatedExpiry.length()==0 || updatedAmount.length()==0){ errorMessage="Please do not leave any fields blank"; }

        else if (! Pattern.matches("^[A-Za-z0-9 _]*[A-Za-z0-9][A-Za-z0-9 _]*$",updatedFoodName)) { errorMessage="Please only enter letters into the Name field"; }


        if (pantry != 3) {
            if (!Pattern.matches("^(0?[1-9]|[12][0-9]|3[01])[/](0?[1-9]|1[012])[/]\\d{4}$", updatedExpiry)) {
                errorMessage = "Please enter the date in the format DD/MM/YYYY, including the forward slashes";
            }
            else {
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy").parse(updatedExpiry);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (todaysDate.after(date) && todaysDate.equals(date) == false) {
                errorMessage = "Please do not enter food that has already expired";
            }
        }

        if (errorMessage==""){
            pair = new Pair<>(intQuantity,true);
            return pair ; }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        pair = new Pair<>(0,false);
        return pair;
    }

    private void updateFood(String updatedFoodName, String updatedExpiry, int updatedAmount, String oldFoodName, String dateAdded,int pantry) {
        // pass the new variable edits along with its old food name and the date it was added
        DBPantry tempPantry = new DBPantry();

        tempPantry.setName(updatedFoodName);
        tempPantry.setDateExpiry(updatedExpiry);
        tempPantry.setAmount(updatedAmount);

        if (pantry==1){db.updateFood(tempPantry,oldFoodName,dateAdded);}
        else if (pantry ==2){ db1.updateFood(tempPantry, oldFoodName, dateAdded);}
        else if (pantry==3){db2.updateFood(tempPantry,oldFoodName,dateAdded);}
        else if (pantry==4) {db3.updateFood(tempPantry,oldFoodName,dateAdded);}
        else{db4.updateFood(tempPantry,oldFoodName,dateAdded);}
    }

    private void addItem(String foodName, String expiry, int amount,int pantry) {
        boolean found = increaseQuantity(foodName,expiry,amount,pantry);

        if (!found){
        if (pantry==1) { db.insertFood(foodName,expiry,amount);}
        else if (pantry==2){ db1.insertFood(foodName, expiry, amount);}
        else if (pantry==3){db2.insertFood(foodName,expiry,amount);}
        else if (pantry==4) {db3.insertFood(foodName,expiry,amount);}
        else{db4.insertFood(foodName,expiry,amount);}
        }
    }


    public boolean increaseQuantity(String foodName, String expiry, int amount,int pantry) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateString=formatter.format(date);
        List<DBPantry> pantryList;
        boolean found = false;

        if (pantry==1){pantryList = db.fetchPantryAll();}
        else if (pantry==2){pantryList =db1.fetchPantryAll();}
        else if (pantry==3){pantryList =db2.fetchPantryAll();}
        else if (pantry==4){pantryList =db3.fetchPantryAll();}
        else{pantryList=db4.fetchPantryAll();}

        for (DBPantry tempPantry : pantryList) {
            if (tempPantry.getName().equals(foodName) && tempPantry.getDateExpiry().substring(0,10).equals(expiry)) {
                updateFood(foodName, expiry, tempPantry.getAmount() + amount, foodName, tempPantry.getDateAdded(), pantry);
                found=true;
            }
        }
        return found;


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
