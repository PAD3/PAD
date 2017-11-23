package com.example.asus.pad3;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.asus.pad3.model.Student;

import java.util.List;

/**
 * Created by ASUS on 13.11.2017.
 */

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private List<Student> contactsList;
    private ItemClickChild mListener;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phone;
        TextView year;
        Button deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            year = (TextView) itemView.findViewById(R.id.year);
            deleteButton = (Button)itemView.findViewById(R.id.deleteButton);
        }
    }


    public StudentAdapter(List<Student> contactsList, Activity activity) {
        this.contactsList = contactsList;
        mListener = (ItemClickChild) activity;
    }

    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_students, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(contactsList.get(position).getName());
        holder.phone.setText(contactsList.get(position).getPhone());
        holder.year.setText(contactsList.get(position).getYear());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = holder.getAdapterPosition();
                removeAt(newPosition);
            }
        });
        final String name = contactsList.get(position).getName();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onChildClick(name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public interface ItemClickChild{
        void onChildClick(String position);
    }
    public void removeAt(int position) {

        contactsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,  contactsList.size());
    }
}
