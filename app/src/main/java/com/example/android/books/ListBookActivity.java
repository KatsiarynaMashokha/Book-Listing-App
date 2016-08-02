package com.example.android.books;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by katsiarynamashokha on 7/24/16.
 */
public class ListBookActivity extends AppCompatActivity {
    public static final String LOG_TAG = ListBookActivity.class.getSimpleName();
    public  static final String KEY_FOR_BOOKS = "books";
    ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.v(LOG_TAG, "from onSaveInstanceState.");
            books = (ArrayList<Book>) savedInstanceState.getSerializable(KEY_FOR_BOOKS);

        }
        else {
            Log.v(LOG_TAG, "from Intent.");
            Intent intent = getIntent();
            books = (ArrayList<Book>) intent.getSerializableExtra(MainActivity.BOOKS_KEY);
        }
            setContentView(R.layout.book_activity);

            ListView bookListView = (ListView) findViewById(R.id.list);
            TextView emptyTextView = (TextView) findViewById(R.id.listview_book_empty);
            bookListView.setEmptyView(emptyTextView);

            final BookAdapter adapter = new BookAdapter(this, books);

            bookListView.setAdapter(adapter);

    }
    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putSerializable(KEY_FOR_BOOKS, books);

    }

}
