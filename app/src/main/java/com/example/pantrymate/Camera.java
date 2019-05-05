package com.example.pantrymate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import database.DBPantry;
import database.DatabaseHelper;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.lang.reflect.Array;
import java.net.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Camera extends AppCompatActivity {
    public String visionData;
    int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap image = null;
    int port = 3000;
    EditText itemListTextView;
    String currentPhotoPath = null;
    File imageFile = null;

    //Used to filter out non food items from vision results
    List<String> foodList = Arrays.asList("Apple","Banana","Grape","Cucumber","Pineapple","Carrot","Tomato","Lettuce","Onion","Bell Pepper");
    //Stores results of object recognition
    ArrayList<String> visionResultsList = new ArrayList<String>();
    //Used to determine when all results have been recieved
    volatile int numOfResponses = 0;
    volatile  boolean recievedText = false;
    Button addBut,addAll,helpBut;
    volatile String textResults = "";
    private RecyclerView nRecyclerView;
    private Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    private DatabaseHelper db,db1;
    ArrayList<Items> itemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHelper(this, "Camera.db");
        db1 = new DatabaseHelper(this,"pantry.db");

        itemList = getPantry();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Toast.makeText(Camera.this, "Processing Complete", Toast.LENGTH_SHORT).show();

        }

        nRecyclerView = findViewById(R.id.ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager = new LinearLayoutManager(this);
        nAdapter = new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);

        nAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent i = new Intent(Camera.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", itemList.get(position).getText1());
                bundle.putString("expiry", itemList.get(position).getText2());
                bundle.putString("quantity", itemList.get(position).getText3());
                bundle.putString("date", itemList.get(position).getDateAdded());
                bundle.putInt("Add", 2); //updating;
                bundle.putInt("pantry",5);
                bundle.putString("activity","camera");
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
                Format formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                String dateString=formatter.format(date);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.WEEK_OF_MONTH, 1);
                Date expiryDate=c.getTime();
                String expiryString=formatter.format(expiryDate);


                Intent i = new Intent(Camera.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name","");
                bundle.putString("expiry",expiryString);
                bundle.putString("quantity","");
                bundle.putString("date","");
                bundle.putInt("Add",1);
                bundle.putInt("pantry",5);
                bundle.putString("activity","camera");
                i.putExtras(bundle);
                startActivity(i);


            }
        });

        addAll = (Button) findViewById(R.id.addPantry);
        addAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Items tempItem : itemList){
                    increaseQuantity(tempItem.getText1(),tempItem.getText2(),Integer.parseInt(tempItem.getText3()));
                }

                itemList.clear();
                nAdapter.notifyDataSetChanged();
                db.deleteAll();
                Toast.makeText(Camera.this, "Added to pantry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void 	onPostResume()
    {
        super.onPostResume();
    }

    private File createImageFile() throws IOException {

            // Create an image file name
            String imageFileName =  "PantryMate";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Note:
    //Call this when you want to open the camera
    public void dispatchCameraIntent()
    {
        //Opens the camera in the app
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try
                {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.pantrymate.fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                    //Stores the image file outside the scope of the function so it can be deleted once the image has been processed
                    imageFile = photoFile;
                }
                catch (Exception ex)
                {
                    saveToFile(ex.toString());
                }

            }
        }
    }

    //Called when the capture button is pressed
    public void onCapturePressed(View view)
    {
        dispatchCameraIntent();
    }

    @SuppressLint("StaticFieldLeak")
    public void segmentImage(Bitmap img)
    {
        Toast.makeText(Camera.this, "Processing", Toast.LENGTH_SHORT).show();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);

        numOfResponses = 0;
        recievedText = false;
        textResults = "";
        visionResultsList = new ArrayList<String>();

        detectObjects(image);
        detectText(image);

        if (img.getWidth() * img.getHeight() > 3000000)
        {
            img = Bitmap.createScaledBitmap(img, img.getWidth() / 2, img.getHeight() / 2, true);
        }

        int incX = img.getWidth() / 5;
        int incY = img.getHeight() / 5;

        int fileCount = 1;

        for (int x1 = 0; x1 < img.getWidth() - incX; x1 += incX)
        {
            for (int y1 = 0; y1 < img.getHeight() - incY; y1 += incY)
            {
                Bitmap currentImage = Bitmap.createBitmap(incX, incY, Bitmap.Config.ARGB_8888);
                for (int x = 0; x < currentImage.getWidth(); x++)
                {
                    for (int y = 0; y < currentImage.getHeight(); y++)
                    {
                        try
                        {
                            currentImage.setPixel(x,y, img.getPixel(x + x1,y + y1));
                        }
                        catch (Exception ex)
                        {
                            saveToFile(ex.toString());
                        }
                    }
                }
                detectObjects(FirebaseVisionImage.fromBitmap(currentImage));

            }
        }

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void ...params) {
                int timer = 0;
                //Waits until its recieved responses for all image segments
                //Times out after 15 seconds
                while (numOfResponses < 26 && timer < 25)
                {
                    try {
                        CharSequence newchars = numOfResponses + " / 26";
                        Thread.sleep(1000);
                        timer++;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }

                //Note:
                //Used to display on screen in text view, you can remove this
                String data = "Response:\n";


                //Stores results to be added to the ui
                ArrayList<String> endResults = new ArrayList<String>();

                //Loops through object results
                for (String s: visionResultsList)
                {
                    for (String currentFood: foodList)
                    {
                        if (s.contains(currentFood))
                        {
                            boolean contains = false;
                            for (String currentResult : endResults)
                            {
                                if (currentFood == currentResult)
                                {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains)
                            {
                                endResults.add(currentFood);
                            }

                        }
                    }
                }

                //Add string results
                String [] words = textResults.split(" ");

                for (String s: words)
                {
                    for (String currentFood: foodList)
                    {
                        if (s.contains(currentFood))
                        {
                            boolean contains = false;
                            for (String currentResult : endResults)
                            {
                                if (currentFood == currentResult)
                                {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains)
                            {
                                endResults.add(currentFood);
                            }

                        }
                    }
                }
                Format formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                Date added = c.getTime();
                c.add(Calendar.WEEK_OF_MONTH, 1);

                Date expiryDate=c.getTime();
                String expiryString=formatter.format(expiryDate);
                String addedString=formatter.format(added);

                for (String currentResult : endResults)
                {
                    itemList.add(new Items(currentResult,expiryString,"1",addedString));
                    db.insertFood(currentResult,expiryString,1);
                }

                Intent i = new Intent(Camera.this, Camera.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "");
                i.putExtras(bundle);
                overridePendingTransition(0,0);
                startActivity(i);

                finish();
                overridePendingTransition(0,0);
                return null;
            }

        }.execute();


        imageFile.delete();

    }

    void saveImage(String fileName, Bitmap img)
    {
        //Stores the image to a file on the phone

        try {
            File f = new File(Environment.getExternalStorageDirectory().toString(), fileName);
            FileOutputStream out = new FileOutputStream(f);

            img.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            saveToFile(e.toString());
        } catch (IOException ex)
        {
            saveToFile(ex.getMessage());
        }
        catch (NullPointerException ex)
        {
            saveToFile(ex.toString());
        }


    }

    void detectObjects(FirebaseVisionImage image)
    {
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();
        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        for (FirebaseVisionImageLabel label: labels) {
                            String text = label.getText();
                            visionResultsList.add(text);
                        }
                        numOfResponses++;
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception ex) {
                        // Task failed with an exception
                        // ...
                        numOfResponses++;
                    }
                });

    }

    void detectText(FirebaseVisionImage image)
    {
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudTextRecognizer();

        //Processes the image to find text
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                // Task completed successfully
                                // ...
                                String data = "Text recognition" + firebaseVisionText.getText();

                                //EditText e = (EditText) findViewById(R.id.itemListTextView);
                               // CharSequence newchars = e.getText() + data;
                                //e.setText(newchars);




                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception ex) {
                                        // Task failed with an exception
                                        // ...

                                        String data = "Could not find text:\n\n" + ex.toString();


                                    }
                                });

    }


    public static boolean saveToFile(String data){

        try {
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "Visontest.txt" ).mkdir();
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Visontest.txt");
            //if (!file.exists()) {
                file.createNewFile();
            //}
            FileOutputStream fileOutputStream = new FileOutputStream(file,false);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());

            return true;
        }  catch(FileNotFoundException ex) {
           // Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
           // Log.d(TAG, ex.getMessage());
        }
        return  false;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bitmap image = BitmapFactory.decodeFile(currentPhotoPath);
            segmentImage(image);
            //saveToFile("point b");
        }
    }




    public void sendNetworkDataTest(Bitmap img) throws IOException
    {
        //Connects to the server
        Socket s = new Socket("192.168.0.10",port);


        DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());

        //Stores the image as an array of bytes
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        //Sends the size of the image in bytes to the server
        outToServer.writeInt(stream.size());
        //Sends the image data to the server
        outToServer.write(byteArray);

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

    private void removeItem(String foodName, String dateAdded) {
        //removes the item from the table, pass food name and dateadded
        db.deleteFood(foodName, dateAdded);

    }

    public boolean increaseQuantity(String foodName, String expiry, int amount) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateString = formatter.format(date);
        List<DBPantry> pantryList;
        boolean found = false;

        pantryList = db1.fetchPantryAll();

        for (DBPantry tempPantry : pantryList) {
            if (tempPantry.getName().equals(foodName) && tempPantry.getDateExpiry().substring(0, 10).equals(expiry)) {
                DBPantry tempP = new DBPantry();
                tempP.setName(foodName);
                tempP.setDateExpiry(expiry);
                tempP.setAmount(tempPantry.getAmount() + amount);
                db1.updateFood(tempP, foodName, tempPantry.getDateAdded());
                found = true;
            }
        }
        if (!found) {
            db1.insertFood(foodName, expiry, amount);
        }
        return found;
    }

    public void openDialog() {
        cameraDialog camDialog = new cameraDialog();
        camDialog.show(getSupportFragmentManager(),"help");

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

