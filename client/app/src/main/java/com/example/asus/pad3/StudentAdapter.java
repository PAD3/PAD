package com.example.asus.pad3;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.pad3.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private List<Student> contactsList = new ArrayList<>();
    private ItemClickChild itemClickChild;
    private ItemClickChildDelete itemClickChildDelete;
    private ItemClickChildChange itemClickChildChange;
    Context context;
    boolean isCheked = false;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phone;
        TextView year;
        Button deleteButton;
        CheckBox checkBox;
        Button editButton;


        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            checkBox = itemView.findViewById(R.id.checkBox);
            phone = itemView.findViewById(R.id.phone);
            year = itemView.findViewById(R.id.year);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.buttonEditt);
        }
    }


    public StudentAdapter(Context activity, ItemClickChild itemClickChildListener,ItemClickChildDelete itemClickChildDeleteListner,
                          ItemClickChildChange itemClickChildChange) {
        itemClickChild = itemClickChildListener;
        itemClickChildDelete = itemClickChildDeleteListner;
        this.itemClickChildChange = itemClickChildChange;
        context = activity;

    }

    public void addItems(List<Student> items) {
        this.contactsList.addAll(items);
        notifyDataSetChanged();
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
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                   isCheked = isChecked;
                }}
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCheked) {
                    itemClickChildChange.onChildClickChange(contactsList.get(position).getId(),position);
                    notifyItemRangeChanged(position, contactsList.size());
                    Toast.makeText(context,contactsList.get(position).getId(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = holder.getAdapterPosition();
                removeAt(newPosition,contactsList.get(position).getId());
            }
        });
        final String name = contactsList.get(position).getName();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickChild.onChildClick(contactsList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public interface ItemClickChild {
        void onChildClick(String studentId);
    }

    public interface ItemClickChildDelete {
        void onChildClickDelete(String studentId);
    }

    public interface ItemClickChildChange {
        void onChildClickChange(String studentId,int posit);
    }

    public void removeAt(int position,String id){
        contactsList.remove(position);
        notifyItemRemoved(position);
        itemClickChildDelete.onChildClickDelete(id);
        notifyItemRangeChanged(position, contactsList.size());
    }
    public void swap(List<Student> datas)
    {
        if(datas == null || datas.size()==0)
            return;
        if (contactsList != null && contactsList.size()>0)
            contactsList.clear();
        contactsList.addAll(datas);
        notifyDataSetChanged();
    }
}
