package com.example.asus.pad3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.asus.pad3.model.Book;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BooksActivity extends AppCompatActivity {
    @BindView(R.id.books_recycler_view)
    RecyclerView booksList;
    List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        init();
    }

    public void init() {
        ButterKnife.bind(this);
        books = new ArrayList<>();
        books.add(new Book("Mertvie Dusi"));
        books.add(new Book("Mertvie Dusi"));
        books.add(new Book("Mertvie Dusi"));
        books.add(new Book("Mertvie Dusi"));
        books.add(new Book("Mertvie Dusi"));
        BooksAdapter categoryAdapter = new BooksAdapter(books);
        booksList.setLayoutManager(new LinearLayoutManager(this));
        booksList.setAdapter(categoryAdapter);
    }
}
