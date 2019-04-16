package com.example.pantrymate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatDialogFragment;
import database.DatabaseHelper;

public class matchesDialog extends AppCompatDialogFragment {

    private DatabaseHelper db;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_matches_dialog, null);

        db = new DatabaseHelper(getContext(), "pantry.db");
        String itemName=getArguments().getString("name");


        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addItem(itemName);
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });




        return builder.create();
    }

    public void addItem(String itemName)
    {
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String dateString=formatter.format(date);


        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.WEEK_OF_MONTH, 1);

        Date expiryDate=c.getTime();
        String expiryString=formatter.format(expiryDate);

        db.insertFood(itemName,expiryString,1);
    }
}
