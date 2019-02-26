package com.example.pantrymate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddDialogue extends AppCompatDialogFragment {
    private EditText itemName;
    private EditText itemQuantity;
    private EditText itemExpiry;
    private addDialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_manual_edit,null);

        builder.setView(view)
                .setTitle("Add Item")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String foodName = itemName.getText().toString();
                        String foodQuantity= itemQuantity.getText().toString();
                        String foodExpiry= itemExpiry.getText().toString();
                        listener.addItems(foodName,foodQuantity,foodExpiry);
                    }
                });
        itemName=view.findViewById(R.id.addName);
        itemQuantity=view.findViewById(R.id.addQuantity);
        itemExpiry=view.findViewById(R.id.addExpiry);



        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener= (addDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Test");
        }
    }

    public interface addDialogListener{
        void addItems(String name, String quantity, String expiry);
    }
}

