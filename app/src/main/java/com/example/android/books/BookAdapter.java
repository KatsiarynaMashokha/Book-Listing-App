package com.example.android.books;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by katsiarynamashokha on 7/24/16.
 */
public class BookAdapter extends ArrayAdapter<Book> {
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.title);
            holder.authorsTextView = (TextView) convertView.findViewById(R.id.author);
            holder.publisherTextView = (TextView) convertView.findViewById(R.id.publisher);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Book currentBook = getItem(position);

        holder.titleTextView.setText(currentBook.title);

        holder.authorsTextView.setText(currentBook.publisher);

        holder.publisherTextView.setText(currentBook.publisher);

        return convertView;
    }

    static class ViewHolder {
        private TextView titleTextView;
        private TextView authorsTextView;
        private TextView publisherTextView;
    }

}
