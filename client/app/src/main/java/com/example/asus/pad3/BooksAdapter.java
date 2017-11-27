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

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter< BooksAdapter.ViewHolder> {
    private List<PayLoad> contactsList;
    private FragmentCommunication mCommunicator;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phone;
        TextView year;
        Button deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            year = (TextView) itemView.findViewById(R.id.year1);
            deleteButton = (Button)itemView.findViewById(R.id.deleteButton);
        }
    }


    public  BooksAdapter(List<PayLoad> contactsList) {
        this.contactsList = contactsList;
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
        holder.name.setText(contactsList.get(position).getTitle() + contactsList.get(position).getAuthor() + String.valueOf(contactsList.get(position).getYear()));
        holder.phone.setText(contactsList.get(position).getDesc());
     //   holder.year.setText(contactsList.get(position).getYear());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = holder.getAdapterPosition();
                removeAt(newPosition);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mCommunicator.respond(position,contactsList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public void removeAt(int position) {

        contactsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,  contactsList.size());
    }
}
