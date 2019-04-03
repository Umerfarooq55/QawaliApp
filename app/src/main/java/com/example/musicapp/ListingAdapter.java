package com.example.musicapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.musicapp.R;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private List<Music> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private  Context  c;

    // data is passed into the constructor
    ListingAdapter(Context context, List<Music> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.c=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listing_row_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Music data = mData.get(position);
        holder.song.setText(data.getTitle());

// Create an ArrayAdapter using the string array and a default spinner layout

        if(position<10) {
            holder.postion.setText("0" + position+".");
        }else{
            holder.postion.setText(""+position+".");
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView song , postion;
        Spinner spinner;
        ViewHolder(View itemView) {
            super(itemView);
            song = itemView.findViewById(R.id.song);
            postion = itemView.findViewById(R.id.postion);
            spinner = (Spinner)itemView.findViewById(R.id.spinner);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Music getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}