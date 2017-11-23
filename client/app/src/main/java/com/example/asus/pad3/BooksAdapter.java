package com.example.asus.pad3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.pad3.model.Book;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter< BooksAdapter.ViewHolder> {
    private List<Book> contactsList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView book;

        ViewHolder(View itemView) {
            super(itemView);
            book = (TextView) itemView.findViewById(R.id.book);
        }
    }


    public  BooksAdapter(List<Book> contactsList) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.book.setText(contactsList.get(position).getBook());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    mListener.onChildClick(name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }
}
