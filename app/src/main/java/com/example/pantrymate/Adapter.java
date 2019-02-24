package com.example.pantrymate;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {
    private ArrayList<Items> nList;
    public static class viewHolder extends RecyclerView.ViewHolder{
        public TextView nTextView1;
        public TextView nTextView2;
        public TextView nTextView3;

        public viewHolder(View itemView){
            super(itemView);
            nTextView1= itemView.findViewById(R.id.Text1);
            nTextView2= itemView.findViewById(R.id.Text2);
            nTextView3= itemView.findViewById(R.id.Text3);
        }
    }

    public Adapter(ArrayList<Items> itemList){
        nList=itemList;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        viewHolder itemViewHolder = new viewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(viewHolder holder,int position)
    {
        Items currentItem= nList.get(position);
        holder.nTextView1.setText(currentItem.getText1());
        holder.nTextView2.setText(currentItem.getText2());
        holder.nTextView3.setText(currentItem.getText3());
    }

    @Override
    public int getItemCount() {
        return nList.size();
    }

}


