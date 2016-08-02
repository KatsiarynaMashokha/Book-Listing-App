package com.example.android.books;

import java.io.Serializable;

/**
 * Created by katsiarynamashokha on 7/23/16.
 */
public class Book implements Serializable {
    public final String title;
    public final String authors;
    public final String publisher;

    public Book(String title, String authors, String publisher) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
    }
}
