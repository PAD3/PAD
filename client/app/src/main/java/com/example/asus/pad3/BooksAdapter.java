package com.example.asus.pad3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.asus.pad3.model.Book;
import com.example.asus.pad3.model.PayLoad;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Path;

public class BooksAdapter extends RecyclerView.Adapter< BooksAdapter.ViewHolder> {
    private List<PayLoad> contactsList = new ArrayList<>();
    private ItemClickChildDelete itemClickChildDelete;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phone;
        TextView year;
        Button deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            year = itemView.findViewById(R.id.yearOfBook);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }


    public  BooksAdapter(ItemClickChildDelete itemClickChildDeleteListner) {
        itemClickChildDelete = itemClickChildDeleteListner;
    }

    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_books, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(contactsList.get(position).getTitle() + contactsList.get(position).getAuthor());
        holder.phone.setText(contactsList.get(position).getDesc());
        holder.year.setText(String.valueOf(contactsList.get(position).getYear()));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = holder.getAdapterPosition();
                removeAt(newPosition,contactsList.get(position).getId());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mCommunicator.respond(position,contactsList.get(position).getId());
            }
        });
    }
    public void addItems(List<PayLoad> items) {
        this.contactsList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }


    public interface ItemClickChildDelete {
        void onChildClickDelete(String studentId);
    }

    public void removeAt(int position,String id){
        contactsList.remove(position);
        notifyItemRemoved(position);
        itemClickChildDelete.onChildClickDelete(id);
        notifyItemRangeChanged(position, contactsList.size());
    }

    public void swap(List<PayLoad> datas)
    {
        if(datas == null || datas.size()==0)
            return;
        if (contactsList != null && contactsList.size()>0)
            contactsList.clear();
        contactsList.addAll(datas);
        notifyDataSetChanged();
    }
}
